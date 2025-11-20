package indi.mofan.business.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(contextId = "storageTccFeign", value = "seata-storage")
public interface StorageTccFeignClient {
    @GetMapping("/tcc/deduct")
    String tccDeduct(@RequestParam("commodityCode") String commodityCode,
                     @RequestParam("count") Integer count);
}