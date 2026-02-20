package indi.mofan.order.controller.redis;

import indi.mofan.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("/redis/control")
@RequiredArgsConstructor
public class RedisScenarioController {

    private final StringRedisTemplate stringRedisTemplate;
    private final AtomicBoolean failureInjection = new AtomicBoolean(false);

    @PostMapping("/reset")
    public ApiResponse<Object> resetRedisDemoState() {
        int deleted = 0;
        deleted += deleteByPattern("test:*");
        deleted += deleteByPattern("lock:*");
        deleted += deleteByPattern("cache:*");
        deleted += deleteByPattern("rate:*");

        failureInjection.set(false);

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("resetApplied", true);
        payload.put("deletedKeyCount", deleted);
        payload.put("failureInjection", false);
        return ApiResponse.success("redis scenario state reset", payload);
    }

    @PostMapping("/failure-injection")
    public ApiResponse<Object> setFailureInjection(@RequestParam(value = "enabled", defaultValue = "true") boolean enabled) {
        failureInjection.set(enabled);
        return ApiResponse.success("redis failure injection updated", Map.of(
                "enabled", failureInjection.get()
        ));
    }

    private int deleteByPattern(String pattern) {
        Set<String> keys = stringRedisTemplate.keys(pattern);
        if (keys == null || keys.isEmpty()) {
            return 0;
        }
        Long deleted = stringRedisTemplate.delete(keys);
        return deleted == null ? 0 : deleted.intValue();
    }
}
