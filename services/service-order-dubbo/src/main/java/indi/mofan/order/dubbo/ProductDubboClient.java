package indi.mofan.order.dubbo;

import indi.mofan.product.bean.Product;
import indi.mofan.product.dubbo.service.IProductDubboService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.Method;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.dubbo.rpc.RpcException;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 产品服务 - Dubbo 消費者
 * 涉用Dubbo原生的超时、重试、讅转等機制呉不使用Sentinel
 *
 * @author xiongweisu
 * @date 2025/3/23
 */
@Slf4j
@Component
public class ProductDubboClient {

    /**
     * 在nacos这样配置没有生效，先在项目中进行硬编码
     * Data ID：indi.mofan.product.dubbo.service.IProductDubboService.configurators
     * Group：product
     * 配置内容：
     * configVersion: v3.0
     * scope: service
     * key: indi.mofan.product.dubbo.service.IProductDubboService
     * enabled: true
     * configs:
     * - side: provider
     * parameters:
     * timeout: 3000
     * retries: 2
     * methods:
     * - name: getProductById
     * parameters:
     * timeout: 2000
     * - name: listAllProducts
     * parameters:
     * timeout: 5000
     * - name: simulateTimeout
     * parameters:
     * timeout: 2600
     * - side: consumer
     * parameters:
     * timeout: 3000
     * retries: 3
     * methods:
     * - name: getProductById
     * parameters:
     * timeout: 2000
     * retries: 2
     * - name: listAllProducts
     * parameters:
     * timeout: 5000
     * retries: 1
     * - name: simulateTimeout
     * parameters:
     * timeout: 2400
     * retries: 2
     * <p>
     * service-order-dubbo.configurators
     * 以下配置同样不生效
     * configVersion: v3.0
     * scope: application
     * key: service-order-dubbo
     * enabled: true
     * configs:
     * - side: consumer
     * applications:
     * - service-order-dubbo
     * services:
     * - indi.mofan.product.dubbo.service.IProductDubboService
     * parameters:
     * timeout: 3200
     * methods:
     * - name: getProductById
     * parameters:
     * timeout: 2000
     * retries: 2
     * - name: listAllProducts
     * parameters:
     * timeout: 5000
     * retries: 1
     * - name: simulateTimeout
     * parameters:
     * timeout: 2600
     * retries: 2
     * <p>
     * 当前服务生效的NACOS动态配置:
     * configVersion: v3.0
     * scope: application
     * key: service-order-dubbo
     * enabled: true
     * configs:
     * - side: consumer
     * parameters:
     * timeout: 3200
     */
    @DubboReference(
            version = "1.0.0",
            group = "product",
            retries = 0,  // 禁用重试，避免重试干扰并发控制测试
            // 消费端并发控制：限制每个消费端的并发调用数为3
            actives = 3,
            // 添加更多配置确保并发控制生效
            check = false,
            lazy = true,
            methods = {
                    @Method(name = "simulateTimeout", timeout = 2400, retries = 2),// 在nacos全局配置了超时时间后会覆盖此处的配置
                    @Method(name = "listAllProducts", timeout = 5000, retries = 1),
                    // 方法级别的消费端并发控制：限制testConcurrencyControl方法的并发调用数为3
                    @Method(name = "testConcurrencyControl", actives = 3, retries = 0),
                    // 最小并发数负载均衡配置
                    @Method(name = "testLeastActiveLoadBalance", loadbalance = "leastactive")
            }
    )
    private IProductDubboService productDubboService;

    /**
     * 获取产品
     * 策略: failover 失败自动转憻ubbo幻想接点，查被包接戈预预錄
     */
    public Product getProduct(Long productId) {
        try {
            log.info("============ Dubbo RPC Call Start ============");
            log.info("通过Dubbo调用获取产品，ID: {}", productId);
            log.info("消费端配置: version=1.0.0, group=product, timeout=3000");
            Product product = productDubboService.getProductById(productId);
            log.info("Dubbo调用成功，返回产品: {}", product);
            log.info("============ Dubbo RPC Call End (Success) ============");
            return product;
        } catch (Exception e) {
            log.error("============ Dubbo RPC Call End (Failed) ============");
            log.error("获取产品失败，ID: {}", productId, e);
            log.warn("返回降级值. 故障: {}", e.getMessage());
            // 降级处理：返回默认值
            Product product = new Product();
            product.setId(productId);
            product.setProductName("产品模拟值");
            product.setPrice(new java.math.BigDecimal("0.00"));
            return product;
        }
    }

    /**
     * 获取所有产品
     */
    public List<Product> getAllProducts() {
        try {
            log.info("通过Dubbo调用获取所有产品");
            return productDubboService.listAllProducts();
        } catch (Exception e) {
            log.warn("获取产品清娕下转，返回空列表. 故障: {}", e.getMessage());
            // 降级挺擦变空列表
            return new ArrayList<>();
        }
    }

    /**
     * 批量查询产品
     */
    public List<Product> queryProducts(List<Long> productIds) {
        try {
            log.info("通过Dubbo调用批量查询产品，IDs: {}", productIds);
            return productDubboService.queryProducts(productIds);
        } catch (Exception e) {
            log.warn("批量查询产品失败，返回空列表. 故障: {}", e.getMessage());
            // 降级頴处理∣8返回空列表
            return Collections.emptyList();
        }
    }

    /**
     * 模拟超时调用
     */
    public Product getProductWithTimeout(Long id, long sleepTime) {
        try {
            log.info("通过Dubbo调用模拟超时，ID: {}, 休眠: {}ms", id, sleepTime);
            return productDubboService.simulateTimeout(id, sleepTime);
        } catch (Exception e) {
            log.error("Dubbo 超时调用失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 模拟异常调用
     */
    public Product getProductWithException(Long id) {
        try {
            log.info("通过Dubbo调用模拟异常，ID: {}", id);
            return productDubboService.simulateException(id);
        } catch (Exception e) {
            log.error("Dubbo 异常调用捕获: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 异步调用演示
     */
    public CompletableFuture<Product> getProductAsync(Long id) {
        log.info("开始 Dubbo 异步调用，ID: {}", id);
        return CompletableFuture.supplyAsync(() -> {
            log.info("异步线程执行 Dubbo 调用...");
            return productDubboService.getProductById(id);
        });
    }

    /**
     * 区域优先测试
     */
    public Product getProductRegionDetails(Long id, String clientRegion) {
        try {
            log.info("Dubbo 区域优先测试，ID: {}, 模拟客户端区域: {}", id, clientRegion);
            // 模拟设置客户端区域，这通常由部署环境决定，但也可以通过 RpcContext 传递
            // 注意：真正的同区域路由需要配合 Dubbo Admin 的路由规则
            // RpcContext.getClientAttachment().setAttachment("region", clientRegion);
            // 使用 Dubbo 标签路由 (Tag Routing) 实现区域优先
            RpcContext.getClientAttachment().setAttachment(org.apache.dubbo.common.constants.CommonConstants.TAG_KEY,
                    clientRegion);
            return productDubboService.getProductRegionDetails(id);
        } catch (Exception e) {
            log.error("Dubbo 区域优先测试失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 服务端并发控制测试（后端多线程实现）
     */
    /**
     * 测试Dubbo并发控制（同时测试消费端actives和服务端executes限流）
     * 使用多线程同时发起多个请求，以触发消费端和服务端的并发控制
     *
     * @param concurrentCount 并发数
     * @param sleepTime       每个请求的休眠时间（毫秒）
     * @return 测试结果
     */
    public Map<String, Object> testConcurrencyControlWithThreads(Integer concurrentCount, Long sleepTime) {
        log.info("开始Dubbo并发控制测试（消费端actives+服务端executes），并发数: {}, 休眠时间: {}ms", concurrentCount, sleepTime);

        Map<String, Object> result = new HashMap<>();
        List<CompletableFuture<Map<String, Object>>> futures = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        AtomicInteger serverLimitedCount = new AtomicInteger(0); // 记录被服务端executes限制的请求数
        AtomicInteger clientLimitedCount = new AtomicInteger(0); // 记录被消费端actives限制的请求数
        List<Long> responseTimes = new ArrayList<>();

        // 创建线程池，使用更大的线程池确保足够的并发压力
        ExecutorService executor = Executors.newFixedThreadPool(Math.max(concurrentCount, 20));

        try {
            // 使用CountDownLatch确保所有线程同时开始
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch readyLatch = new CountDownLatch(concurrentCount);

            // 使用多线程同时发起多个请求
            for (int i = 0; i < concurrentCount; i++) {
                final int requestId = i;
                CompletableFuture<Map<String, Object>> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        // 线程准备就绪
                        readyLatch.countDown();
                        // 等待所有线程准备就绪后同时开始
                        startLatch.await();

                        log.debug("线程 {} 开始发起Dubbo请求", requestId);
                        long requestStart = System.currentTimeMillis();

                        // 方法1：使用原始的Dubbo引用
                        Map<String, Object> response = productDubboService.testConcurrencyControl(sleepTime);

                        long requestEnd = System.currentTimeMillis();
                        long responseTime = requestEnd - requestStart;

                        log.debug("线程 {} 完成Dubbo请求，响应时间: {}ms", requestId, responseTime);

                        synchronized (responseTimes) {
                            responseTimes.add(responseTime);
                        }

                        Map<String, Object> requestResult = new HashMap<>();
                        requestResult.put("requestId", requestId);
                        requestResult.put("success", true);
                        requestResult.put("responseTime", responseTime);
                        requestResult.put("message", "请求成功,结果:" + response.toString());

                        successCount.incrementAndGet();
                        return requestResult;
                    } catch (Exception e) {
                        log.warn("线程 {} 请求失败: {}", requestId, e.getMessage());

                        Map<String, Object> requestResult = new HashMap<>();
                        requestResult.put("requestId", requestId);
                        requestResult.put("success", false);
                        requestResult.put("responseTime", 0L);
                        requestResult.put("message", e.getMessage());

                        // 判断限流类型
                        if (e instanceof RpcException && e.getMessage() != null) {
                            // 服务端限流：服务端线程池满载
                            if (e.getMessage().contains("The service using threads greater than")) {
                                log.warn("线程 {} 触发服务端限流", requestId);
                                serverLimitedCount.incrementAndGet();
                            }
                            // 消费端限流：消费端并发调用数超过actives限制
                            else if (e.getMessage().contains("actives") ||
                                    e.getMessage().contains("rejected") ||
                                    e.getMessage().contains("Failed to invoke remote") ||
                                    e.getMessage().contains("No provider") ||
                                    e.getMessage().contains("timeout") ||
                                    e.getMessage().contains("Thread pool is exhausted") ||
                                    e.getMessage().contains("Server side") ||
                                    e.getMessage().contains("Too many active")) {
                                log.warn("线程 {} 触发消费端限流: {}", requestId, e.getMessage());
                                clientLimitedCount.incrementAndGet();
                            }
                            // 其他RpcException
                            else {
                                log.warn("线程 {} 发生其他RpcException: {}", requestId, e.getMessage());
                                failCount.incrementAndGet();
                            }
                        } else {
                            // 非RpcException
                            log.warn("线程 {} 发生非RpcException: {}", requestId, e.getMessage());
                            failCount.incrementAndGet();
                        }

                        return requestResult;
                    }
                }, executor);

                futures.add(future);
            }

            // 等待所有线程准备就绪
            readyLatch.await();
            // 同时开始所有请求
            startLatch.countDown();
            log.info("所有线程同时开始发起Dubbo请求");

            // 等待所有请求完成
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));

            // 设置超时时间
            allFutures.get(60, TimeUnit.SECONDS);

            // 计算统计信息
            result.put("successCount", successCount.get());
            result.put("failCount", failCount.get());
            result.put("serverLimitedCount", serverLimitedCount.get());
            result.put("clientLimitedCount", clientLimitedCount.get());
            result.put("totalCount", concurrentCount);

            if (!responseTimes.isEmpty()) {
                long minTime = responseTimes.stream().mapToLong(Long::longValue).min().orElse(0);
                long maxTime = responseTimes.stream().mapToLong(Long::longValue).max().orElse(0);
                double avgTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0);

                result.put("minResponseTime", minTime);
                result.put("maxResponseTime", maxTime);
                result.put("avgResponseTime", Math.round(avgTime));
            }

            // 获取所有请求的详细结果
            List<Map<String, Object>> details = futures.stream().map(future -> {
                try {
                    return future.get();
                } catch (Exception e) {
                    Map<String, Object> errorResult = new HashMap<>();
                    errorResult.put("success", false);
                    errorResult.put("message", "获取结果异常: " + e.getMessage());
                    return errorResult;
                }
            }).collect(Collectors.toList());

            result.put("details", details);

            // 关闭线程池
            executor.shutdown();
            try {
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }

            return result;
        } catch (Exception e) {
            log.error("消费端并发控制测试（后端多线程）失败: {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * 消费端并发控制测试
     */
    public Map<String, Object> testActivesControl() {
        try {
            return productDubboService.testActivesControl();
        } catch (Exception e) {
            log.error("消费端并发控制测试失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 最小并发数负载均衡测试
     */
    public Map<String, Object> testLeastActiveLoadBalance() {
        try {
            log.info("开始最小并发数负载均衡测试");
            return productDubboService.testLeastActiveLoadBalance();
        } catch (Exception e) {
            log.error("最小并发数负载均衡测试失败: {}", e.getMessage());
            throw e;
        }
    }
}
