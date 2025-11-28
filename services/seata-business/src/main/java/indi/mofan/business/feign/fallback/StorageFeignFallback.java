package indi.mofan.business.feign.fallback;

import indi.mofan.business.feign.StorageFeignClient;
import org.springframework.stereotype.Component;

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
}