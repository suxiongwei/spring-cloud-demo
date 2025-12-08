package indi.mofan.business.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author xiongweisu
 * @date 2025/5/1 16:21
 */
@FeignClient(contextId = "storageFeign", value = "seata-storage", fallback = indi.mofan.business.feign.fallback.StorageFeignFallback.class)
public interface StorageFeignClient {
    /**
     * 扣减库存
     */
    @GetMapping("/deduct")
    String deduct(@RequestParam("commodityCode") String commodityCode,
                  @RequestParam("count") Integer count);

    @GetMapping("/addBack")
    String addBack(@RequestParam("commodityCode") String commodityCode,
                   @RequestParam("count") Integer count);
}
