package top.daoha.domain.trade.service.settlement;


import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.daoha.domain.trade.adapter.repository.ITradeRepository;
import top.daoha.domain.trade.model.aggregate.GroupBuyTeamSettlementAggregate;
import top.daoha.domain.trade.model.entity.*;
import top.daoha.domain.trade.service.ITradeSettlementOrderService;
import top.daoha.domain.trade.service.settlement.factory.TradeSettlementRuleFilterFactory;
import top.daoha.types.desgin.framework.link.model2.chain.BusinessLinkedList;

import javax.annotation.Resource;

@Service
@Slf4j
public class TreadeSettlementOrderService implements ITradeSettlementOrderService {

    @Resource
    private ITradeRepository tradeRepository;

    @Resource(name = "tradeSettlementRuleFilter1")
    BusinessLinkedList<TradeSettlementRuleCommandEntity, TradeSettlementRuleFilterFactory.DynamicContext, TradeSettlementRuleFilterBackEntity>  tradeSettlementRuleFilterBackEntity;

    @Override
    public TradePaySettlementEntity settlementOrder(TradePaySuccessEntity tradePaySuccessEntity) throws Exception {

        // 1 查询拼团信息
        TradeSettlementRuleCommandEntity build1 = TradeSettlementRuleCommandEntity.builder()
                .source(tradePaySuccessEntity.getSource())
                .channel(tradePaySuccessEntity.getChannel())
                .outTradeTime(tradePaySuccessEntity.getOutTradeTime())
                .outTradeNo(tradePaySuccessEntity.getOutTradeNo())
                .userId(tradePaySuccessEntity.getUserId())
                .build();

        TradeSettlementRuleFilterBackEntity tradeEntity = tradeSettlementRuleFilterBackEntity.apply(build1,new TradeSettlementRuleFilterFactory.DynamicContext());

        GroupBuyTeamEntity groupBuyTeamEntity = GroupBuyTeamEntity.builder()
                .validEndTime(tradeEntity.getValidEndTime())
                .validStartTime(tradeEntity.getValidStartTime())
                .teamId(tradeEntity.getTeamId())
                .targetCount(tradeEntity.getTargetCount())
                .status(tradeEntity.getStatus())
                .lockCount(tradeEntity.getLockCount())
                .completeCount(tradeEntity.getCompleteCount())
                .activityId(tradeEntity.getActivityId())
                .build();

        log.info("查看根据teamId查到的订单信息:{}",JSON.toJSONString(groupBuyTeamEntity));
        GroupBuyTeamSettlementAggregate build = GroupBuyTeamSettlementAggregate.builder()
                .userEntity(new UserEntity(tradePaySuccessEntity.getUserId()))
                .groupBuyTeamEntity(groupBuyTeamEntity)
                .tradePaySuccessEntity(tradePaySuccessEntity)
                .build();

        //结算处理
        tradeRepository.settlementMarketPayOrder(build);

        return TradePaySettlementEntity.builder()
                .source(tradePaySuccessEntity.getSource())
                .channel(tradePaySuccessEntity.getChannel())
                .userId(tradePaySuccessEntity.getUserId())
                .teamId(tradeEntity.getTeamId())
                .activityId(groupBuyTeamEntity.getActivityId())
                .outTradeNo(tradePaySuccessEntity.getOutTradeNo())
                .build();
    }
}
