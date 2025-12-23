package indi.mofan.product.service.impl;

import indi.mofan.product.bean.Product;
import indi.mofan.product.dubbo.service.IProductDubboService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.config.annotation.Method;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 产品服务 Dubbo 实现
 * 
 * @author xiongweisu
 * @date 2025/3/23
 */
@Slf4j
@DubboService(
    version = "1.0.0", 
    group = "product", 
    retries = 2,
    // 服务端并发控制：限制整个服务的最大并发执行数为10
    executes = 3,
    methods = {
        // 方法级别的并发控制
        @Method(name = "testConcurrencyControl", executes = 5)
    }
)
public class ProductDubboServiceImpl implements IProductDubboService {

    /**
     * 模拟产品数据库
     */
    private static final List<Product> PRODUCTS = Arrays.asList(
            new Product(1L, new BigDecimal("110"), "产品名", 10),
            new Product(2L, new BigDecimal("1110"), "产品名1", 11),
            new Product(3L, new BigDecimal("113210"), "产品名2", 12),
            new Product(4L, new BigDecimal("22"), "产品名3", 13),
            new Product(5L, new BigDecimal("323"), "产品名4", 14));

    // 用于跟踪当前并发请求数
    private final AtomicInteger currentConcurrency = new AtomicInteger(0);
    // 用于记录请求开始时间
    private final Map<String, Long> requestStartTimes = new ConcurrentHashMap<>();
    // 用于跟踪每个服务实例的活跃请求数
    private final AtomicInteger activeRequests = new AtomicInteger(0);

    @Override
    public Product getProductById(Long id) {
        log.info("Dubbo Service: 查询产品，ID: {}", id);
        return PRODUCTS.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Product> listAllProducts() {
        log.info("Dubbo Service: 获取所有产品");
        return new java.util.ArrayList<>(PRODUCTS);
    }

    @Override
    public List<Product> queryProducts(List<Long> productIds) {
        log.info("Dubbo Service: 批量查询产品，IDs: {}", productIds);
        return PRODUCTS.stream()
                .filter(p -> productIds.contains(p.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public Product simulateTimeout(Long id, long sleepTime) {
        log.info("Dubbo Service: 模拟超时调用，ID: {}, 休眠: {}ms", id, sleepTime);
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return getProductById(id);
    }

    @Override
    public Product simulateException(Long id) {
        log.info("Dubbo Service: 模拟异常调用，ID: {}", id);
        throw new RuntimeException("Dubbo 服务端模拟异常: 数据库连接失败");
    }

    @Value("${dubbo.registry.parameters.region:unknown}")
    private String region;

    @Override
    public Product getProductRegionDetails(Long id) {
        // 获取当前服务提供者的区域配置
        // String region =
        // RpcContext.getServiceContext().getUrl().getParameter("region", "unknown");
        log.info("Dubbo Service: 区域优先测试，ID: {}, 当前区域: {}", id, region);

        Product product = getProductById(id);
        if (product != null) {
            // 克隆对象以免修改原对象（虽然这里是模拟数据）
            Product p = new Product(product.getId(), product.getPrice(), product.getProductName(), product.getNum());
            p.setProductName(p.getProductName() + " [Region: " + region + "]");
            return p;
        }
        return null;
    }

    @Override
    public Map<String, Object> testConcurrencyControl(Long sleepTime) {
        log.info("Dubbo Service: 开始服务端并发控制测试, 休眠时间: {}ms", sleepTime);
        
        Map<String, Object> result = new HashMap<>();
        List<String> messages = new ArrayList<>();
        List<Long> executionTimes = new ArrayList<>();

        try {
            // 记录当前并发数
            int current = currentConcurrency.incrementAndGet();
            log.info("Dubbo Service: 当前并发数: {}, 请求开始时间: {}", current, System.currentTimeMillis());
            messages.add(String.format("当前并发数: %d", current));
            
            // 记录请求开始时间
            String requestId = UUID.randomUUID().toString();
            requestStartTimes.put(requestId, System.currentTimeMillis());
            
            // 模拟耗时操作
            Thread.sleep(sleepTime);
            
            // 记录执行时间
            long executionTime = System.currentTimeMillis() - requestStartTimes.get(requestId);
            executionTimes.add(executionTime);
            requestStartTimes.remove(requestId);

            // 减少当前并发数
            currentConcurrency.decrementAndGet();
            log.info("Dubbo Service: 请求完成，当前并发数: {}, 请求结束时间: {}", currentConcurrency.get(), System.currentTimeMillis());
            
            result.put("success", true);
            result.put("message", "服务端并发控制测试成功");
            result.put("currentConcurrency", current);
            result.put("executionTime", executionTime + "ms");
            result.put("sleepTime", sleepTime + "ms");
            
        } catch (Exception e) {
            currentConcurrency.decrementAndGet();
            log.error("服务端并发控制测试异常", e);
            
            result.put("success", false);
            result.put("message", "服务端并发控制测试异常: " + e.getMessage());
            result.put("error", e.getClass().getName());
        }
        
        result.put("messages", messages);
        result.put("executionTimes", executionTimes);
        
        return result;
    }

    @Override
    public Map<String, Object> testLeastActiveLoadBalance() {
        log.info("Dubbo Service: 开始最小并发数负载均衡测试");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 记录当前活跃请求数
            int current = activeRequests.incrementAndGet();

            // 模拟处理时间
            long processingTime = 300 + new Random().nextInt(700); // 300-1000ms随机处理时间
            Thread.sleep(processingTime);

            // 减少当前活跃请求数
            activeRequests.decrementAndGet();

            // 获取当前服务实例信息
            String serverInfo = String.format("Server[%s][Region:%s][Active:%d]",
                System.getProperty("server.port", "8080"), region, current);

            result.put("success", true);
            result.put("message", "最小并发数负载均衡测试成功");
            result.put("serverInfo", serverInfo);
            result.put("processingTime", processingTime + "ms");
            result.put("activeRequests", current);

        } catch (Exception e) {
            activeRequests.decrementAndGet();
            log.error("最小并发数负载均衡测试异常", e);
            
            result.put("success", false);
            result.put("message", "最小并发数负载均衡测试异常: " + e.getMessage());
            result.put("error", e.getClass().getName());
        }
        
        return result;
    }
}
