package indi.mofan.business.feign.fallback;

import indi.mofan.business.feign.StorageFeignClient;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class StorageFeignFallback implements StorageFeignClient {
    @Override
    public String deduct(String commodityCode, Integer count) {
        throw new RuntimeException("storage service unavailable");
    }

    @Override
    public String addBack(String commodityCode, Integer count) {
        throw new RuntimeException("storage service unavailable");
    }

    @Override
    public Map<String, Object> snapshot(String commodityCode) {
        throw new RuntimeException("storage service unavailable");
    }
}
