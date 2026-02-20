package indi.mofan.order.controller.redis;

import indi.mofan.order.service.redis.RedisScenarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class RedisRateLimitController {
    private final RedisScenarioService redisScenarioService;

    @GetMapping("/rate-limit/fixed")
    public RedisTestResult testFixedWindow() {
        return redisScenarioService.testFixedWindow();
    }

    @GetMapping("/rate-limit/sliding")
    public RedisTestResult testSlidingWindow() {
        return redisScenarioService.testSlidingWindow();
    }

    @GetMapping("/rate-limit/token")
    public RedisTestResult testTokenBucket() {
        return redisScenarioService.testTokenBucket();
    }

    @GetMapping("/rate-limit/leaky")
    public RedisTestResult testLeakyBucket() {
        return redisScenarioService.testLeakyBucket();
    }
}
