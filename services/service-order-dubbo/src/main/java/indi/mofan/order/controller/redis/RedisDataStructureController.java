package indi.mofan.order.controller.redis;

import indi.mofan.order.service.redis.RedisScenarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class RedisDataStructureController {
    private final RedisScenarioService redisScenarioService;

    @GetMapping("/string")
    public RedisTestResult testString() {
        return redisScenarioService.testString();
    }

    @GetMapping("/hash")
    public RedisTestResult testHash() {
        return redisScenarioService.testHash();
    }

    @GetMapping("/list")
    public RedisTestResult testList() {
        return redisScenarioService.testList();
    }

    @GetMapping("/set")
    public RedisTestResult testSet() {
        return redisScenarioService.testSet();
    }

    @GetMapping("/zset")
    public RedisTestResult testZSet() {
        return redisScenarioService.testZSet();
    }
}
