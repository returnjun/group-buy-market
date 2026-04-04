package top.daoha.infrastructure.dao;


import org.apache.ibatis.annotations.Mapper;
import top.daoha.infrastructure.dao.po.GroupBuyOrder;
import top.daoha.infrastructure.dao.po.GroupBuyOrderList;

import java.util.List;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 用户拼单明细
 * @create 2025-01-11 09:07
 */
@Mapper
public interface IGroupBuyOrderListDao {

    void insert(GroupBuyOrderList groupBuyOrderListReq);

    GroupBuyOrderList queryGroupBuyOrderRecordByOutTradeNo(GroupBuyOrderList groupBuyOrderListReq);

    Integer queryOrderCountByActivityId(GroupBuyOrderList groupBuyOrderList);


    int updateStatus2COMPLETE(GroupBuyOrderList groupBuyOrderListReq);

    List<String> queryGroupBuyCompleterOrderOutTradeNoListByTeamId(String teamId);
}
