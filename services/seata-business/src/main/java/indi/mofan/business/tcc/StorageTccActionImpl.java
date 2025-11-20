package indi.mofan.business.tcc;

import indi.mofan.business.feign.StorageFeignClient;

import org.apache.seata.core.context.RootContext;
import org.apache.seata.rm.tcc.api.BusinessActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class StorageTccActionImpl implements StorageTccAction {

    private static final ConcurrentHashMap<String, Boolean> tried = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Boolean> committed = new ConcurrentHashMap<>();

    @Autowired
    private StorageFeignClient storageFeignClient;

    @Override
    public boolean prepare(BusinessActionContext ctx, String commodityCode, int count) {
        String xid = (ctx != null && ctx.getXid() != null) ? ctx.getXid() : RootContext.getXID();
        String key = xid + ":sto:" + commodityCode;
        if (tried.putIfAbsent(key, true) != null) {
            return true;
        }
        storageFeignClient.deduct(commodityCode, count);
        return true;
    }

    @Override
    public boolean commit(BusinessActionContext ctx) {
        String xid = (ctx != null && ctx.getXid() != null) ? ctx.getXid() : RootContext.getXID();
        String commodityCode = (String) ctx.getActionContext("commodityCode");
        String key = xid + ":sto:" + commodityCode;
        committed.put(key, true);
        return true;
    }

    @Override
    public boolean cancel(BusinessActionContext ctx) {
        String xid = (ctx != null && ctx.getXid() != null) ? ctx.getXid() : RootContext.getXID();
        String commodityCode = (String) ctx.getActionContext("commodityCode");
        Integer count = (Integer) ctx.getActionContext("count");
        String key = xid + ":sto:" + commodityCode;
        if (committed.containsKey(key)) {
            return true;
        }
        if (count != null) {
            storageFeignClient.addBack(commodityCode, count);
        }
        return true;
    }
}