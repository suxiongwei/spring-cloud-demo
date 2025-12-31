package indi.mofan.order.dubbo;

import indi.mofan.product.bean.Product;
import indi.mofan.product.dubbo.service.IProductDubboService;
import indi.mofan.product.dubbo.service.IVersionGroupService;
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
            protocol = "dubbo",
            retries = 0,
//            actives = 3,
            check = false,
            lazy = false,
            timeout = 5000,
            methods = {
                    @Method(name = "simulateTimeout", timeout = 2400, retries = 2),
                    @Method(name = "listAllProducts", timeout = 5000, retries = 1),
                    @Method(name = "testConcurrencyControlV1", actives = 3, retries = 0, timeout = 5000),
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
    public Map<String, Object> testConcurrencyControlWithThreads(Integer concurrentCount, Long sleepTime, String type) {
        log.info("开始Dubbo并发控制测试（消费端actives+服务端executes），并发数: {}, 休眠时间: {}ms", concurrentCount, sleepTime);
        log.info("消费端actives配置: 3，服务端executes配置: 需要在服务端配置");
        log.info("Dubbo引用: {}", productDubboService.getClass().getName());
        log.info("Dubbo引用Hash: {}", System.identityHashCode(productDubboService));
        log.warn("注意：Nacos中的service-order-dubbo.configurators配置会覆盖@DubboReference注解中的actives配置");

        Map<String, Object> result = new HashMap<>();
        List<CompletableFuture<Map<String, Object>>> futures = new ArrayList<>();
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        AtomicInteger serverLimitedCount = new AtomicInteger(0);
        AtomicInteger clientLimitedCount = new AtomicInteger(0);
        List<Long> responseTimes = new ArrayList<>();
        List<Long> startTimes = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(concurrentCount);

        try {
            CountDownLatch readyLatch = new CountDownLatch(concurrentCount);
            CountDownLatch startLatch = new CountDownLatch(1);

            for (int i = 0; i < concurrentCount; i++) {
                final int requestId = i;
                CompletableFuture<Map<String, Object>> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        readyLatch.countDown();
                        startLatch.await();

                        long requestStart = System.currentTimeMillis();
                        synchronized (startTimes) {
                            startTimes.add(requestStart);
                        }

                        log.info("线程 {} 开始发起Dubbo请求，时间: {}, Dubbo引用: {}", requestId, requestStart, System.identityHashCode(productDubboService));

                        Map<String, Object> response;
                        if (type.equals("executes")){
                            response = productDubboService.testConcurrencyControlV2(sleepTime);
                        }else {
                            response = productDubboService.testConcurrencyControlV1(sleepTime);
                        }

                        long requestEnd = System.currentTimeMillis();
                        long responseTime = requestEnd - requestStart;

                        log.info("线程 {} 完成Dubbo请求，响应时间: {}ms，结束时间: {}", requestId, responseTime, requestEnd);

                        synchronized (responseTimes) {
                            responseTimes.add(responseTime);
                        }

                        Map<String, Object> requestResult = new HashMap<>();
                        requestResult.put("requestId", requestId);
                        requestResult.put("success", true);
                        requestResult.put("responseTime", responseTime);
                        requestResult.put("startTime", requestStart);
                        requestResult.put("endTime", requestEnd);
                        requestResult.put("message", "请求成功,结果:" + response.toString());

                        successCount.incrementAndGet();
                        return requestResult;
                    } catch (Exception e) {
                        log.error("线程 {} 请求失败: {}", requestId, e.getMessage(), e);

                        Map<String, Object> requestResult = new HashMap<>();
                        requestResult.put("requestId", requestId);
                        requestResult.put("success", false);
                        requestResult.put("responseTime", 0L);
                        requestResult.put("message", e.getMessage());

                        if (e instanceof RpcException && e.getMessage() != null) {
                            String errorMsg = e.getMessage();
                            
                            if (errorMsg.contains("The service using threads greater than") ||
                                errorMsg.contains("Server side") ||
                                errorMsg.contains("Thread pool is exhausted")) {
                                serverLimitedCount.incrementAndGet();
                                log.info("线程 {} 被服务端限流", requestId);
                            }
                            else if (errorMsg.contains("actives") ||
                                    errorMsg.contains("rejected") ||
                                    errorMsg.contains("Failed to invoke remote") ||
                                    errorMsg.contains("No provider") ||
                                    errorMsg.contains("timeout") ||
                                    errorMsg.contains("Too many active") ||
                                    errorMsg.contains("Waiting concurrent invoke")) {
                                clientLimitedCount.incrementAndGet();
                                log.info("线程 {} 被消费端限流: {}", requestId, errorMsg);
                            }
                            else {
                                failCount.incrementAndGet();
                                log.info("线程 {} 其他RpcException: {}", requestId, errorMsg);
                            }
                        } else {
                            failCount.incrementAndGet();
                            log.info("线程 {} 非RpcException: {}", requestId, e.getClass().getName());
                        }

                        return requestResult;
                    }
                }, executor);

                futures.add(future);
            }

            readyLatch.await();
            log.info("所有线程已准备就绪，同时开始Dubbo请求");
            startLatch.countDown();

            CompletableFuture<Void> allFutures = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            allFutures.get(120, TimeUnit.SECONDS);
            
            log.info("所有请求完成");

            // 计算统计信息
            result.put("successCount", successCount.get());
            result.put("failCount", failCount.get());
            result.put("serverLimitedCount", serverLimitedCount.get());
            result.put("clientLimitedCount", clientLimitedCount.get());
            result.put("limitedCount", serverLimitedCount.get() + clientLimitedCount.get());
            result.put("totalCount", concurrentCount);

            if (!responseTimes.isEmpty()) {
                long minTime = responseTimes.stream().mapToLong(Long::longValue).min().orElse(0);
                long maxTime = responseTimes.stream().mapToLong(Long::longValue).max().orElse(0);
                double avgTime = responseTimes.stream().mapToLong(Long::longValue).average().orElse(0);

                result.put("minResponseTime", minTime);
                result.put("maxResponseTime", maxTime);
                result.put("avgResponseTime", Math.round(avgTime));
                
                int actives = 3;
                int expectedBatches = (int) Math.ceil((double) concurrentCount / actives);
                long expectedMaxTime = expectedBatches * sleepTime;
                
                result.put("actives", actives);
                result.put("expectedBatches", expectedBatches);
                result.put("expectedMaxTime", expectedMaxTime);
                
                List<Long> sortedResponseTimes = responseTimes.stream().sorted().collect(Collectors.toList());
                result.put("sortedResponseTimes", sortedResponseTimes);
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

    /**
     * Filter拦截测试
     */
    public Map<String, Object> testFilter(String message) {
        try {
            log.info("开始Filter拦截测试，消息: {}", message);
            return productDubboService.testFilter(message);
        } catch (Exception e) {
            log.error("Filter拦截测试失败: {}", e.getMessage());
            throw e;
        }
    }

    @DubboReference(version = "1.0.0", group = "default", check = false)
    private IVersionGroupService versionGroupServiceV1Default;

    @DubboReference(version = "2.0.0", group = "default", check = false)
    private IVersionGroupService versionGroupServiceV2Default;

    @DubboReference(version = "1.0.0", group = "groupA", check = false)
    private IVersionGroupService versionGroupServiceV1GroupA;

    @DubboReference(version = "1.0.0", group = "groupB", check = false)
    private IVersionGroupService versionGroupServiceV1GroupB;

    @DubboReference(version = "2.0.0", group = "groupA", check = false)
    private IVersionGroupService versionGroupServiceV2GroupA;

    /**
     * 分组聚合 - 聚合版本 1.0.0 所有分组的菜单项
     * 使用 group="*" 来聚合版本 1.0.0 的所有分组
     * 
     * 临时禁用：Dubbo 3.2.15 版本使用 group="*" 和 merger="true" 会导致 ScopeClusterInvoker NPE
     */
//    @DubboReference(version = "1.0.0", group = "*", check = false, merger = "true")
//    private IVersionGroupService versionGroupServiceMergerV1;

    /**
     * 分组聚合 - 聚合版本 2.0.0 所有分组的菜单项
     * 使用 group="*" 来聚合版本 2.0.0 的所有分组
     * 
     * 临时禁用：Dubbo 3.2.15 版本使用 group="*" 和 merger="true" 会导致 ScopeClusterInvoker NPE
     */
//    @DubboReference(version = "2.0.0", group = "*", check = false, merger = "true")
//    private IVersionGroupService versionGroupServiceMergerV2;

    /**
     * 测试版本 1.0.0 默认分组
     */
    public Map<String, Object> testVersion1Default(String name) {
        try {
            log.info("调用版本 1.0.0 默认分组服务，name: {}", name);
            return versionGroupServiceV1Default.sayHello(name);
        } catch (Exception e) {
            log.error("版本 1.0.0 默认分组调用失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 测试版本 2.0.0 默认分组
     */
    public Map<String, Object> testVersion2Default(String name) {
        try {
            log.info("调用版本 2.0.0 默认分组服务，name: {}", name);
            return versionGroupServiceV2Default.sayHello(name);
        } catch (Exception e) {
            log.error("版本 2.0.0 默认分组调用失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 测试版本 1.0.0 分组 A
     */
    public Map<String, Object> testVersion1GroupA(String name) {
        try {
            log.info("调用版本 1.0.0 分组 A 服务，name: {}", name);
            return versionGroupServiceV1GroupA.sayHello(name);
        } catch (Exception e) {
            log.error("版本 1.0.0 分组 A 调用失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 测试版本 1.0.0 分组 B
     */
    public Map<String, Object> testVersion1GroupB(String name) {
        try {
            log.info("调用版本 1.0.0 分组 B 服务，name: {}", name);
            return versionGroupServiceV1GroupB.sayHello(name);
        } catch (Exception e) {
            log.error("版本 1.0.0 分组 B 调用失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 测试版本 2.0.0 分组 A
     */
    public Map<String, Object> testVersion2GroupA(String name) {
        try {
            log.info("调用版本 2.0.0 分组 A 服务，name: {}", name);
            return versionGroupServiceV2GroupA.sayHello(name);
        } catch (Exception e) {
            log.error("版本 2.0.0 分组 A 调用失败: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 获取所有版本和分组的服务信息
     */
    public Map<String, Object> getAllVersionGroupServicesInfo() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            result.put("v1-default", versionGroupServiceV1Default.getServerInfo());
            result.put("v2-default", versionGroupServiceV2Default.getServerInfo());
            result.put("v1-groupA", versionGroupServiceV1GroupA.getServerInfo());
            result.put("v1-groupB", versionGroupServiceV1GroupB.getServerInfo());
            result.put("v2-groupA", versionGroupServiceV2GroupA.getServerInfo());
            
            log.info("成功获取所有版本和分组的服务信息");
        } catch (Exception e) {
            log.error("获取服务信息失败: {}", e.getMessage());
            throw e;
        }
        
        return result;
    }

    /**
     * 测试分组聚合 - 聚合所有版本和分组的菜单项
     * 手动聚合各个分组的结果（替代 group="*" 和 merger="true"）
     * 注意：由于 Dubbo 3.2.15 版本的 bug，无法使用 group="*" 和 merger="true"，
     *       因此改为手动调用各个分组的服务并聚合结果
     */
    public Map<String, Object> testGroupMerger() {
        try {
            log.info("调用分组聚合服务 - 手动聚合所有版本和分组的菜单项");
            
            List<Map<String, Object>> mergedMenuItems = new ArrayList<>();
            
            try {
                List<Map<String, Object>> v1DefaultItems = versionGroupServiceV1Default.getMenuItems();
                if (v1DefaultItems != null) {
                    mergedMenuItems.addAll(v1DefaultItems);
                    log.info("版本 1.0.0 default 分组获取了 {} 个菜单项", v1DefaultItems.size());
                }
            } catch (Exception e) {
                log.warn("版本 1.0.0 default 分组调用失败: {}", e.getMessage());
            }
            
            try {
                List<Map<String, Object>> v1GroupAItems = versionGroupServiceV1GroupA.getMenuItems();
                if (v1GroupAItems != null) {
                    mergedMenuItems.addAll(v1GroupAItems);
                    log.info("版本 1.0.0 groupA 分组获取了 {} 个菜单项", v1GroupAItems.size());
                }
            } catch (Exception e) {
                log.warn("版本 1.0.0 groupA 分组调用失败: {}", e.getMessage());
            }
            
            try {
                List<Map<String, Object>> v1GroupBItems = versionGroupServiceV1GroupB.getMenuItems();
                if (v1GroupBItems != null) {
                    mergedMenuItems.addAll(v1GroupBItems);
                    log.info("版本 1.0.0 groupB 分组获取了 {} 个菜单项", v1GroupBItems.size());
                }
            } catch (Exception e) {
                log.warn("版本 1.0.0 groupB 分组调用失败: {}", e.getMessage());
            }
            
            try {
                List<Map<String, Object>> v2DefaultItems = versionGroupServiceV2Default.getMenuItems();
                if (v2DefaultItems != null) {
                    mergedMenuItems.addAll(v2DefaultItems);
                    log.info("版本 2.0.0 default 分组获取了 {} 个菜单项", v2DefaultItems.size());
                }
            } catch (Exception e) {
                log.warn("版本 2.0.0 default 分组调用失败: {}", e.getMessage());
            }
            
            try {
                List<Map<String, Object>> v2GroupAItems = versionGroupServiceV2GroupA.getMenuItems();
                if (v2GroupAItems != null) {
                    mergedMenuItems.addAll(v2GroupAItems);
                    log.info("版本 2.0.0 groupA 分组获取了 {} 个菜单项", v2GroupAItems.size());
                }
            } catch (Exception e) {
                log.warn("版本 2.0.0 groupA 分组调用失败: {}", e.getMessage());
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("mergedMenuItems", mergedMenuItems);
            result.put("totalItems", mergedMenuItems.size());
            result.put("description", "这是通过手动聚合各个分组服务获取的所有版本和分组的菜单项");
            
            log.info("分组聚合成功，共聚合 {} 个菜单项", mergedMenuItems.size());
            
            return result;
        } catch (Exception e) {
            log.error("分组聚合调用失败: {}", e.getMessage());
            throw e;
        }
    }
}
