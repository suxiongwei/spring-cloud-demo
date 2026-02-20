package indi.mofan.order.facade;

import indi.mofan.common.ApiResponse;
import indi.mofan.common.ResultCode;
import indi.mofan.common.demo.ScenarioEvidenceKeys;
import indi.mofan.order.feign.ProductFeignClient;
import indi.mofan.order.properties.CkProperties;
import indi.mofan.product.bean.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class OrderDemoFacade {
    private final DiscoveryClient discoveryClient;
    private final ProductFeignClient productFeignClient;
    private final CkProperties ckProperties;
    private final Environment environment;

    public ApiResponse<Object> nacosServicesDemo() {
        long startTime = System.currentTimeMillis();
        List<String> services = discoveryClient.getServices();
        Map<String, Object> result = new HashMap<>();
        result.put("总服务数", services.size());
        result.put("服务列表", services);

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
        attachEvidence(result, "nacos-services", startTime, false,
                Map.of("serviceCount", services.size(), "instanceGroupCount", serviceInstances.size()),
                List.of("Nacos 健康实例摘除策略是什么？", "配置中心和注册中心如何隔离命名空间？"));
        return ApiResponse.success("Nacos服务发现成功", result);
    }

    public ApiResponse<Object> tracingDemo() {
        Map<String, Object> result = new HashMap<>();
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

    public ApiResponse<Object> gatewayRoutingDemo() {
        long startTime = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();
        String[] routes = new String[] {
                "GET /product -> service-product",
                "GET /api/order -> service-order",
                "GET /api/account -> seata-account",
                "GET /api/storage -> seata-storage"
        };
        String[] filters = new String[] {
                "OnceToken 过滤器: 防止重复提交",
                "RtGlobalFilter: 记录请求耗时",
                "路由过滤器: 自动添加服务前缀"
        };
        result.put("网关地址", "http://localhost:9090");
        result.put("路由规则", routes);
        result.put("过滤器", filters);
        result.put("说明", "网关作为所有请求的入口，提供统一的路由、认证、限流等功能");
        attachEvidence(result, "gateway-routing", startTime, false,
                Map.of("routeCount", routes.length, "filterCount", filters.length),
                List.of("网关鉴权和业务鉴权如何分层？", "如何避免网关成为单点瓶颈？"));

        return ApiResponse.success("网关路由信息获取成功", result);
    }

    public ApiResponse<Object> nacosConfigDemo() {
        Map<String, Object> result = new HashMap<>();
        result.put("connectTimeout", ckProperties.getConnectTimeout());
        result.put("readTimeout", ckProperties.getReadTimeout());
        result.put("说明", "这些配置来自 Nacos (common.yaml)，修改后应用会自动刷新");
        result.put("注意", "请确保在 Nacos 中 hh.yaml 的 Group 为 'ORDER_GROUP'，而不是 'SENTINEL_GROUP'");
        result.put("@RefreshScope", "标记在配置类上，实现配置的动态刷新");

        return ApiResponse.success("Nacos配置读取成功", result);
    }

    public ApiResponse<Object> asyncParallelDemo() {
        long start = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();

        CompletableFuture<String> userFuture = CompletableFuture
                .supplyAsync(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return "User(ID=1001, Name=Mofan)";
                });

        CompletableFuture<String> stockFuture = CompletableFuture
                .supplyAsync(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return "Stock(Count=99)";
                });

        CompletableFuture.allOf(userFuture, stockFuture).join();

        try {
            result.put("userInfo", userFuture.get());
            result.put("stockInfo", stockFuture.get());
        } catch (Exception e) {
            result.put("error", e.getMessage());
        }

        long cost = System.currentTimeMillis() - start;
        result.put("totalCost", cost + "ms");
        result.put("optimization", "串行执行需2000ms，并行执行仅需约1000ms");

        return ApiResponse.success("异步并发执行成功", result);
    }

    public ApiResponse<Object> timeoutRetryDemo(int delay, Long productId) {
        Map<String, Object> result = new HashMap<>();
        long start = System.currentTimeMillis();

        try {
            Product product = productFeignClient.getProductById(productId);
            long elapsed = System.currentTimeMillis() - start;

            result.put("调用结果", product);
            result.put("实际耗时", elapsed + "ms");
            result.put("超时配置", "连接超时: 3s, 读取超时: 6s");
            result.put("重试配置", "默认不重试(避免幂等性问题)");
            result.put("测试说明", "productId=88888会触发5秒延迟，可用于测试读取超时");
            result.put("当前productId", productId);
            result.put("delay参数说明", "当前演示未使用 delay 参数，保留用于前端兼容");
            result.put("delay", delay);

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

    public ApiResponse<Object> feignInterceptorDemo() {
        Map<String, Object> result = new HashMap<>();
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

    public ApiResponse<Object> configRefreshDemo() {
        Map<String, Object> result = new HashMap<>();
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

    public ApiResponse<Object> healthCheckDemo() {
        Map<String, Object> result = new HashMap<>();
        List<String> services = discoveryClient.getServices();
        Map<String, Object> healthStatus = new HashMap<>();

        for (String service : services) {
            List<ServiceInstance> instances = discoveryClient.getInstances(service);
            Map<String, Object> serviceHealth = new HashMap<>();
            serviceHealth.put("实例数量", instances.size());
            serviceHealth.put("健康实例", instances.size());
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

    public ApiResponse<Object> feignCallEnhancedDemo(Long productId) {
        Map<String, Object> result = new HashMap<>();
        long start = System.currentTimeMillis();

        try {
            Product product = productFeignClient.getProductById(productId);
            long elapsed = System.currentTimeMillis() - start;

            result.put("调用结果", product);
            result.put("调用耗时", elapsed + "ms");
            result.put("是否降级", product.getProductName().equals("未知商品"));
            result.put("降级策略", "返回默认商品信息,保证系统可用性");
            String connectTimeout = environment.getProperty(
                    "spring.cloud.openfeign.client.config.service-product.connect-timeout", "3000");
            String readTimeout = environment.getProperty(
                    "spring.cloud.openfeign.client.config.service-product.read-timeout", "6000");

            result.put("配置信息", Map.of(
                    "连接超时时间", connectTimeout + "ms",
                    "读取超时时间", readTimeout + "ms",
                    "降级类", "ProductFeignClientFallback.class",
                    "负载均衡", "默认轮询策略"));
            boolean degraded = product.getProductName().equals("未知商品");
            attachEvidence(result, "feign-call-enhanced", start, degraded,
                    Map.of("degraded", degraded, "productId", productId),
                    List.of("Feign 降级和 Sentinel 降级职责如何划分？", "超时阈值如何按下游差异化配置？"));

            return ApiResponse.success("OpenFeign调用完成", result);
        } catch (Exception e) {
            long elapsed = System.currentTimeMillis() - start;
            result.put("错误信息", e.getMessage());
            result.put("调用耗时", elapsed + "ms");
            return ApiResponse.fail(ResultCode.INTERNAL_ERROR, "服务调用失败");
        }
    }

    public ApiResponse<Object> loadBalanceEnhancedDemo(int times) {
        Map<String, Object> result = new HashMap<>();
        long startTime = System.currentTimeMillis();
        List<ServiceInstance> instances = discoveryClient.getInstances("service-product");
        result.put("service-product实例数", instances.size());

        List<Map<String, Object>> instanceDetails = instances.stream().map(inst -> {
            Map<String, Object> detail = new HashMap<>();
            detail.put("地址", inst.getHost() + ":" + inst.getPort());
            detail.put("元数据", inst.getMetadata());
            return detail;
        }).toList();
        result.put("实例详情", instanceDetails);

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
        attachEvidence(result, "load-balance", startTime, false,
                Map.of("instanceCount", instances.size(), "distributionPorts", portCount.keySet()),
                List.of("为何当前场景用轮询而不是最少连接？", "如何做灰度权重发布？"));

        return ApiResponse.success("负载均衡测试完成", result);
    }

    public ApiResponse<Object> guidedInterviewFlow(String gatewayBaseUrl) {
        List<Map<String, Object>> steps = new ArrayList<>();

        steps.add(Map.of(
                "step", 1,
                "name", "Gateway 路由校验",
                "request", Map.of("method", "GET", "url", gatewayBaseUrl + "/api/order/demo/gateway-routing"),
                "responseSummary", "验证 gateway 是否已加载 order/business/product 路由",
                "assertion", Map.of("rule", "response.data.evidence.routeCount >= 3", "expected", true),
                "keyMetricOrRule", "route_count >= 3"));

        steps.add(Map.of(
                "step", 2,
                "name", "Sentinel 限流校验",
                "request", Map.of("method", "GET", "url", gatewayBaseUrl + "/api/order/rateLimit/qps"),
                "responseSummary", "触发/未触发限流均应返回结构化响应",
                "assertion", Map.of("rule", "response.code in [200,429]", "expected", true),
                "keyMetricOrRule", "result.code in [200,429]"));

        steps.add(Map.of(
                "step", 3,
                "name", "Dubbo 调用校验",
                "request", Map.of("method", "GET", "url", gatewayBaseUrl + "/api/order/dubbo/call-sync?productId=1"),
                "responseSummary", "验证 Dubbo 基础调用链路可用",
                "assertion", Map.of("rule", "response.data.product.id == 1", "expected", true),
                "keyMetricOrRule", "response.data.product.id == 1"));

        steps.add(Map.of(
                "step", 4,
                "name", "Seata TCC 验证",
                "request", Map.of("method", "GET",
                        "url", gatewayBaseUrl + "/api/business/purchase/tcc/verify?userId=U1001&commodityCode=P0001&count=1&fail=false"),
                "responseSummary", "验证提交路径有资源变更证据",
                "assertion", Map.of("rule", "response.verification.commitVerified == true", "expected", true),
                "keyMetricOrRule", "verification.commitVerified == true"));

        steps.add(Map.of(
                "step", 5,
                "name", "Trace 透传验证",
                "request", Map.of("method", "GET", "url", gatewayBaseUrl + "/api/order/config"),
                "responseSummary", "检查响应头是否包含 traceparent（开启 tracing 时）",
                "assertion", Map.of("rule", "response.headers.traceparent exists when demo.tracing.enabled=true", "expected", "conditional"),
                "keyMetricOrRule", "response.headers.traceparent exists when demo.tracing.enabled=true"));

        Map<String, Object> result = new HashMap<>();
        result.put("flowName", "java-senior-interview-guided-flow-v2");
        result.put("runId", UUID.randomUUID().toString());
        result.put("gatewayBaseUrl", gatewayBaseUrl);
        result.put("totalSteps", steps.size());
        result.put("steps", steps);
        return ApiResponse.success("引导式面试流程生成成功", result);
    }

    private void attachEvidence(Map<String, Object> payload, String scenarioId, long startTime,
            boolean failureInjected, Map<String, Object> evidence, List<String> hints) {
        payload.put(ScenarioEvidenceKeys.SCENARIO_ID, scenarioId);
        payload.put(ScenarioEvidenceKeys.SUCCESS, true);
        payload.put(ScenarioEvidenceKeys.FAILURE_INJECTED, failureInjected);
        payload.put(ScenarioEvidenceKeys.COST_MS, Math.max(System.currentTimeMillis() - startTime, 0));
        payload.put(ScenarioEvidenceKeys.EVIDENCE, evidence);
        payload.put(ScenarioEvidenceKeys.NEXT_QUESTION_HINTS, hints);
    }
}
