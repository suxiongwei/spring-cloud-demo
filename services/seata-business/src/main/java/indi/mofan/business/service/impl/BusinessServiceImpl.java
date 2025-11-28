package indi.mofan.business.service.impl;

import indi.mofan.business.feign.OrderFeignClient;
import indi.mofan.business.feign.StorageFeignClient;
import indi.mofan.business.tcc.AccountTccAction;
import indi.mofan.business.tcc.StorageTccAction;
import indi.mofan.business.service.BusinessService;
import org.apache.seata.spring.annotation.GlobalTransactional;
import org.apache.seata.core.context.RootContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class BusinessServiceImpl implements BusinessService {

    @Autowired
    private StorageFeignClient storageFeignClient;

    @Autowired
    private OrderFeignClient orderFeignClient;

    @Autowired
    private AccountTccAction accountTccAction;

    @Autowired
    private StorageTccAction storageTccAction;

    @Override
    @GlobalTransactional
    public void purchase(String userId, String commodityCode, int orderCount) {
        String xid = RootContext.getXID();
        log.info("xid={} purchase", xid);
        storageFeignClient.deduct(commodityCode, orderCount);
        orderFeignClient.create(userId, commodityCode, orderCount, false);
    }

    @GlobalTransactional
    public void purchaseTcc(String userId, String commodityCode, int orderCount, boolean fail) {
        String xid = RootContext.getXID();
        log.info("xid={} purchaseTcc", xid);
        storageTccAction.prepare(null, commodityCode, orderCount);
        int money = 9 * orderCount;
        accountTccAction.prepare(null, userId, money);
        orderFeignClient.create(userId, commodityCode, orderCount, fail);
    }
}
