package indi.mofan.order.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import indi.mofan.order.common.BlockDetailFormatter;

import indi.mofan.order.bean.Order;
import indi.mofan.order.common.ApiResponse;
import indi.mofan.order.common.ResultCode;
import indi.mofan.order.properties.OrderProperties;
import indi.mofan.order.service.CommonResourceService;
import indi.mofan.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author mofan
 * @date 2025/3/23 17:34
 */
@Slf4j
// @RequestMapping("/api/order")
@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderProperties orderProperties;
    @Autowired
    private CommonResourceService commonResourceService;

    

    @GetMapping("/config")
    public ApiResponse<String> config() {
        String msg = "order timeout: " + orderProperties.getTimeout()
            + " auto-confirm: " + orderProperties.getAutoConfirm()
            + " db-url: " + orderProperties.getDbUrl();
        return ApiResponse.success(msg);
    }

    @GetMapping("/create")
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
        Thread.sleep(1000);
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
        return ApiResponse.fail(ResultCode.TOO_MANY_REQUESTS, "系统繁忙，请稍后再试（writeDb 并发过高）=> " + BlockDetailFormatter.format(ex));
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
    public ApiResponse<String> authorityControl(@RequestParam(value = "userType", defaultValue = "normal") String userType) {
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
    public ApiResponse<String> authorityControlBlack(@RequestParam(value = "userType", defaultValue = "normal") String userType) {
        return ApiResponse.success("黑名单控制测试，用户类型: " + userType);
    }
}
