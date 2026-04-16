package top.daoha.domain.activity.service;

import org.springframework.stereotype.Service;
import top.daoha.domain.activity.adapter.repository.IActivityRepository;
import top.daoha.domain.activity.model.entity.MarketProductEntity;
import top.daoha.domain.activity.model.entity.TrialBalanceEntity;
import top.daoha.domain.activity.model.entity.UserGroupBuyOrderDetailEntity;
import top.daoha.domain.activity.model.valobj.TeamStatisticVO;
import top.daoha.domain.activity.service.trial.factory.DefaultActivityStrategyFactoy;
import top.daoha.types.desgin.framework.tree.StrategyHandler;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName : IIndexGroupBuyMarketServiceImpl
 * @Description :
 * @github:
 * @Author : 24209
 * @Date: 2026/3/22  16:21
 */
@Service
public class IIndexGroupBuyMarketServiceImpl implements IIndexGroupBuyMarketService{

    @Resource
    public DefaultActivityStrategyFactoy defaultActivityStrategyFactoy;

    @Resource
    public IActivityRepository iActivityRepository;

    @Override
    public TrialBalanceEntity indexMarketTrial(MarketProductEntity marketProductEntity) throws Exception {

        //这块本质上返回的是rootnode
        StrategyHandler<MarketProductEntity, DefaultActivityStrategyFactoy.DynamicContext, TrialBalanceEntity> strategyHandler = defaultActivityStrategyFactoy.strategyHandler();

        TrialBalanceEntity trialBalanceEntity = strategyHandler.apply(marketProductEntity, new DefaultActivityStrategyFactoy.DynamicContext());

        return trialBalanceEntity;
    }

    @Override
    public List<UserGroupBuyOrderDetailEntity> queryInProgressUserGroupBuyOrderDetailList(Long activityId, String userId, int ownerCount, int randomCount) {
        List<UserGroupBuyOrderDetailEntity> unionAllList = new ArrayList<>();

        if(0!=ownerCount){
            List<UserGroupBuyOrderDetailEntity> ownerList=iActivityRepository.queryInProgressUserGroupBuyOrderDetailListByOwner(activityId,userId,ownerCount);
            if(null != ownerList && ownerList.size()>0){
                unionAllList.addAll(ownerList);
            }
        }
        if(0!=randomCount){
            List<UserGroupBuyOrderDetailEntity> ownerList=iActivityRepository.queryInProgressUserGroupBuyOrderDetailListByRandom(activityId,userId,randomCount);
            if(null != ownerList && ownerList.size()>0){
                unionAllList.addAll(ownerList);
            }
        }

        return unionAllList;
    }

    @Override
    public TeamStatisticVO queryTeamStatisticByActivity(Long activityId) {

        return iActivityRepository.queryTeamStatisticByActivity(activityId);
    }


}
