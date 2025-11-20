package indi.mofan.business.tcc;

import indi.mofan.business.feign.AccountFeignClient;

import org.apache.seata.core.context.RootContext;
import org.apache.seata.rm.tcc.api.BusinessActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class AccountTccActionImpl implements AccountTccAction {

    private static final ConcurrentHashMap<String, Boolean> tried = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Boolean> committed = new ConcurrentHashMap<>();

    @Autowired
    private AccountFeignClient accountFeignClient;

    @Override
    public boolean prepare(BusinessActionContext ctx, String userId, int money) {
        String xid = (ctx != null && ctx.getXid() != null) ? ctx.getXid() : RootContext.getXID();
        String key = xid + ":acc:" + userId;
        if (tried.putIfAbsent(key, true) != null) {
            return true;
        }
        accountFeignClient.debit(userId, money);
        return true;
    }

    @Override
    public boolean commit(BusinessActionContext ctx) {
        String xid = (ctx != null && ctx.getXid() != null) ? ctx.getXid() : RootContext.getXID();
        String userId = (String) ctx.getActionContext("userId");
        String key = xid + ":acc:" + userId;
        committed.put(key, true);
        return true;
    }

    @Override
    public boolean cancel(BusinessActionContext ctx) {
        String xid = (ctx != null && ctx.getXid() != null) ? ctx.getXid() : RootContext.getXID();
        String userId = (String) ctx.getActionContext("userId");
        Integer money = (Integer) ctx.getActionContext("money");
        String key = xid + ":acc:" + userId;
        if (committed.containsKey(key)) {
            return true;
        }
        if (money != null) {
            accountFeignClient.addBack(userId, money);
        }
        return true;
    }
}