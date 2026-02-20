package indi.mofan.business.feign.fallback;

import indi.mofan.business.feign.OrderFeignClient;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OrderFeignFallback implements OrderFeignClient {
    @Override
    public String create(String userId, String commodityCode, int orderCount, Boolean fail) {
        throw new RuntimeException("order service unavailable");
    }

    @Override
    public Map<String, Object> snapshot(String userId, String commodityCode) {
        throw new RuntimeException("order service unavailable");
    }
}
