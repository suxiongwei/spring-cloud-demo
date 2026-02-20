package indi.mofan.order.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import indi.mofan.common.ApiResponse;
import indi.mofan.common.ResultCode;
import indi.mofan.order.bean.Order;
import indi.mofan.order.common.BlockDetailFormatter;
import indi.mofan.order.facade.OrderBasicFacade;
import indi.mofan.order.facade.OrderDemoFacade;
import indi.mofan.order.facade.RocketMqDemoFacade;
import indi.mofan.order.service.CommonResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author xiongweisu
 * @date 2025/3/23 17:34
 */
@Slf4j
@Tag(name = "订单接口")
@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderBasicFacade orderBasicFacade;
    private final OrderDemoFacade orderDemoFacade;
    private final RocketMqDemoFacade rocketMqDemoFacade;
    private final CommonResourceService commonResourceService;

    @GetMapping("/config")
    @Operation(summary = "查看订单服务配置")
    public ApiResponse<String> config() {
        return orderBasicFacade.config();
    }

    @GetMapping("/create")
    @Operation(summary = "创建订单")
    public ApiResponse<Order> createOrder(@RequestParam("userId") Long userId,
            @RequestParam("productId") Long productId) {
        return orderBasicFacade.createOrder(userId, productId);
    }

    @GetMapping("/rateLimit/qps")
    @SentinelResource(value = "rateLimit-qps", blockHandler = "qpsBlockHandler")
    public ApiResponse<String> rateLimitByQps() {
        return ApiResponse.success("QPS限流测试接口调用成功");
    }

    public ApiResponse<String> qpsBlockHandler(BlockException ex) {
        log.warn("QPS限流触发: {}", BlockDetailFormatter.format(ex));
        return ApiResponse.fail(ResultCode.TOO_MANY_REQUESTS, "接口访问过于频繁，请稍后再试=> " + BlockDetailFormatter.format(ex));
    }

    @GetMapping("/rateLimit/thread")
    @SentinelResource(value = "rateLimit-thread", blockHandler = "threadBlockHandler")
    public ApiResponse<String> rateLimitByThread() throws InterruptedException {
        Thread.sleep(8000);
        return ApiResponse.success("并发线程数限流测试接口调用成功");
    }

    public ApiResponse<String> threadBlockHandler(BlockException ex) {
        log.warn("并发线程数限流触发: {}", BlockDetailFormatter.format(ex));
        return ApiResponse.fail(ResultCode.TOO_MANY_REQUESTS, "系统繁忙，请求排队中=> " + BlockDetailFormatter.format(ex));
    }

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

    @GetMapping("/system/protection")
    @SentinelResource(value = "system-protection", blockHandler = "systemProtectionBlockHandler")
    public ApiResponse<String> systemProtection() throws InterruptedException {
        Thread.sleep(200);
        return ApiResponse.success("系统自适应保护测试接口调用成功");
    }

    public ApiResponse<String> systemProtectionBlockHandler(BlockException ex) {
        log.warn("系统自适应保护触发: {}", BlockDetailFormatter.format(ex));
        return ApiResponse.fail(ResultCode.TOO_MANY_REQUESTS, "系统负载过高，请稍后再试=> " + BlockDetailFormatter.format(ex));
    }

    @GetMapping("/writeDb")
    @SentinelResource(value = "write-db")
    public ApiResponse<String> writeDb() {
        log.info("writeDb=>");
        try {
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

    @GetMapping("/entry/a")
    public ApiResponse<String> entryA() {
        return commonResourceService.commonResource("链路A");
    }

    @GetMapping("/entry/b")
    public ApiResponse<String> entryB() {
        return commonResourceService.commonResource("链路B");
    }

    @GetMapping("/degrade/rt")
    @SentinelResource(value = "degrade-rt", fallback = "degradeRtFallback", blockHandler = "degradeRtBlockHandler")
    public ApiResponse<String> degradeByRt() throws InterruptedException {
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

    @GetMapping("/demo/nacos/services")
    @Operation(summary = "Nacos 服务注册与发现演示")
    public ApiResponse<Object> nacosServicesDemo() {
        return orderDemoFacade.nacosServicesDemo();
    }

    @GetMapping("/demo/tracing")
    @Operation(summary = "链路追踪与日志演示")
    public ApiResponse<Object> tracingDemo() {
        return orderDemoFacade.tracingDemo();
    }

    @GetMapping("/demo/gateway-routing")
    @Operation(summary = "网关路由与过滤演示")
    public ApiResponse<Object> gatewayRoutingDemo() {
        return orderDemoFacade.gatewayRoutingDemo();
    }

    @GetMapping("/demo/nacos-config")
    @Operation(summary = "Nacos 配置动态更新演示")
    public ApiResponse<Object> nacosConfigDemo() {
        return orderDemoFacade.nacosConfigDemo();
    }

    @GetMapping("/demo/async-parallel")
    @Operation(summary = "异步并发处理演示")
    public ApiResponse<Object> asyncParallelDemo() {
        return orderDemoFacade.asyncParallelDemo();
    }

    @GetMapping("/demo/timeout-retry")
    @Operation(summary = "超时与重试机制演示")
    public ApiResponse<Object> timeoutRetryDemo(@RequestParam(value = "delay", defaultValue = "0") int delay,
            @RequestParam(value = "productId", defaultValue = "1") Long productId) {
        return orderDemoFacade.timeoutRetryDemo(delay, productId);
    }

    @GetMapping("/demo/feign-interceptor")
    @Operation(summary = "Feign请求拦截器演示")
    public ApiResponse<Object> feignInterceptorDemo() {
        return orderDemoFacade.feignInterceptorDemo();
    }

    @GetMapping("/demo/config-refresh")
    @Operation(summary = "Nacos配置动态刷新演示")
    public ApiResponse<Object> configRefreshDemo() {
        return orderDemoFacade.configRefreshDemo();
    }

    @GetMapping("/demo/health-check")
    @Operation(summary = "服务健康检查演示")
    public ApiResponse<Object> healthCheckDemo() {
        return orderDemoFacade.healthCheckDemo();
    }

    @GetMapping("/demo/feign/call-enhanced")
    @Operation(summary = "OpenFeign调用增强版(含降级演示)")
    public ApiResponse<Object> feignCallEnhancedDemo(@RequestParam("productId") Long productId) {
        return orderDemoFacade.feignCallEnhancedDemo(productId);
    }

    @GetMapping("/demo/load-balance")
    @Operation(summary = "负载均衡策略增强版")
    public ApiResponse<Object> loadBalanceEnhancedDemo(
            @RequestParam(value = "times", defaultValue = "10") int times) {
        return orderDemoFacade.loadBalanceEnhancedDemo(times);
    }

    @GetMapping("/demo/rocketmq/publish-basic")
    @Operation(summary = "RocketMQ 场景01：订单创建后异步通知库存/营销")
    public ApiResponse<Object> rocketMqPublishBasicDemo() {
        return rocketMqDemoFacade.publishBasicDemo();
    }

    @GetMapping("/demo/rocketmq/retry")
    @Operation(summary = "RocketMQ 场景02：消费失败自动重试")
    public ApiResponse<Object> rocketMqRetryDemo() {
        return rocketMqDemoFacade.retryDemo();
    }

    @GetMapping("/demo/rocketmq/dlq")
    @Operation(summary = "RocketMQ 场景03：死信队列隔离")
    public ApiResponse<Object> rocketMqDlqDemo() {
        return rocketMqDemoFacade.dlqDemo();
    }

    @GetMapping("/demo/rocketmq/idempotent")
    @Operation(summary = "RocketMQ 场景04：幂等消费")
    public ApiResponse<Object> rocketMqIdempotentDemo() {
        return rocketMqDemoFacade.idempotentDemo();
    }

    @GetMapping("/demo/rocketmq/orderly")
    @Operation(summary = "RocketMQ 场景05：顺序消息")
    public ApiResponse<Object> rocketMqOrderlyDemo() {
        return rocketMqDemoFacade.orderlyDemo();
    }

    @GetMapping("/demo/rocketmq/delay-close")
    @Operation(summary = "RocketMQ 场景06：延迟消息自动关单")
    public ApiResponse<Object> rocketMqDelayCloseDemo() {
        return rocketMqDemoFacade.delayCloseDemo();
    }

    @GetMapping("/demo/rocketmq/tx/send")
    @Operation(summary = "RocketMQ 场景07：事务消息发送")
    public ApiResponse<Object> rocketMqTxSendDemo() {
        return rocketMqDemoFacade.txSendDemo();
    }

    @GetMapping("/demo/rocketmq/tx/check")
    @Operation(summary = "RocketMQ 场景08：事务回查")
    public ApiResponse<Object> rocketMqTxCheckDemo() {
        return rocketMqDemoFacade.txCheckDemo();
    }

    @GetMapping("/demo/rocketmq/tag-filter")
    @Operation(summary = "RocketMQ 场景09：Tag 路由过滤")
    public ApiResponse<Object> rocketMqTagFilterDemo() {
        return rocketMqDemoFacade.tagFilterDemo();
    }

    @GetMapping("/demo/rocketmq/replay-dlq")
    @Operation(summary = "RocketMQ 场景10：死信重放与补偿")
    public ApiResponse<Object> rocketMqReplayDlqDemo() {
        return rocketMqDemoFacade.replayDlqDemo();
    }

    @GetMapping("/demo/guided-flow")
    @Operation(summary = "引导式面试流程编排")
    public ApiResponse<Object> guidedInterviewFlow(
            @RequestParam(value = "gatewayBaseUrl", defaultValue = "http://localhost:9090") String gatewayBaseUrl) {
        return orderDemoFacade.guidedInterviewFlow(gatewayBaseUrl);
    }
}
