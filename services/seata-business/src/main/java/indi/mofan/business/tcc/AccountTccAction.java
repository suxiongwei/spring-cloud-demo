package indi.mofan.business.tcc;

import org.apache.seata.rm.tcc.api.BusinessActionContext;
import org.apache.seata.rm.tcc.api.BusinessActionContextParameter;
import org.apache.seata.rm.tcc.api.TwoPhaseBusinessAction;

public interface AccountTccAction {

    @TwoPhaseBusinessAction(name = "bizAccountDebitTcc", commitMethod = "commit", rollbackMethod = "cancel")
    boolean prepare(BusinessActionContext ctx,
                    @BusinessActionContextParameter(paramName = "userId") String userId,
                    @BusinessActionContextParameter(paramName = "money") int money);

    boolean commit(BusinessActionContext ctx);

    boolean cancel(BusinessActionContext ctx);
}