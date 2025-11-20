package indi.mofan.business.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "accountTccFeign", value = "seata-account")
public interface AccountTccFeignClient {
    @GetMapping("/tcc/debit")
    String tccDebit(@RequestParam("userId") String userId,
                    @RequestParam("money") int money);
}