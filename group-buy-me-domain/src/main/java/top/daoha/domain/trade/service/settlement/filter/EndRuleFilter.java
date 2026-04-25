package top.daoha.domain.trade.service.settlement.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.daoha.domain.trade.adapter.repository.ITradeRepository;
import top.daoha.domain.trade.model.entity.GroupBuyTeamEntity;
import top.daoha.domain.trade.model.entity.TradeSettlementRuleCommandEntity;
import top.daoha.domain.trade.model.entity.TradeSettlementRuleFilterBackEntity;
import top.daoha.domain.trade.service.settlement.factory.TradeSettlementRuleFilterFactory;
import top.daoha.types.desgin.framework.link.model2.handler.ILogicHandler;

import javax.annotation.Resource;

@Slf4j
@Service
public class EndRuleFilter implements ILogicHandler<TradeSettlementRuleCommandEntity, TradeSettlementRuleFilterFactory.DynamicContext, TradeSettlementRuleFilterBackEntity> {
    @Resource
    private ITradeRepository tradeRepository;

    @Override
    public TradeSettlementRuleFilterBackEntity apply(TradeSettlementRuleCommandEntity requestParameter, TradeSettlementRuleFilterFactory.DynamicContext dynamicContext) throws Exception {
        GroupBuyTeamEntity groupBuyTeamEntity = dynamicContext.getGroupBuyTeamEntity();

        return TradeSettlementRuleFilterBackEntity.builder()
                .activityId(groupBuyTeamEntity.getActivityId())
                .completeCount(groupBuyTeamEntity.getCompleteCount())
                .lockCount(groupBuyTeamEntity.getLockCount())
                .status(groupBuyTeamEntity.getStatus())
                .targetCount(groupBuyTeamEntity.getTargetCount())
                .teamId(groupBuyTeamEntity.getTeamId())
                .validEndTime(groupBuyTeamEntity.getValidEndTime())
                .validStartTime(groupBuyTeamEntity.getValidStartTime())
                .notifyConfigVO(groupBuyTeamEntity.getNotifyConfigVO())
                .build();
    }
}
