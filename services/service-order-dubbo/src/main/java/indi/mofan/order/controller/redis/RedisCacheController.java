package indi.mofan.order.controller.redis;

import indi.mofan.order.service.redis.RedisScenarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class RedisCacheController {
    private final RedisScenarioService redisScenarioService;

    @GetMapping("/cache/warmup")
    public RedisTestResult testCacheWarmup() {
        return redisScenarioService.testCacheWarmup();
    }

    @GetMapping("/cache/penetration")
    public RedisTestResult testCachePenetration() {
        return redisScenarioService.testCachePenetration();
    }

    @GetMapping("/cache/breakdown")
    public RedisTestResult testCacheBreakdown() {
        return redisScenarioService.testCacheBreakdown();
    }

    @GetMapping("/cache/avalanche")
    public RedisTestResult testCacheAvalanche() {
        return redisScenarioService.testCacheAvalanche();
    }
}
