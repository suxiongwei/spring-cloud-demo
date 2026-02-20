package indi.mofan.order.controller;

import indi.mofan.common.ApiResponse;
import indi.mofan.common.ResultCode;
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
    @GetMapping("/concurrency")
    public ApiResponse<Map<String, Object>> dubboConcurrency(
            @RequestParam(value = "concurrentCount", defaultValue = "20") Integer concurrentCount,
            @RequestParam(value = "sleepTime", defaultValue = "1000") Long sleepTime,
            @RequestParam(value = "type", defaultValue = "executes") String type) {
        try {
            long startTime = System.currentTimeMillis();
            
            // 使用后端多线程并发调用测试
            Map<String, Object> result = productDubboClient.testConcurrencyControlWithThreads(concurrentCount, sleepTime, type);

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

    /**
     * Dubbo Filter拦截测试
     */
    @GetMapping("/filter-test")
    public ApiResponse<Map<String, Object>> dubboFilterTest(
            @RequestParam(value = "message", defaultValue = "Hello Dubbo Filter") String message) {
        try {
            long startTime = System.currentTimeMillis();
            
            // Filter拦截测试
            Map<String, Object> result = productDubboClient.testFilter(message);
            
            long duration = System.currentTimeMillis() - startTime;

            result.put("duration", duration + "ms");
            result.put("method", "Dubbo RPC Filter拦截测试");

            return ApiResponse.success("Dubbo Filter拦截测试完成", result);
        } catch (Exception e) {
            log.error("Dubbo Filter拦截测试失败", e);
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "Dubbo Filter拦截测试失败: " + e.getMessage());
        }
    }

    /**
     * Dubbo 版本 1.0.0 默认分组测试
     */
    @GetMapping("/version-group/v1-default")
    public ApiResponse<Map<String, Object>> dubboVersion1Default(
            @RequestParam(value = "name", defaultValue = "World") String name) {
        try {
            long startTime = System.currentTimeMillis();
            
            Map<String, Object> result = productDubboClient.testVersion1Default(name);
            
            long duration = System.currentTimeMillis() - startTime;

            result.put("duration", duration + "ms");
            result.put("method", "Dubbo RPC 版本 1.0.0 默认分组测试");

            return ApiResponse.success("Dubbo 版本 1.0.0 默认分组测试完成", result);
        } catch (Exception e) {
            log.error("Dubbo 版本 1.0.0 默认分组测试失败", e);
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "Dubbo 版本 1.0.0 默认分组测试失败: " + e.getMessage());
        }
    }

    /**
     * Dubbo 版本 2.0.0 默认分组测试
     */
    @GetMapping("/version-group/v2-default")
    public ApiResponse<Map<String, Object>> dubboVersion2Default(
            @RequestParam(value = "name", defaultValue = "World") String name) {
        try {
            long startTime = System.currentTimeMillis();
            
            Map<String, Object> result = productDubboClient.testVersion2Default(name);
            
            long duration = System.currentTimeMillis() - startTime;

            result.put("duration", duration + "ms");
            result.put("method", "Dubbo RPC 版本 2.0.0 默认分组测试");

            return ApiResponse.success("Dubbo 版本 2.0.0 默认分组测试完成", result);
        } catch (Exception e) {
            log.error("Dubbo 版本 2.0.0 默认分组测试失败", e);
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "Dubbo 版本 2.0.0 默认分组测试失败: " + e.getMessage());
        }
    }

    /**
     * Dubbo 版本 1.0.0 分组A测试
     */
    @GetMapping("/version-group/v1-groupA")
    public ApiResponse<Map<String, Object>> dubboVersion1GroupA(
            @RequestParam(value = "name", defaultValue = "World") String name) {
        try {
            long startTime = System.currentTimeMillis();
            
            Map<String, Object> result = productDubboClient.testVersion1GroupA(name);
            
            long duration = System.currentTimeMillis() - startTime;

            result.put("duration", duration + "ms");
            result.put("method", "Dubbo RPC 版本 1.0.0 分组A测试");

            return ApiResponse.success("Dubbo 版本 1.0.0 分组A测试完成", result);
        } catch (Exception e) {
            log.error("Dubbo 版本 1.0.0 分组A测试失败", e);
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "Dubbo 版本 1.0.0 分组A测试失败: " + e.getMessage());
        }
    }

    /**
     * Dubbo 版本 1.0.0 分组B测试
     */
    @GetMapping("/version-group/v1-groupB")
    public ApiResponse<Map<String, Object>> dubboVersion1GroupB(
            @RequestParam(value = "name", defaultValue = "World") String name) {
        try {
            long startTime = System.currentTimeMillis();
            
            Map<String, Object> result = productDubboClient.testVersion1GroupB(name);
            
            long duration = System.currentTimeMillis() - startTime;

            result.put("duration", duration + "ms");
            result.put("method", "Dubbo RPC 版本 1.0.0 分组B测试");

            return ApiResponse.success("Dubbo 版本 1.0.0 分组B测试完成", result);
        } catch (Exception e) {
            log.error("Dubbo 版本 1.0.0 分组B测试失败", e);
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "Dubbo 版本 1.0.0 分组B测试失败: " + e.getMessage());
        }
    }

    /**
     * Dubbo 版本 2.0.0 分组A测试
     */
    @GetMapping("/version-group/v2-groupA")
    public ApiResponse<Map<String, Object>> dubboVersion2GroupA(
            @RequestParam(value = "name", defaultValue = "World") String name) {
        try {
            long startTime = System.currentTimeMillis();
            
            Map<String, Object> result = productDubboClient.testVersion2GroupA(name);
            
            long duration = System.currentTimeMillis() - startTime;

            result.put("duration", duration + "ms");
            result.put("method", "Dubbo RPC 版本 2.0.0 分组A测试");

            return ApiResponse.success("Dubbo 版本 2.0.0 分组A测试完成", result);
        } catch (Exception e) {
            log.error("Dubbo 版本 2.0.0 分组A测试失败", e);
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "Dubbo 版本 2.0.0 分组A测试失败: " + e.getMessage());
        }
    }

    /**
     * Dubbo 版本与分组对比测试（同时调用多个版本和分组）
     */
    @GetMapping("/version-group/compare")
    public ApiResponse<Map<String, Object>> dubboVersionGroupCompare(
            @RequestParam(value = "name", defaultValue = "World") String name) {
        try {
            long startTime = System.currentTimeMillis();
            
            Map<String, Object> result = new HashMap<>();
            
            result.put("v1Default", productDubboClient.testVersion1Default(name));
            result.put("v2Default", productDubboClient.testVersion2Default(name));
            result.put("v1GroupA", productDubboClient.testVersion1GroupA(name));
            result.put("v1GroupB", productDubboClient.testVersion1GroupB(name));
            result.put("v2GroupA", productDubboClient.testVersion2GroupA(name));
            
            long duration = System.currentTimeMillis() - startTime;

            result.put("duration", duration + "ms");
            result.put("method", "Dubbo RPC 版本与分组对比测试");

            return ApiResponse.success("Dubbo 版本与分组对比测试完成", result);
        } catch (Exception e) {
            log.error("Dubbo 版本与分组对比测试失败", e);
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "Dubbo 版本与分组对比测试失败: " + e.getMessage());
        }
    }

    /**
     * Dubbo 分组聚合测试
     * 使用 group="*" 和 merger="true" 来聚合所有分组的结果
     */
    @GetMapping("/version-group/group-merger")
    public ApiResponse<Map<String, Object>> dubboGroupMerger() {
        try {
            long startTime = System.currentTimeMillis();
            
            Map<String, Object> result = productDubboClient.testGroupMerger();
            
            long duration = System.currentTimeMillis() - startTime;

            result.put("duration", duration + "ms");
            result.put("method", "Dubbo RPC 分组聚合测试");

            return ApiResponse.success("Dubbo 分组聚合测试完成", result);
        } catch (Exception e) {
            log.error("Dubbo 分组聚合测试失败", e);
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "Dubbo 分组聚合测试失败: " + e.getMessage());
        }
    }

    /**
     * Dubbo 随机负载均衡策略测试
     */
    @GetMapping("/loadbalance/random")
    public ApiResponse<Map<String, Object>> dubboRandomLoadBalance(
            @RequestParam(value = "requestCount", defaultValue = "20") Integer requestCount) {
        try {
            long startTime = System.currentTimeMillis();
            
            Map<String, Object> result = productDubboClient.testRandomLoadBalance(requestCount);
            
            long duration = System.currentTimeMillis() - startTime;

            result.put("duration", duration + "ms");
            result.put("method", "Dubbo RPC 随机负载均衡策略测试");

            return ApiResponse.success("Dubbo 随机负载均衡策略测试完成", result);
        } catch (Exception e) {
            log.error("Dubbo 随机负载均衡策略测试失败", e);
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "Dubbo 随机负载均衡策略测试失败: " + e.getMessage());
        }
    }

    /**
     * Dubbo 轮询负载均衡策略测试
     */
    @GetMapping("/loadbalance/roundrobin")
    public ApiResponse<Map<String, Object>> dubboRoundRobinLoadBalance(
            @RequestParam(value = "requestCount", defaultValue = "20") Integer requestCount) {
        try {
            long startTime = System.currentTimeMillis();
            
            Map<String, Object> result = productDubboClient.testRoundRobinLoadBalance(requestCount);
            
            long duration = System.currentTimeMillis() - startTime;

            result.put("duration", duration + "ms");
            result.put("method", "Dubbo RPC 轮询负载均衡策略测试");

            return ApiResponse.success("Dubbo 轮询负载均衡策略测试完成", result);
        } catch (Exception e) {
            log.error("Dubbo 轮询负载均衡策略测试失败", e);
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "Dubbo 轮询负载均衡策略测试失败: " + e.getMessage());
        }
    }

    /**
     * Dubbo 一致性哈希负载均衡策略测试
     */
    @GetMapping("/loadbalance/consistenthash")
    public ApiResponse<Map<String, Object>> dubboConsistentHashLoadBalance(
            @RequestParam(value = "requestCount", defaultValue = "20") Integer requestCount,
            @RequestParam(value = "param", defaultValue = "1") Long param) {
        try {
            long startTime = System.currentTimeMillis();
            
            Map<String, Object> result = productDubboClient.testConsistentHashLoadBalance(requestCount, param);
            
            long duration = System.currentTimeMillis() - startTime;

            result.put("duration", duration + "ms");
            result.put("method", "Dubbo RPC 一致性哈希负载均衡策略测试");

            return ApiResponse.success("Dubbo 一致性哈希负载均衡策略测试完成", result);
        } catch (Exception e) {
            log.error("Dubbo 一致性哈希负载均衡策略测试失败", e);
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "Dubbo 一致性哈希负载均衡策略测试失败: " + e.getMessage());
        }
    }

    /**
     * Dubbo 最小活跃数负载均衡策略测试
     */
    @GetMapping("/loadbalance/leastactive")
    public ApiResponse<Map<String, Object>> dubboLeastActiveLoadBalance(
            @RequestParam(value = "requestCount", defaultValue = "20") Integer requestCount) {
        try {
            long startTime = System.currentTimeMillis();
            
            Map<String, Object> result = productDubboClient.testLeastActiveLoadBalanceStrategy(requestCount);
            
            long duration = System.currentTimeMillis() - startTime;

            result.put("duration", duration + "ms");
            result.put("method", "Dubbo RPC 最小活跃数负载均衡策略测试");

            return ApiResponse.success("Dubbo 最小活跃数负载均衡策略测试完成", result);
        } catch (Exception e) {
            log.error("Dubbo 最小活跃数负载均衡策略测试失败", e);
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "Dubbo 最小活跃数负载均衡策略测试失败: " + e.getMessage());
        }
    }

    /**
     * Dubbo 最短响应时间负载均衡策略测试
     */
    @GetMapping("/loadbalance/shortestresponse")
    public ApiResponse<Map<String, Object>> dubboShortestResponseLoadBalance(
            @RequestParam(value = "requestCount", defaultValue = "20") Integer requestCount) {
        try {
            long startTime = System.currentTimeMillis();
            
            Map<String, Object> result = productDubboClient.testShortestResponseLoadBalance(requestCount);
            
            long duration = System.currentTimeMillis() - startTime;

            result.put("duration", duration + "ms");
            result.put("method", "Dubbo RPC 最短响应时间负载均衡策略测试");

            return ApiResponse.success("Dubbo 最短响应时间负载均衡策略测试完成", result);
        } catch (Exception e) {
            log.error("Dubbo 最短响应时间负载均衡策略测试失败", e);
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "Dubbo 最短响应时间负载均衡策略测试失败: " + e.getMessage());
        }
    }

    /**
     * 协议对比测试 - Dubbo vs Triple vs REST
     */
    @GetMapping("/protocol/compare")
    public ApiResponse<Map<String, Object>> compareProtocols(
            @RequestParam(value = "productId", defaultValue = "1") Long productId,
            @RequestParam(value = "requestCount", defaultValue = "100") Integer requestCount) {
        try {
            long startTime = System.currentTimeMillis();
            
            Map<String, Object> result = productDubboClient.compareProtocols(productId, requestCount);
            
            long duration = System.currentTimeMillis() - startTime;

            result.put("duration", duration + "ms");
            result.put("method", "协议对比测试 - Dubbo vs Triple vs REST");
            result.put("timestamp", System.currentTimeMillis());

            return ApiResponse.success("协议对比测试完成", result);
        } catch (Exception e) {
            log.error("协议对比测试失败", e);
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "协议对比测试失败: " + e.getMessage());
        }
    }

    /**
     * Dubbo协议单独测试
     */
    @GetMapping("/protocol/dubbo")
    public ApiResponse<Map<String, Object>> testDubboProtocol(
            @RequestParam(value = "productId", defaultValue = "1") Long productId) {
        try {
            long startTime = System.currentTimeMillis();
            Product product = productDubboClient.getProduct(productId);
            long duration = System.currentTimeMillis() - startTime;

            Map<String, Object> result = new HashMap<>();
            result.put("product", product);
            result.put("duration", duration + "ms");
            result.put("method", "Dubbo协议测试");
            result.put("protocol", "dubbo");

            return ApiResponse.success("Dubbo协议测试成功", result);
        } catch (Exception e) {
            log.error("Dubbo协议测试失败", e);
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "Dubbo协议测试失败: " + e.getMessage());
        }
    }

    /**
     * Triple协议单独测试
     */
    @GetMapping("/protocol/triple")
    public ApiResponse<Map<String, Object>> testTripleProtocol(
            @RequestParam(value = "productId", defaultValue = "1") Long productId) {
        try {
            long startTime = System.currentTimeMillis();
            Product product = productDubboClient.getProductTriple(productId);
            long duration = System.currentTimeMillis() - startTime;

            Map<String, Object> result = new HashMap<>();
            result.put("product", product);
            result.put("duration", duration + "ms");
            result.put("method", "Triple协议测试");
            result.put("protocol", "triple");

            return ApiResponse.success("Triple协议测试成功", result);
        } catch (Exception e) {
            log.error("Triple协议测试失败", e);
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "Triple协议测试失败: " + e.getMessage());
        }
    }

    /**
     * REST协议单独测试
     */
    @GetMapping("/protocol/rest")
    public ApiResponse<Map<String, Object>> testRestProtocol(
            @RequestParam(value = "productId", defaultValue = "1") Long productId) {
        try {
            long startTime = System.currentTimeMillis();
            Product product = productDubboClient.getProductRest(productId);
            long duration = System.currentTimeMillis() - startTime;

            Map<String, Object> result = new HashMap<>();
            result.put("product", product);
            result.put("duration", duration + "ms");
            result.put("method", "REST协议测试");
            result.put("protocol", "rest");

            return ApiResponse.success("REST协议测试成功", result);
        } catch (Exception e) {
            log.error("REST协议测试失败", e);
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "REST协议测试失败: " + e.getMessage());
        }
    }
}

