package indi.mofan.storage.tcc;

import org.apache.seata.rm.tcc.api.BusinessActionContext;
import org.apache.seata.rm.tcc.api.BusinessActionContextParameter;
import org.apache.seata.rm.tcc.api.TwoPhaseBusinessAction;

public interface StorageTccService {

    @TwoPhaseBusinessAction(name = "storageDeductTcc", commitMethod = "commit", rollbackMethod = "cancel")
    boolean prepare(BusinessActionContext ctx,
                    @BusinessActionContextParameter(paramName = "commodityCode") String commodityCode,
                    @BusinessActionContextParameter(paramName = "count") int count);

    boolean commit(BusinessActionContext ctx);

    boolean cancel(BusinessActionContext ctx);
}