package indi.mofan.account.mapper;

import indi.mofan.account.bean.AccountTbl;

public interface AccountTblMapper {

    int deleteByPrimaryKey(Long id);

    int insert(AccountTbl record);

    int insertSelective(AccountTbl record);

    AccountTbl selectByPrimaryKey(Long id);
    AccountTbl selectByUserId(String userId);

    int updateByPrimaryKeySelective(AccountTbl record);

    int updateByPrimaryKey(AccountTbl record);

    void debit(String userId, int money);
    
    void addBack(String userId, int money);
}
