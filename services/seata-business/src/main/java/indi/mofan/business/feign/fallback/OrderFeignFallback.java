package indi.mofan.business.feign.fallback;

import indi.mofan.business.feign.OrderFeignClient;
import org.springframework.stereotype.Component;

@Component
public class OrderFeignFallback implements OrderFeignClient {
    @Override
    public String create(String userId, String commodityCode, int orderCount, Boolean fail) {
        throw new RuntimeException("order service unavailable");
    }
}