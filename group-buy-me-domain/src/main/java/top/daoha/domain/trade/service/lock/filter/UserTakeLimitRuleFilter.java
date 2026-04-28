package top.daoha.domain.trade.service.lock.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.daoha.domain.trade.adapter.repository.ITradeRepository;
import top.daoha.domain.trade.model.entity.GroupBuyActivityEntity;
import top.daoha.domain.trade.model.entity.TradeLockRuleCommandEntity;
import top.daoha.domain.trade.model.entity.TradeLockRuleFilterBackEntity;
import top.daoha.domain.trade.service.lock.factory.TradeLockRuleFilterFactory;
import top.daoha.types.desgin.framework.link.model2.handler.ILogicHandler;
import top.daoha.types.enums.ResponseCode;
import top.daoha.types.exception.AppException;

import javax.annotation.Resource;

@Slf4j
@Service
public class UserTakeLimitRuleFilter implements ILogicHandler<TradeLockRuleCommandEntity, TradeLockRuleFilterFactory.DynamicContext, TradeLockRuleFilterBackEntity> {

    @Resource
    private ITradeRepository tradeRepository;

    @Override
    public TradeLockRuleFilterBackEntity apply(TradeLockRuleCommandEntity requestParameter, TradeLockRuleFilterFactory.DynamicContext dynamicContext) throws Exception {
        GroupBuyActivityEntity groupBuyActivityEntity = dynamicContext.getGroupBuyActivityEntity();
        log.info("交易规则过滤-用户参与次数过滤:{} activityId: {}", requestParameter.getUserId(), requestParameter.getActivityId());
        Integer orderCount = tradeRepository.queryOrderCountByActivityId(requestParameter.getActivityId(),requestParameter.getUserId());
        dynamicContext.setUserTakeOrderCount(orderCount);

        //验证用户参与次数是否超过限制
        if(null!=groupBuyActivityEntity.getTakeLimitCount()&&orderCount>=groupBuyActivityEntity.getTakeLimitCount()){
            throw new AppException(ResponseCode.E0103);
        }
        return next(requestParameter, dynamicContext);
    }
}
