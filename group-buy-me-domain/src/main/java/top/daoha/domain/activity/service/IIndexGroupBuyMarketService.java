package top.daoha.domain.activity.service;

import top.daoha.domain.activity.model.entity.MarketProductEntity;
import top.daoha.domain.activity.model.entity.TrialBalanceEntity;
import top.daoha.domain.activity.model.entity.UserGroupBuyOrderDetailEntity;
import top.daoha.domain.activity.model.valobj.TeamStatisticVO;

import java.util.List;

/**
 * @ClassName : IIndexGroupBuyMarketService
 * @Description :
 * @github:
 * @Author : 24209
 * @Date: 2026/3/22  16:19
 */

public interface IIndexGroupBuyMarketService {
    //输入商品信息然后返回商品折扣后的结果
    TrialBalanceEntity indexMarketTrial(MarketProductEntity marketProductEntity)throws Exception;

    List<UserGroupBuyOrderDetailEntity> queryInProgressUserGroupBuyOrderDetailList(Long activityId, String userId, int owner, int random);

    TeamStatisticVO queryTeamStatisticByActivity(Long activityId);
}
