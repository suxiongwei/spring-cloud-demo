package indi.mofan.order.controller;

import indi.mofan.product.bean.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import indi.mofan.order.common.BlockDetailFormatter;

import indi.mofan.order.bean.Order;
import indi.mofan.order.common.ApiResponse;
import indi.mofan.order.common.ResultCode;
import indi.mofan.order.properties.CkProperties;
import indi.mofan.order.properties.OrderProperties;
import indi.mofan.order.service.CommonResourceService;
import indi.mofan.order.service.OrderService;
import indi.mofan.order.feign.ProductFeignClient;
import lombok.extern.slf4j.Slf4j;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.ArrayList;
import java.util.HashMap;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRuleManager;

/**
 * @author mofan
 * @date 2025/3/23 17:34
 */
@Slf4j
@Tag(name = "订单接口")
// @RequestMapping("/api/order")
@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderProperties orderProperties;
    @Autowired
    private CkProperties ckProperties;
    @Autowired
    private CommonResourceService commonResourceService;
    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private ProductFeignClient productFeignClient;

    @GetMapping("/config")
    @Operation(summary = "查看订单服务配置")
    public ApiResponse<String> config() {
        String msg = "order timeout: " + orderProperties.getTimeout()
                + " auto-confirm: " + orderProperties.getAutoConfirm()
                + " db-url: " + orderProperties.getDbUrl();
        return ApiResponse.success(msg);
    }

    @GetMapping("/create")
    @Operation(summary = "创建订单")
    public ApiResponse<Order> createOrder(@RequestParam("userId") Long userId,
            @RequestParam("productId") Long productId) {
        Order order = orderService.createOrder(productId, userId);
        return ApiResponse.success("订单创建成功", order);
    }

    /**
     * 1. 接口级别限流示例：QPS限流
     * 适用于：秒杀接口等需要严格控制访问频率的场景
     */
    @GetMapping("/rateLimit/qps")
    @SentinelResource(value = "rateLimit-qps", blockHandler = "qpsBlockHandler")
    public ApiResponse<String> rateLimitByQps() {
        return ApiResponse.success("QPS限流测试接口调用成功");
    }

    public ApiResponse<String> qpsBlockHandler(BlockException ex) {
        log.warn("QPS限流触发: {}", BlockDetailFormatter.format(ex));
        return ApiResponse.fail(ResultCode.TOO_MANY_REQUESTS, "接口访问过于频繁，请稍后再试=> " + BlockDetailFormatter.format(ex));
    }

    /**
     * 1. 接口级别限流示例：并发线程数限流
     * 适用于：数据库连接池有限、需要控制并发处理数量的场景
     */
    @GetMapping("/rateLimit/thread")
    @SentinelResource(value = "rateLimit-thread", blockHandler = "threadBlockHandler")
    public ApiResponse<String> rateLimitByThread() throws InterruptedException {
        // 模拟处理耗时
        Thread.sleep(8000);
        return ApiResponse.success("并发线程数限流测试接口调用成功");
    }

    public ApiResponse<String> threadBlockHandler(BlockException ex) {
        log.warn("并发线程数限流触发: {}", BlockDetailFormatter.format(ex));
        return ApiResponse.fail(ResultCode.TOO_MANY_REQUESTS, "系统繁忙，请求排队中=> " + BlockDetailFormatter.format(ex));
    }

    /**
     * 2. 热点参数限流示例
     * 适用于：根据用户ID、商品ID等参数进行精细化流量控制
     */
    @GetMapping("/hotspot/param")
    @SentinelResource(value = "hotspot-param", blockHandler = "hotspotParamBlockHandler")
    public ApiResponse<String> hotspotParamLimit(@RequestParam("userId") Long userId,
            @RequestParam(value = "productId", required = false) Long productId) {
        return ApiResponse.success("热点参数限流测试，用户ID: " + userId + ", 商品ID: " + productId);
    }

    public ApiResponse<String> hotspotParamBlockHandler(Long userId, Long productId, BlockException ex) {
        log.warn("热点参数限流触发，用户ID: {}, 商品ID: {}, {}", userId, productId, BlockDetailFormatter.format(ex));
        return ApiResponse.fail(ResultCode.TOO_MANY_REQUESTS, "请求过于频繁，请稍后再试=> " + BlockDetailFormatter.format(ex));
    }

    /**
     * 3. 系统自适应保护示例
     * 适用于：基于系统负载自动调节流量，保护系统稳定性
     */
    @GetMapping("/system/protection")
    @SentinelResource(value = "system-protection", blockHandler = "systemProtectionBlockHandler")
    public ApiResponse<String> systemProtection() throws InterruptedException {
        // 模拟处理耗时
        Thread.sleep(200);
        return ApiResponse.success("系统自适应保护测试接口调用成功");
    }

    public ApiResponse<String> systemProtectionBlockHandler(BlockException ex) {
        log.warn("系统自适应保护触发: {}", BlockDetailFormatter.format(ex));
        return ApiResponse.fail(ResultCode.TOO_MANY_REQUESTS, "系统负载过高，请稍后再试=> " + BlockDetailFormatter.format(ex));
    }

    /**
     * 4. 关联限流示例
     * 适用于：写操作优先于读操作的场景
     */
    @GetMapping("/writeDb")
    @SentinelResource(value = "write-db")
    public ApiResponse<String> writeDb() {
        log.info("writeDb=>");
        try {
            // 模拟耗时操作，方便测试并发
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return ApiResponse.success("writeDb success=>");
    }

    @GetMapping("/readDb")
    @SentinelResource(value = "read-db", blockHandler = "readDbBlockHandler")
    public ApiResponse<String> readDb() {
        log.info("readDb=>");
        return ApiResponse.success("readDb success=>");
    }

    public ApiResponse<String> readDbBlockHandler(BlockException ex) {
        log.warn("readDb 被限流，原因：writeDb 并发过高，{}", BlockDetailFormatter.format(ex));
        return ApiResponse.fail(ResultCode.TOO_MANY_REQUESTS,
                "系统繁忙，请稍后再试（writeDb 并发过高）=> " + BlockDetailFormatter.format(ex));
    }

    /**
     * 5. 链路限流示例
     * 适用于：同一资源在不同调用链路中采用不同限流策略
     */
    @GetMapping("/entry/a")
    public ApiResponse<String> entryA() {
        return commonResourceService.commonResource("链路A");
    }

    @GetMapping("/entry/b")
    public ApiResponse<String> entryB() {
        return commonResourceService.commonResource("链路B");
    }

    /**
     * 6. 熔断降级示例
     * 适用于：依赖服务不稳定时的快速失败和恢复机制
     */
    @GetMapping("/degrade/rt")
    @SentinelResource(value = "degrade-rt", fallback = "degradeRtFallback", blockHandler = "degradeRtBlockHandler")
    public ApiResponse<String> degradeByRt() throws InterruptedException {
        // 模拟慢调用
        Thread.sleep(2000);
        return ApiResponse.success("熔断降级测试（响应时间）调用成功");
    }

    public ApiResponse<String> degradeRtFallback() {
        log.warn("熔断降级触发（响应时间超限）");
        return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "服务暂时不可用，正在降级处理=>");
    }

    public ApiResponse<String> degradeRtBlockHandler(BlockException ex) {
        log.warn("熔断降级限流触发: {}", BlockDetailFormatter.format(ex));
        return ApiResponse.fail(ResultCode.TOO_MANY_REQUESTS, "服务请求过于频繁，请稍后再试=> " + BlockDetailFormatter.format(ex));
    }

    /**
     * 7. 黑白名单控制示例
     * 适用于：基于来源IP或用户标识的访问控制
     */
    @GetMapping("/authority/control")
    @SentinelResource(value = "authority-control", blockHandler = "authorityBlockHandler")
    public ApiResponse<String> authorityControl(
            @RequestParam(value = "userType", defaultValue = "normal") String userType) {
        return ApiResponse.success("访问控制测试，用户类型: " + userType);
    }

    public ApiResponse<String> authorityBlockHandler(String userType, BlockException ex) {
        log.warn("访问控制拦截触发，用户类型: {}, {}", userType, BlockDetailFormatter.format(ex));
        return ApiResponse.fail(ResultCode.FORBIDDEN, "访问权限不足=> " + BlockDetailFormatter.format(ex));
    }

    /**
     * 7+. 黑名单控制单独示例
     * 与白名单示例分离，便于同时展示两种策略不互相干扰
     */
    @GetMapping("/authority/control/black")
    @SentinelResource(value = "authority-control-black", blockHandler = "authorityBlockHandler")
    public ApiResponse<String> authorityControlBlack(
            @RequestParam(value = "userType", defaultValue = "normal") String userType) {
        return ApiResponse.success("黑名单控制测试，用户类型: " + userType);
    }

    @GetMapping("/sentinel/rules/flow")
    public ApiResponse<List<FlowRule>> getFlowRules(
            @RequestParam(value = "resource", required = false) String resource) {
        List<FlowRule> rules = FlowRuleManager.getRules();
        if (resource != null && !resource.isEmpty()) {
            rules = rules.stream().filter(r -> resource.equals(r.getResource())).toList();
        }
        return ApiResponse.success("flow rules", rules);
    }

    @GetMapping("/sentinel/rules/param-flow")
    public ApiResponse<List<ParamFlowRule>> getParamFlowRules(
            @RequestParam(value = "resource", required = false) String resource) {
        List<ParamFlowRule> rules = ParamFlowRuleManager.getRules();
        if (resource != null && !resource.isEmpty()) {
            rules = rules.stream().filter(r -> resource.equals(r.getResource())).toList();
        }
        return ApiResponse.success("param flow rules", rules);
    }

    @GetMapping("/sentinel/rules/degrade")
    public ApiResponse<List<DegradeRule>> getDegradeRules(
            @RequestParam(value = "resource", required = false) String resource) {
        List<DegradeRule> rules = DegradeRuleManager.getRules();
        if (resource != null && !resource.isEmpty()) {
            rules = rules.stream().filter(r -> resource.equals(r.getResource())).toList();
        }
        return ApiResponse.success("degrade rules", rules);
    }

    @GetMapping("/sentinel/rules/authority")
    public ApiResponse<List<AuthorityRule>> getAuthorityRules(
            @RequestParam(value = "resource", required = false) String resource) {
        List<AuthorityRule> rules = AuthorityRuleManager.getRules();
        if (resource != null && !resource.isEmpty()) {
            rules = rules.stream().filter(r -> resource.equals(r.getResource())).toList();
        }
        return ApiResponse.success("authority rules", rules);
    }

    // ======================== 以下是新增的演示功能接口 ========================

    /**
     * 1. OpenFeign 服务间调用演示
     */
    @GetMapping("/demo/feign/call")
    @Operation(summary = "OpenFeign 服务间调用演示")
    public ApiResponse<Product> feignCallDemo(@RequestParam("productId") Long productId) {
        try {
            long start = System.currentTimeMillis();
            Product product = productFeignClient.getProductById(productId);
            long elapsed = System.currentTimeMillis() - start;
            return ApiResponse.success("OpenFeign调用成功", product);
        } catch (Exception e) {
            log.error("OpenFeign 调用失败", e);
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "服务调用失败: " + e.getMessage());
        }
    }

    /**
     * 2. Nacos 服务注册与发现演示
     */
    @GetMapping("/demo/nacos/services")
    @Operation(summary = "Nacos 服务注册与发现演示")
    public ApiResponse<Object> nacosServicesDemo() {
        List<String> services = discoveryClient.getServices();
        Map<String, Object> result = new HashMap<>();
        result.put("总服务数", services.size());
        result.put("服务列表", services);

        // 获取每个服务的实例信息
        Map<String, Object> serviceInstances = new HashMap<>();
        for (String service : services) {
            List<ServiceInstance> instances = discoveryClient.getInstances(service);
            List<Map<String, Object>> instanceInfos = instances.stream().map(inst -> {
                Map<String, Object> info = new HashMap<>();
                info.put("服务ID", inst.getServiceId());
                info.put("主机", inst.getHost());
                info.put("端口", inst.getPort());
                info.put("URI", inst.getUri());
                return info;
            }).toList();
            serviceInstances.put(service, instanceInfos);
        }
        result.put("实例详情", serviceInstances);
        return ApiResponse.success("Nacos服务发现成功", result);
    }

    /**
     * 3. 负载均衡策略演示
     * 使用spring-cloud-starter-loadbalancer来实现，需要在pom中进行配置
     * 
     */
    @GetMapping("/demo/load-balance")
    @Operation(summary = "负载均衡策略演示")
    public ApiResponse<Object> loadBalanceDemo() {
        Map<String, Object> result = new HashMap<>();
        List<ServiceInstance> instances = discoveryClient.getInstances("service-product");
        result.put("service-product 实例数", instances.size());
        result.put("实例列表", instances.stream().map(inst -> inst.getHost() + ":" + inst.getPort()).toList());

        // 模拟5次调用，展示负载均衡效果
        List<String> loadBalanceResults = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Product product = productFeignClient.getProductById(1L);
            loadBalanceResults.add("第" + i + "次调用 -> " + product.getPort());
        }
        result.put("实际调用结果(5次)", loadBalanceResults);

        result.put("说明", "Spring Cloud 默认使用轮询策略，多次调用会依次分配到不同实例");
        return ApiResponse.success("负载均衡策略获取成功", result);
    }

    /**
     * 4. 链路追踪与日志演示
     */
    @GetMapping("/demo/tracing")
    @Operation(summary = "链路追踪与日志演示")
    public ApiResponse<Object> tracingDemo() {
        Map<String, Object> result = new HashMap<>();

        // 获取当前请求的信息
        String traceId = java.util.UUID.randomUUID().toString().replace("-", "");
        result.put("TraceID", traceId);
        result.put("说明", "在实际应用中，可通过 RequestContextHolder 或者 @RequestHeader 获取真实的 TraceID");
        result.put("日志级别配置", "indi.mofan.order.feign: DEBUG (查看 Feign 调用日志)");
        result.put("调用链路", "gateway -> order -> product");
        result.put("关键信息", new String[] {
                "请求时间",
                "响应时间",
                "调用耗时",
                "是否成功",
                "返回数据"
        });

        return ApiResponse.success("链路追踪信息生成成功", result);
    }

    /**
     * 5. 网关路由与过滤演示
     */
    @GetMapping("/demo/gateway-routing")
    @Operation(summary = "网关路由与过滤演示")
    public ApiResponse<Object> gatewayRoutingDemo() {
        Map<String, Object> result = new HashMap<>();
        result.put("网关地址", "http://localhost:9090");
        result.put("路由规则", new String[] {
                "GET /product -> service-product",
                "GET /api/order -> service-order",
                "GET /api/account -> seata-account",
                "GET /api/storage -> seata-storage"
        });
        result.put("过滤器", new String[] {
                "OnceToken 过滤器: 防止重复提交",
                "RtGlobalFilter: 记录请求耗时",
                "路由过滤器: 自动添加服务前缀"
        });
        result.put("说明", "网关作为所有请求的入口，提供统一的路由、认证、限流等功能");

        return ApiResponse.success("网关路由信息获取成功", result);
    }

    /**
     * 6. Nacos 配置动态更新演示
     */
    @GetMapping("/demo/nacos-config")
    @Operation(summary = "Nacos 配置动态更新演示")
    public ApiResponse<Object> nacosConfigDemo() {
        Map<String, Object> result = new HashMap<>();
        result.put("connectTimeout", ckProperties.getConnectTimeout());
        result.put("readTimeout", ckProperties.getReadTimeout());
        result.put("说明", "这些配置来自 Nacos (common.yaml)，修改后应用会自动刷新");
        result.put("注意", "请确保在 Nacos 中 hh.yaml 的 Group 为 'ORDER_GROUP'，而不是 'SENTINEL_GROUP'");
        result.put("@RefreshScope", "标记在配置类上，实现配置的动态刷新");

        return ApiResponse.success("Nacos配置读取成功", result);
    }

    /**
     * 7. 异步并发处理演示 (CompletableFuture)
     */
    @GetMapping("/demo/async-parallel")
    @Operation(summary = "异步并发处理演示")
    public ApiResponse<Object> asyncParallelDemo() {
        long start = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();

        // 模拟任务1：查询用户信息 (耗时1秒)
        CompletableFuture<String> userFuture = CompletableFuture
                .supplyAsync(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return "User(ID=1001, Name=Mofan)";
                });

        // 模拟任务2：查询库存信息 (耗时1秒)
        CompletableFuture<String> stockFuture = CompletableFuture
                .supplyAsync(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return "Stock(Count=99)";
                });

        // 等待两个任务都完成
        CompletableFuture.allOf(userFuture, stockFuture).join();

        try {
            result.put("userInfo", userFuture.get());
            result.put("stockInfo", stockFuture.get());
        } catch (Exception e) {
            e.printStackTrace();
        }

        long end = System.currentTimeMillis();
        long cost = end - start;

        result.put("totalCost", cost + "ms");
        result.put("optimization", "串行执行需2000ms，并行执行仅需约1000ms");

        return ApiResponse.success("异步并发执行成功", result);
    }
}
