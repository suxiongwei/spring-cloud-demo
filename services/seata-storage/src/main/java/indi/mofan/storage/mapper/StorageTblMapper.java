package indi.mofan.storage.mapper;

import indi.mofan.storage.bean.StorageTbl;

public interface StorageTblMapper {

    int deleteByPrimaryKey(Long id);

    int insert(StorageTbl record);

    int insertSelective(StorageTbl record);

    StorageTbl selectByPrimaryKey(Long id);
    StorageTbl selectByCommodityCode(String commodityCode);

    int updateByPrimaryKeySelective(StorageTbl record);

    int updateByPrimaryKey(StorageTbl record);

    void deduct(String commodityCode, int count);
    
    void addBack(String commodityCode, int count);
}
