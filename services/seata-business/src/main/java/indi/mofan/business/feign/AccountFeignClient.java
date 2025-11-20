package indi.mofan.business.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "accountFeign", value = "seata-account")
public interface AccountFeignClient {
    @GetMapping("/debit")
    String debit(@RequestParam("userId") String userId,
                 @RequestParam("money") int money);

    @GetMapping("/addBack")
    String addBack(@RequestParam("userId") String userId,
                   @RequestParam("money") int money);
}