package top.daoha.domain.activity.service;

import org.springframework.stereotype.Service;
import top.daoha.domain.activity.model.entity.MarketProductEntity;
import top.daoha.domain.activity.model.entity.TrialBalanceEntity;
import top.daoha.domain.activity.service.trial.factory.DefaultActivityStrategyFactoy;
import top.daoha.types.desgin.framework.tree.StrategyHandler;

import javax.annotation.Resource;

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

    @Override
    public TrialBalanceEntity indexMarketTrial(MarketProductEntity marketProductEntity) throws Exception {

        //这块本质上返回的是rootnode
        StrategyHandler<MarketProductEntity, DefaultActivityStrategyFactoy.DynamicContext, TrialBalanceEntity> strategyHandler = defaultActivityStrategyFactoy.strategyHandler();

        TrialBalanceEntity trialBalanceEntity = strategyHandler.apply(marketProductEntity, new DefaultActivityStrategyFactoy.DynamicContext());

        return trialBalanceEntity;
    }
}
