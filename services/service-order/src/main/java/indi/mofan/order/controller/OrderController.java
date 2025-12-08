package indi.mofan.order.controller;

import indi.mofan.product.bean.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.env.Environment;
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
 * @author xiongweisu
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
    @Autowired
    private Environment environment;

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

//    /**
//     * 3. 负载均衡策略演示
//     * 使用spring-cloud-starter-loadbalancer来实现，需要在pom中进行配置
//     *
//     */
//    @GetMapping("/demo/load-balance")
//    @Operation(summary = "负载均衡策略演示")
//    public ApiResponse<Object> loadBalanceDemo() {
//        Map<String, Object> result = new HashMap<>();
//        List<ServiceInstance> instances = discoveryClient.getInstances("service-product");
//        result.put("service-product 实例数", instances.size());
//        result.put("实例列表", instances.stream().map(inst -> inst.getHost() + ":" + inst.getPort()).toList());
//
//        // 模拟5次调用，展示负载均衡效果
//        List<String> loadBalanceResults = new ArrayList<>();
//        for (int i = 1; i <= 5; i++) {
//            Product product = productFeignClient.getProductById(1L);
//            loadBalanceResults.add("第" + i + "次调用 -> " + product.getPort());
//        }
//        result.put("实际调用结果(5次)", loadBalanceResults);
//
//        result.put("说明", "Spring Cloud 默认使用轮询策略，多次调用会依次分配到不同实例");
//        return ApiResponse.success("负载均衡策略获取成功", result);
//    }

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


    /**
     * 9. 超时与重试机制演示
     */
    @GetMapping("/demo/timeout-retry")
    @Operation(summary = "超时与重试机制演示")
    public ApiResponse<Object> timeoutRetryDemo(@RequestParam(value = "delay", defaultValue = "0") int delay,
                                              @RequestParam(value = "productId", defaultValue = "1") Long productId) {
        Map<String, Object> result = new HashMap<>();
        long start = System.currentTimeMillis();

        try {
            // 使用指定的productId调用product服务，如果productId=88888会触发5秒延迟
            Product product = productFeignClient.getProductById(productId);
            long elapsed = System.currentTimeMillis() - start;

            result.put("调用结果", product);
            result.put("实际耗时", elapsed + "ms");
            result.put("超时配置", "连接超时: 3s, 读取超时: 6s");
            result.put("重试配置", "默认不重试(避免幂等性问题)");
            result.put("测试说明", "productId=88888会触发5秒延迟，可用于测试读取超时");
            result.put("当前productId", productId);

            return ApiResponse.success("超时重试测试完成", result);
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - start;
            result.put("错误信息", e.getMessage());
            result.put("实际耗时", elapsed + "ms");
            result.put("说明", "请求超时,已触发超时保护机制");
            result.put("建议", "尝试使用productId=88888测试5秒延迟场景");
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "请求超时");
        }
    }

    /**
     * 10. 请求/响应拦截器演示
     */
    @GetMapping("/demo/feign-interceptor")
    @Operation(summary = "Feign请求拦截器演示")
    public ApiResponse<Object> feignInterceptorDemo() {
        Map<String, Object> result = new HashMap<>();

        // 调用 product 服务,拦截器会自动添加请求头
        Product product = productFeignClient.getProductById(1L);

        result.put("调用结果", product);
        result.put("拦截器功能", new String[] {
                "自动添加 X-Token 请求头",
                "记录请求和响应日志",
                "统计调用耗时",
                "添加链路追踪ID"
        });
        result.put("配置位置", "FeignConfig.java 中配置 RequestInterceptor");
        result.put("说明", "拦截器可用于统一添加认证信息、日志记录、性能监控等");

        return ApiResponse.success("拦截器演示完成", result);
    }

    /**
     * 11. 配置动态刷新增强演示
     */
    @GetMapping("/demo/config-refresh")
    @Operation(summary = "Nacos配置动态刷新演示")
    public ApiResponse<Object> configRefreshDemo() {
        Map<String, Object> result = new HashMap<>();

        // 读取当前配置
        result.put("当前配置", Map.of(
                "connectTimeout", ckProperties.getConnectTimeout(),
                "readTimeout", ckProperties.getReadTimeout()));

        result.put("配置来源", "Nacos配置中心 (common.yaml)");
        result.put("刷新机制", "@RefreshScope 注解实现自动刷新");
        result.put("操作步骤", new String[] {
                "1. 在 Nacos 控制台修改配置",
                "2. 发布配置",
                "3. 应用自动感知并刷新(无需重启)",
                "4. 再次调用此接口查看新配置"
        });
        result.put("验证方法", "修改 Nacos 中的 connectTimeout 值,然后刷新此接口");
        result.put("Nacos地址", "http://localhost:8848/nacos");

        return ApiResponse.success("配置信息读取成功", result);
    }

    /**
     * 12. 服务健康检查演示
     */
    @GetMapping("/demo/health-check")
    @Operation(summary = "服务健康检查演示")
    public ApiResponse<Object> healthCheckDemo() {
        Map<String, Object> result = new HashMap<>();

        // 检查各个服务的健康状态
        List<String> services = discoveryClient.getServices();
        Map<String, Object> healthStatus = new HashMap<>();

        for (String service : services) {
            List<ServiceInstance> instances = discoveryClient.getInstances(service);
            Map<String, Object> serviceHealth = new HashMap<>();
            serviceHealth.put("实例数量", instances.size());
            serviceHealth.put("健康实例", instances.size()); // 简化处理,实际应检查每个实例
            serviceHealth.put("状态", instances.isEmpty() ? "DOWN" : "UP");

            List<String> instanceList = instances.stream()
                    .map(inst -> inst.getHost() + ":" + inst.getPort())
                    .toList();
            serviceHealth.put("实例列表", instanceList);

            healthStatus.put(service, serviceHealth);
        }

        result.put("服务健康状态", healthStatus);
        result.put("健康检查端点", "/actuator/health");
        result.put("说明", new String[] {
                "Spring Boot Actuator 提供健康检查端点",
                "Nacos 会定期检查实例健康状态",
                "不健康的实例会自动从服务列表中移除",
                "支持自定义健康检查指标"
        });
        result.put("优雅下线", "通过 /actuator/shutdown 端点实现优雅下线");

        return ApiResponse.success("健康检查完成", result);
    }

    /**
     * 13. 改进版 - OpenFeign 调用(展示降级效果)
     */
    @GetMapping("/demo/feign/call-enhanced")
    @Operation(summary = "OpenFeign调用增强版(含降级演示)")
    public ApiResponse<Object> feignCallEnhancedDemo(
            @RequestParam("productId") Long productId) {
        Map<String, Object> result = new HashMap<>();
        long start = System.currentTimeMillis();

        try {
            Product product = productFeignClient.getProductById(productId);

            long elapsed = System.currentTimeMillis() - start;

            result.put("调用结果", product);
            result.put("调用耗时", elapsed + "ms");
            result.put("是否降级", product.getProductName().equals("未知商品"));
            result.put("降级策略", "返回默认商品信息,保证系统可用性");
            // 获取Feign客户端的实际配置值
            String connectTimeout = environment.getProperty("spring.cloud.openfeign.client.config.service-product.connect-timeout", "3000");
            String readTimeout = environment.getProperty("spring.cloud.openfeign.client.config.service-product.read-timeout", "6000");
            
            result.put("配置信息", Map.of(
                    "连接超时时间", connectTimeout + "ms",
                    "读取超时时间", readTimeout + "ms",
                    "降级类", "ProductFeignClientFallback.class",
                    "负载均衡", "默认轮询策略"));

            return ApiResponse.success("OpenFeign调用完成", result);
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - start;
            result.put("错误信息", e.getMessage());
            result.put("调用耗时", elapsed + "ms");
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "服务调用失败");
        }
    }

    /**
     * 14. 改进版 - 负载均衡(展示多种策略)
     */
    @GetMapping("/demo/load-balance")
    @Operation(summary = "负载均衡策略增强版")
    public ApiResponse<Object> loadBalanceEnhancedDemo(
            @RequestParam(value = "times", defaultValue = "10") int times) {
        Map<String, Object> result = new HashMap<>();

        // 获取服务实例信息
        List<ServiceInstance> instances = discoveryClient.getInstances("service-product");
        result.put("service-product实例数", instances.size());

        List<Map<String, Object>> instanceDetails = instances.stream().map(inst -> {
            Map<String, Object> detail = new HashMap<>();
            detail.put("地址", inst.getHost() + ":" + inst.getPort());
            detail.put("元数据", inst.getMetadata());
            return detail;
        }).toList();
        result.put("实例详情", instanceDetails);

        // 执行多次调用,统计负载均衡效果
        Map<String, Integer> portCount = new HashMap<>();
        List<String> callSequence = new ArrayList<>();

        for (int i = 1; i <= times; i++) {
            Product product = productFeignClient.getProductById(1L);
            String port = product.getPort();
            portCount.put(port, portCount.getOrDefault(port, 0) + 1);
            callSequence.add("第" + i + "次 -> " + port);
        }

        result.put("调用序列", callSequence);
        result.put("端口分布统计", portCount);
        result.put("负载均衡策略", "默认轮询(Round Robin)");
        result.put("其他支持策略", new String[] {
                "RandomLoadBalancer - 随机策略",
                "WeightedResponseTimeLoadBalancer - 加权响应时间",
                "自定义策略 - 实现 ReactorServiceInstanceLoadBalancer 接口"
        });
        result.put("配置方式", "在配置类中通过 @Bean 注入自定义 LoadBalancer");

        return ApiResponse.success("负载均衡测试完成", result);
    }
}
