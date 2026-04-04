package top.daoha.domain.trade.service.filter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.daoha.domain.trade.adapter.repository.ITradeRepository;
import top.daoha.domain.trade.model.entity.GroupBuyActivityEntity;
import top.daoha.domain.trade.model.entity.TradeRuleCommandEntity;
import top.daoha.domain.trade.model.entity.TradeRuleFilterBackEntity;
import top.daoha.domain.trade.service.factory.TradeRuleFilterFactory;
import top.daoha.types.desgin.framework.link.model2.handler.ILogicHandler;
import top.daoha.types.enums.ResponseCode;
import top.daoha.types.exception.AppException;

import javax.annotation.Resource;

@Slf4j
@Service
public class UserTakeLimitRuleFilter implements ILogicHandler<TradeRuleCommandEntity, TradeRuleFilterFactory.DynamicContext, TradeRuleFilterBackEntity> {

    @Resource
    private ITradeRepository tradeRepository;

    @Override
    public TradeRuleFilterBackEntity apply(TradeRuleCommandEntity requestParameter, TradeRuleFilterFactory.DynamicContext dynamicContext) throws Exception {
        GroupBuyActivityEntity groupBuyActivityEntity = dynamicContext.getGroupBuyActivityEntity();
        log.info("交易规则过滤-用户参与次数过滤:{} activityId: {}", requestParameter.getUserId(), requestParameter.getActivityId());
        Integer orderCount = tradeRepository.queryOrderCountByActivityId(requestParameter.getActivityId(),requestParameter.getUserId());
        //验证用户参与次数是否超过限制
        if(null!=groupBuyActivityEntity.getTakeLimitCount()&&orderCount>=groupBuyActivityEntity.getTakeLimitCount()){
            throw new AppException(ResponseCode.E0103);
        }
        return TradeRuleFilterBackEntity.builder()
                .userTakeOrderCount(orderCount)
                .build();
    }
}
