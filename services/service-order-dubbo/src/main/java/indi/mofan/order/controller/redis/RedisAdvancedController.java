package indi.mofan.order.controller.redis;

import indi.mofan.order.service.redis.RedisScenarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class RedisAdvancedController {
    private final RedisScenarioService redisScenarioService;

    @GetMapping("/pipeline")
    public RedisTestResult testPipeline() {
        return redisScenarioService.testPipeline();
    }

    @GetMapping("/transaction")
    public RedisTestResult testTransaction() {
        return redisScenarioService.testTransaction();
    }

    @GetMapping("/watch")
    public RedisTestResult testWatch() {
        return redisScenarioService.testWatch();
    }

    @GetMapping("/lua")
    public RedisTestResult testLua() {
        return redisScenarioService.testLua();
    }

    @GetMapping("/pubsub")
    public RedisTestResult testPubSub() {
        return redisScenarioService.testPubSub();
    }

    @GetMapping("/pubsub/pattern")
    public RedisTestResult testPatternPubSub() {
        return redisScenarioService.testPatternPubSub();
    }

    @GetMapping("/persistence/rdb")
    public RedisTestResult testRDB() {
        return redisScenarioService.testRDB();
    }

    @GetMapping("/persistence/aof")
    public RedisTestResult testAOF() {
        return redisScenarioService.testAOF();
    }

    @GetMapping("/monitor")
    public RedisTestResult testMonitor() {
        return redisScenarioService.testMonitor();
    }
}
