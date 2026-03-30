package top.daoha.domain.activity.service.trial.node;

import org.apache.commons.lang3.StringUtils;
import top.daoha.domain.activity.model.entity.MarketProductEntity;
import top.daoha.domain.activity.model.entity.TrialBalanceEntity;
import top.daoha.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import top.daoha.domain.activity.service.trial.AbstractGroupBuyMarketSupport;
import top.daoha.domain.activity.service.trial.factory.DefaultActivityStrategyFactoy;
import top.daoha.types.desgin.framework.tree.StrategyHandler;

import javax.annotation.Resource;

public class TagNode extends AbstractGroupBuyMarketSupport<MarketProductEntity, DefaultActivityStrategyFactoy.DynamicContext, TrialBalanceEntity> {
    @Resource
    private EndNode endNode;

    @Override
    protected TrialBalanceEntity doApply(MarketProductEntity requestParameter, DefaultActivityStrategyFactoy.DynamicContext dynamicContext) throws Exception {
        GroupBuyActivityDiscountVO groupBuyActivityDiscountVO = dynamicContext.getGroupBuyActivityDiscountVO();
        String tagId = groupBuyActivityDiscountVO.getTagId();
        boolean enable = groupBuyActivityDiscountVO.isEnable();
        boolean visible = groupBuyActivityDiscountVO.isVisible();

        //这里，如果当前这个人群标签都没有，直接默认没有限制
        if(StringUtils.isBlank(tagId)){
            dynamicContext.setEnabled(true);
            dynamicContext.setVisible(true);
            router(requestParameter, dynamicContext);
        }

        boolean isWithin =


        return router(requestParameter, dynamicContext);
    }

    @Override
    public StrategyHandler<MarketProductEntity, DefaultActivityStrategyFactoy.DynamicContext, TrialBalanceEntity> get(MarketProductEntity requestParameter, DefaultActivityStrategyFactoy.DynamicContext dynamicContext) {
        return endNode;
    }
}
