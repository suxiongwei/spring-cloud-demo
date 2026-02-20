package indi.mofan.account.service;

import indi.mofan.account.bean.AccountTbl;

public interface AccountService {

    /**
     * 从用户账户中扣减
     *
     * @param userId 用户id
     * @param money  扣减金额
     */
    void debit(String userId, int money);
    void addBack(String userId, int money);
    AccountTbl snapshot(String userId);
}
