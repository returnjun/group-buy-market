package top.daoha.infrastructure.dao;


import org.apache.ibatis.annotations.Mapper;
import top.daoha.infrastructure.dao.po.GroupBuyOrder;

import java.util.List;
import java.util.Set;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 用户拼单
 * @create 2025-01-11 10:33
 */
@Mapper
public interface IGroupBuyOrderDao {

    void insert(GroupBuyOrder groupBuyOrder);

    int updateAddLockCount(String teamId);
    //update的返回值是说当前更新操作受影响的行数
    int updateSubtractionLockCount(String teamId);

    GroupBuyOrder queryGroupBuyProgress(String teamId);

    GroupBuyOrder queryGroupBuyTeamByTeamId(String teamId);

    int updateAddCompleteCount(String teamId);

    int updateOrderStatus2COMPLETE(String teamId);

    List<GroupBuyOrder> queryGroupBuyProgressByTeamIds(Set<String> teamIds);

    Integer queryALLTeamCount(Set<String> teamIds);

    Integer queryALLTeamCompleteCount(Set<String> teamIds);

    Integer queryALLUserCount(Set<String> teamIds);
}
