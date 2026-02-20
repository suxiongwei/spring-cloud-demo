package indi.mofan.order.controller;


import indi.mofan.order.bean.OrderTbl;
import indi.mofan.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class OrderRestController {

    @Autowired
    OrderService orderService;

    /**
     * 创建订单
     */
    @GetMapping("/create")
    public String create(@RequestParam("userId") String userId,
                         @RequestParam("commodityCode") String commodityCode,
                         @RequestParam("count") int orderCount,
                         @RequestParam(value = "fail", required = false, defaultValue = "false") boolean fail) {
        OrderTbl tbl = orderService.create(userId, commodityCode, orderCount, fail);
        return "order create success = 订单id：[" + tbl.getId() + "]";
    }

    @GetMapping("/snapshot")
    public Map<String, Object> snapshot(@RequestParam("userId") String userId,
                                        @RequestParam("commodityCode") String commodityCode) {
        int count = orderService.countByUserAndCommodity(userId, commodityCode);
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("commodityCode", commodityCode);
        result.put("orderCount", count);
        return result;
    }
}
