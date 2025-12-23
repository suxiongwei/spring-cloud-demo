package indi.mofan.order.controller;

import indi.mofan.order.common.ApiResponse;
import indi.mofan.order.common.ResultCode;
import indi.mofan.order.dubbo.ProductDubboClient;
import indi.mofan.product.bean.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 订单服务 - Dubbo 版本 Controller
 * 
 * @author xiongweisu
 * @date 2025/3/23
 */
@Slf4j
@RestController
public class OrderDubboController {

    @Autowired
    private ProductDubboClient productDubboClient;

    /**
     * Dubbo 同步调用演示
     */
    @GetMapping("/call-sync")
    public ApiResponse<Map<String, Object>> dubboCallSync(
            @RequestParam(value = "productId", defaultValue = "1") Long productId) {
        try {
            long startTime = System.currentTimeMillis();
            Product product = productDubboClient.getProduct(productId);
            long duration = System.currentTimeMillis() - startTime;

            Map<String, Object> result = new HashMap<>();
            result.put("product", product);
            result.put("duration", duration + "ms");
            result.put("method", "Dubbo RPC 同步调用");

            return ApiResponse.success("Dubbo 同步调用成功", result);
        } catch (Exception e) {
            log.error("Dubbo 同步调用失败", e);
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "Dubbo 调用失败: " + e.getMessage());
        }
    }

    /**
     * Dubbo 批量查询演示
     */
    @GetMapping("/call-batch")
    public ApiResponse<Map<String, Object>> dubboCallBatch(
            @RequestParam(value = "productIds", defaultValue = "1,2,3") String productIds) {
        try {
            long startTime = System.currentTimeMillis();
            List<Long> ids = Arrays.stream(productIds.split(","))
                    .map(Long::parseLong)
                    .toList();
            List<Product> products = productDubboClient.queryProducts(ids);
            long duration = System.currentTimeMillis() - startTime;

            Map<String, Object> result = new HashMap<>();
            result.put("products", products);
            result.put("count", products.size());
            result.put("duration", duration + "ms");
            result.put("method", "Dubbo RPC 批量查询");

            return ApiResponse.success("Dubbo 批量查询成功", result);
        } catch (Exception e) {
            log.error("Dubbo 批量查询失败", e);
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "Dubbo 批量查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取所有产品
     */
    @GetMapping("/list-all")
    public ApiResponse<Map<String, Object>> dubboListAll() {
        try {
            long startTime = System.currentTimeMillis();
            List<Product> products = productDubboClient.getAllProducts();
            long duration = System.currentTimeMillis() - startTime;

            Map<String, Object> result = new HashMap<>();
            result.put("products", products);
            result.put("count", products.size());
            result.put("duration", duration + "ms");
            result.put("method", "Dubbo RPC 查询列表");

            return ApiResponse.success("Dubbo 查询成功", result);
        } catch (Exception e) {
            log.error("Dubbo 查询失败", e);
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "Dubbo 查询失败: " + e.getMessage());
        }
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public ApiResponse<String> health() {
        return ApiResponse.success("service-order-dubbo 健康状态正常");
    }

    /**
     * Dubbo 超时调用演示
     */
    @GetMapping("/call-timeout")
    public ApiResponse<Map<String, Object>> dubboCallTimeout(
            @RequestParam(value = "productId", defaultValue = "1") Long productId,
            @RequestParam(value = "sleepTime", defaultValue = "4000") Long sleepTime) {
        try {
            long startTime = System.currentTimeMillis();
            // 默认超时时间为 3000ms，如果 sleepTime > 3000ms 将触发超时
            Product product = productDubboClient.getProductWithTimeout(productId, sleepTime);
            long duration = System.currentTimeMillis() - startTime;

            Map<String, Object> result = new HashMap<>();
            result.put("product", product);
            result.put("duration", duration + "ms");
            result.put("method", "Dubbo RPC 超时测试");

            return ApiResponse.success("Dubbo 调用成功 (未超时)", result);
        } catch (Exception e) {
            log.error("Dubbo 超时演示捕获异常", e);
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "Dubbo 调用超时或失败: " + e.getMessage());
        }
    }

    /**
     * Dubbo 异常处理演示
     */
    @GetMapping("/call-exception")
    public ApiResponse<Map<String, Object>> dubboCallException(
            @RequestParam(value = "productId", defaultValue = "1") Long productId) {
        try {
            productDubboClient.getProductWithException(productId);
            return ApiResponse.success("Dubbo 调用成功 (意外)", null);
        } catch (Exception e) {
            log.error("Dubbo 异常演示捕获异常", e);
            Map<String, Object> result = new HashMap<>();
            result.put("exception", e.getClass().getName());
            result.put("message", e.getMessage());
            result.put("method", "Dubbo RPC 异常测试");
            return ApiResponse.success("Dubbo 异常捕获成功", result);
        }
    }

    /**
     * Dubbo 异步调用演示
     */
    @GetMapping("/call-async")
    public ApiResponse<Map<String, Object>> dubboCallAsync(
            @RequestParam(value = "productId", defaultValue = "1") Long productId) {
        try {
            long startTime = System.currentTimeMillis();
            CompletableFuture<Product> future = productDubboClient.getProductAsync(productId);

            // 模拟主线程做其他事情
            Thread.sleep(50);

            Product product = future.get(); // 阻塞等待结果
            long duration = System.currentTimeMillis() - startTime;

            Map<String, Object> result = new HashMap<>();
            result.put("product", product);
            result.put("duration", duration + "ms");
            result.put("method", "Dubbo RPC 异步调用");
            result.put("async", true);

            return ApiResponse.success("Dubbo 异步调用成功", result);
        } catch (Exception e) {
            log.error("Dubbo 异步调用失败", e);
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "Dubbo 异步调用失败: " + e.getMessage());
        }
    }

    /**
     * Dubbo 同机房/区域优先调用演示
     */
    @GetMapping("/call-region")
    public ApiResponse<Map<String, Object>> dubboCallRegion(
            @RequestParam(value = "productId", defaultValue = "1") Long productId,
            @RequestParam(value = "clientRegion", defaultValue = "hangzhou") String clientRegion) {
        try {
            long startTime = System.currentTimeMillis();
            Product product = productDubboClient.getProductRegionDetails(productId, clientRegion);
            long duration = System.currentTimeMillis() - startTime;

            Map<String, Object> result = new HashMap<>();
            result.put("product", product);
            result.put("duration", duration + "ms");
            result.put("method", "Dubbo RPC 区域优先测试");
            result.put("clientRegion", clientRegion);

            return ApiResponse.success("Dubbo 区域优先调用成功", result);
        } catch (Exception e) {
            log.error("Dubbo 区域优先调用失败", e);
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "Dubbo 区域优先调用失败: " + e.getMessage());
        }
    }

    /**
     * Dubbo 并发控制测试（同时测试消费端actives和服务端executes限流）
     */
    @GetMapping("/concurrency-test-backend")
    public ApiResponse<Map<String, Object>> dubboConcurrencyTestBackend(
            @RequestParam(value = "concurrentCount", defaultValue = "20") Integer concurrentCount,
            @RequestParam(value = "sleepTime", defaultValue = "1000") Long sleepTime) {
        try {
            long startTime = System.currentTimeMillis();
            
            // 使用后端多线程并发调用测试
//            Map<String, Object> result = productDubboClient.testConcurrencyControlWithThreads(concurrentCount, sleepTime);
            Map<String, Object> result = productDubboClient.testActivesControl();

            long duration = System.currentTimeMillis() - startTime;

            result.put("duration", duration + "ms");
            result.put("method", "Dubbo RPC 并发控制测试（消费端actives+服务端executes）");
            result.put("concurrentCount", concurrentCount);
            result.put("sleepTime", sleepTime);

            return ApiResponse.success("Dubbo 并发控制测试完成（消费端actives+服务端executes）", result);
        } catch (Exception e) {
            log.error("Dubbo 并发控制测试失败", e);
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "Dubbo 并发控制测试失败: " + e.getMessage());
        }
    }

    /**
     * Dubbo 最小并发数负载均衡测试
     */
    @GetMapping("/leastactive-test")
    public ApiResponse<Map<String, Object>> dubboLeastActiveTest() {
        try {
            long startTime = System.currentTimeMillis();
            
            // 最小并发数负载均衡测试
            Map<String, Object> result = productDubboClient.testLeastActiveLoadBalance();
            
            long duration = System.currentTimeMillis() - startTime;

            result.put("duration", duration + "ms");
            result.put("method", "Dubbo RPC 最小并发数负载均衡测试");

            return ApiResponse.success("Dubbo 最小并发数负载均衡测试完成", result);
        } catch (Exception e) {
            log.error("Dubbo 最小并发数负载均衡测试失败", e);
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "Dubbo 最小并发数负载均衡测试失败: " + e.getMessage());
        }
    }
}
