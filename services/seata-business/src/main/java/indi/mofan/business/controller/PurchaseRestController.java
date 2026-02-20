package indi.mofan.business.controller;

import indi.mofan.business.feign.AccountFeignClient;
import indi.mofan.business.feign.OrderFeignClient;
import indi.mofan.business.feign.StorageFeignClient;
import indi.mofan.business.service.BusinessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class PurchaseRestController {

    @Autowired
    BusinessService businessService;
    @Autowired
    AccountFeignClient accountFeignClient;
    @Autowired
    StorageFeignClient storageFeignClient;
    @Autowired
    OrderFeignClient orderFeignClient;

    /**
     * 购买
     */
    @GetMapping("/purchase")
    public String purchase(@RequestParam("userId") String userId,
                           @RequestParam("commodityCode") String commodityCode,
                           @RequestParam("count") int orderCount) {
        businessService.purchase(userId, commodityCode, orderCount);
        return "business purchase success";
    }

    @GetMapping("/purchase/tcc")
    public String purchaseTcc(@RequestParam("userId") String userId,
                              @RequestParam("commodityCode") String commodityCode,
                              @RequestParam("count") int orderCount,
                              @RequestParam(value = "fail", required = false, defaultValue = "false") boolean fail) {
        businessService.purchaseTcc(userId, commodityCode, orderCount, fail);
        return "business purchase tcc success";
    }

    @GetMapping("/purchase/tcc/verify")
    public Map<String, Object> purchaseTccWithVerify(@RequestParam("userId") String userId,
                                                     @RequestParam("commodityCode") String commodityCode,
                                                     @RequestParam("count") int orderCount,
                                                     @RequestParam(value = "fail", required = false, defaultValue = "false") boolean fail) {
        Map<String, Object> before = collectSnapshot(userId, commodityCode);
        boolean success = false;
        String errorMessage = null;

        try {
            businessService.purchaseTcc(userId, commodityCode, orderCount, fail);
            success = true;
        } catch (Exception e) {
            errorMessage = e.getMessage();
        }

        Map<String, Object> after = collectSnapshot(userId, commodityCode);

        int beforeAccountMoney = readInt(before, "account.money");
        int afterAccountMoney = readInt(after, "account.money");
        int beforeStorageCount = readInt(before, "storage.count");
        int afterStorageCount = readInt(after, "storage.count");
        int beforeOrderCount = readInt(before, "order.orderCount");
        int afterOrderCount = readInt(after, "order.orderCount");

        int expectedMoneyDelta = 9 * orderCount;
        int expectedCountDelta = orderCount;

        boolean rollbackVerified = !success
                && beforeAccountMoney == afterAccountMoney
                && beforeStorageCount == afterStorageCount
                && beforeOrderCount == afterOrderCount;

        boolean commitVerified = success
                && afterAccountMoney == (beforeAccountMoney - expectedMoneyDelta)
                && afterStorageCount == (beforeStorageCount - expectedCountDelta)
                && afterOrderCount == (beforeOrderCount + 1);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", success);
        result.put("failInjected", fail);
        result.put("errorMessage", errorMessage);
        result.put("input", Map.of(
                "userId", userId,
                "commodityCode", commodityCode,
                "count", orderCount
        ));
        result.put("before", before);
        result.put("after", after);
        result.put("verification", Map.of(
                "expectedMoneyDelta", expectedMoneyDelta,
                "expectedCountDelta", expectedCountDelta,
                "rollbackVerified", rollbackVerified,
                "commitVerified", commitVerified
        ));
        return result;
    }

    private Map<String, Object> collectSnapshot(String userId, String commodityCode) {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        snapshot.put("account", accountFeignClient.snapshot(userId));
        snapshot.put("storage", storageFeignClient.snapshot(commodityCode));
        snapshot.put("order", orderFeignClient.snapshot(userId, commodityCode));
        return snapshot;
    }

    private int readInt(Map<String, Object> snapshot, String path) {
        String[] keys = path.split("\\.");
        Object current = snapshot;
        for (String key : keys) {
            if (!(current instanceof Map<?, ?> map) || !map.containsKey(key)) {
                return 0;
            }
            current = map.get(key);
        }
        if (current instanceof Number n) {
            return n.intValue();
        }
        if (current instanceof String s) {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException ignored) {
                return 0;
            }
        }
        return 0;
    }
}
