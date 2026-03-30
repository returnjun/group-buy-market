package top.daoha.domain.activity.service.trial.node;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.daoha.domain.activity.model.entity.MarketProductEntity;
import top.daoha.domain.activity.model.entity.TrialBalanceEntity;
import top.daoha.domain.activity.service.trial.AbstractGroupBuyMarketSupport;
import top.daoha.domain.activity.service.trial.factory.DefaultActivityStrategyFactoy;
import top.daoha.types.desgin.framework.tree.StrategyHandler;
import top.daoha.types.enums.ResponseCode;
import top.daoha.types.exception.AppException;


@Slf4j
@Service
public class ErrorNode extends AbstractGroupBuyMarketSupport<MarketProductEntity, DefaultActivityStrategyFactoy.DynamicContext, TrialBalanceEntity> {


    @Override
    protected TrialBalanceEntity doApply(MarketProductEntity requestParameter, DefaultActivityStrategyFactoy.DynamicContext dynamicContext) throws Exception {
        log.info("拼团商品查询试算服务-ErrorNode:{},requestParameter:{},dynamicContext:{}", requestParameter.getUserId(), JSON.toJSONString(requestParameter));
        if (null == dynamicContext.getGroupBuyActivityDiscountVO() || null == dynamicContext.getSkuVO()) {
            throw new AppException(ResponseCode.E0002.getInfo(), ResponseCode.E0002.getCode());
        }
        return TrialBalanceEntity.builder().build();
    }
    @Override
    public StrategyHandler<MarketProductEntity, DefaultActivityStrategyFactoy.DynamicContext, TrialBalanceEntity> get(MarketProductEntity requestParameter, DefaultActivityStrategyFactoy.DynamicContext dynamicContext) {
        return defaultStrategyHandler;
    }
}
