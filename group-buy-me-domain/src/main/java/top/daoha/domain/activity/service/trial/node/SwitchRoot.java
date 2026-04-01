package top.daoha.domain.activity.service.trial.node;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.daoha.domain.activity.model.entity.MarketProductEntity;
import top.daoha.domain.activity.model.entity.TrialBalanceEntity;
import top.daoha.domain.activity.service.trial.AbstractGroupBuyMarketSupport;
import top.daoha.domain.activity.service.trial.factory.DefaultActivityStrategyFactoy;
import top.daoha.types.desgin.framework.tree.StrategyHandler;
import top.daoha.types.enums.ResponseCode;
import top.daoha.types.exception.AppException;

import javax.annotation.Resource;

/**
 * @ClassName : SwitchRoot
 * @Description : 选择节点主要就是用于根据当前情况选择分支
 * @github:
 * @Author : 24209
 * @Date: 2026/3/22  16:08
 */
@Slf4j
@Service
public class SwitchRoot extends AbstractGroupBuyMarketSupport<MarketProductEntity, DefaultActivityStrategyFactoy.DynamicContext, TrialBalanceEntity> {

    @Resource
    private MarketNode marketNode;

    @Override
    public TrialBalanceEntity doApply(MarketProductEntity requestParameter, DefaultActivityStrategyFactoy.DynamicContext dynamicContext) throws Exception {
        String userId = requestParameter.getUserId();

        if(iActivityRepository.downgradeSwitch()){
            throw new AppException(ResponseCode.E0003.getCode(),ResponseCode.E0003.getInfo());
        }

        if(!iActivityRepository.cutRange(userId)){
            throw new AppException(ResponseCode.E0004.getCode(),ResponseCode.E0004.getInfo());
        }

        return router(requestParameter,dynamicContext);
    }

    @Override
    public StrategyHandler<MarketProductEntity, DefaultActivityStrategyFactoy.DynamicContext, TrialBalanceEntity> get(MarketProductEntity requestParameter, DefaultActivityStrategyFactoy.DynamicContext dynamicContext) {
        return marketNode;
    }
}
