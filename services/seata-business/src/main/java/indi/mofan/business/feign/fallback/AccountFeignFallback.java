package indi.mofan.business.feign.fallback;

import indi.mofan.business.feign.AccountFeignClient;
import org.springframework.stereotype.Component;

@Component
public class AccountFeignFallback implements AccountFeignClient {
    @Override
    public String debit(String userId, int money) {
        throw new RuntimeException("account service unavailable");
    }

    @Override
    public String addBack(String userId, int money) {
        throw new RuntimeException("account service unavailable");
    }
}