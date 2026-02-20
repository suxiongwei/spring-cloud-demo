package indi.mofan.order.controller.redis;

import indi.mofan.order.service.redis.RedisScenarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class RedisLockController {
    private final RedisScenarioService redisScenarioService;

    @GetMapping("/lock/basic")
    public RedisTestResult testBasicLock() {
        return redisScenarioService.testBasicLock();
    }

    @GetMapping("/lock/reentrant")
    public RedisTestResult testReentrantLock() {
        return redisScenarioService.testReentrantLock();
    }

    @GetMapping("/lock/renewal")
    public RedisTestResult testLockRenewal() {
        return redisScenarioService.testLockRenewal();
    }

    @GetMapping("/lock/redlock")
    public RedisTestResult testRedLock() {
        return redisScenarioService.testRedLock();
    }
}
