package indi.mofan.storage.tcc;

import indi.mofan.storage.mapper.StorageTblMapper;
import org.apache.seata.rm.tcc.api.BusinessActionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class StorageTccServiceImpl implements StorageTccService {

    private static final ConcurrentHashMap<String, Boolean> tried = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Boolean> committed = new ConcurrentHashMap<>();

    @Autowired
    private StorageTblMapper storageTblMapper;

    @Override
    public boolean prepare(BusinessActionContext ctx, String commodityCode, int count) {
        String xid = ctx != null ? ctx.getXid() : "";
        String key = xid + ":sto:" + commodityCode;
        if (tried.putIfAbsent(key, true) != null) {
            return true;
        }
        storageTblMapper.deduct(commodityCode, count);
        return true;
    }

    @Override
    public boolean commit(BusinessActionContext ctx) {
        String xid = ctx != null ? ctx.getXid() : "";
        String commodityCode = (String) ctx.getActionContext("commodityCode");
        String key = xid + ":sto:" + commodityCode;
        committed.put(key, true);
        return true;
    }

    @Override
    public boolean cancel(BusinessActionContext ctx) {
        String xid = ctx != null ? ctx.getXid() : "";
        String commodityCode = (String) ctx.getActionContext("commodityCode");
        Integer count = (Integer) ctx.getActionContext("count");
        String key = xid + ":sto:" + commodityCode;
        if (committed.containsKey(key)) {
            return true;
        }
        if (count != null) {
            storageTblMapper.addBack(commodityCode, count);
        }
        return true;
    }
}