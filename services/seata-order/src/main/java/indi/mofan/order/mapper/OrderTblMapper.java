package indi.mofan.order.mapper;

import indi.mofan.order.bean.OrderTbl;
import org.apache.ibatis.annotations.Param;

public interface OrderTblMapper {

    int deleteByPrimaryKey(Long id);

    int insert(OrderTbl record);

    int insertSelective(OrderTbl record);

    OrderTbl selectByPrimaryKey(Long id);
    Integer countByUserAndCommodity(@Param("userId") String userId,
                                    @Param("commodityCode") String commodityCode);

    int updateByPrimaryKeySelective(OrderTbl record);

    int updateByPrimaryKey(OrderTbl record);

}
