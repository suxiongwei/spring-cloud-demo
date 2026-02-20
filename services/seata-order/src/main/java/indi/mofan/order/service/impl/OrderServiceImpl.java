package indi.mofan.order.service.impl;

import indi.mofan.order.bean.OrderTbl;
import indi.mofan.order.feign.AccountFeignClient;
import indi.mofan.order.mapper.OrderTblMapper;
import indi.mofan.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderTblMapper orderTblMapper;

    @Autowired
    AccountFeignClient accountFeignClient;

    @Override
    @Transactional
    public OrderTbl create(String userId, String commodityCode, int orderCount, boolean fail) {
        // 1. 计算订单价格
        int orderMoney = calculate(commodityCode, orderCount);
        // 2. 扣减账户余额
        accountFeignClient.debit(userId, orderMoney);
        // 3. 保存订单
        OrderTbl orderTbl = new OrderTbl();
        orderTbl.setUserId(userId);
        orderTbl.setCommodityCode(commodityCode);
        orderTbl.setCount(orderCount);
        orderTbl.setMoney(orderMoney);

        // 4. 保存订单
        orderTblMapper.insert(orderTbl);

        // 可选失败触发
        if (fail) {
            throw new RuntimeException("模拟订单失败，触发全局回滚");
        }

        return orderTbl;
    }

    // 计算价格
    private int calculate(String commodityCode, int orderCount) {
        return 9 * orderCount;
    }

    @Override
    public int countByUserAndCommodity(String userId, String commodityCode) {
        Integer count = orderTblMapper.countByUserAndCommodity(userId, commodityCode);
        return count == null ? 0 : count;
    }
}
