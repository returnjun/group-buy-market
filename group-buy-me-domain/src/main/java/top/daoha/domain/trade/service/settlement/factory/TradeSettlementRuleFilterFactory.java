package top.daoha.domain.trade.service.settlement.factory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import top.daoha.domain.trade.model.entity.*;
import top.daoha.domain.trade.service.lock.factory.TradeLockRuleFilterFactory;
import top.daoha.domain.trade.service.lock.filter.ActivityUsabilityRuleFilter;
import top.daoha.domain.trade.service.lock.filter.UserTakeLimitRuleFilter;
import top.daoha.domain.trade.service.settlement.filter.EndRuleFilter;
import top.daoha.domain.trade.service.settlement.filter.OutTradeNoRuleFilter;
import top.daoha.domain.trade.service.settlement.filter.SCRuleFilter;
import top.daoha.domain.trade.service.settlement.filter.SettlableRuleFilter;
import top.daoha.types.desgin.framework.link.model2.LinkArmory;
import top.daoha.types.desgin.framework.link.model2.chain.BusinessLinkedList;

@Slf4j
@Service
public class TradeSettlementRuleFilterFactory {

    private  DynamicContext dynamicContext;


    @Bean("tradeSettlementRuleFilter1")
    public BusinessLinkedList<TradeSettlementRuleCommandEntity, TradeSettlementRuleFilterFactory.DynamicContext, TradeSettlementRuleFilterBackEntity>
    tradeSettlementRuleFilter(
            SCRuleFilter scRuleFilter,
            OutTradeNoRuleFilter outTradeNoRuleFilter,
            SettlableRuleFilter settlableRuleFilter,
            EndRuleFilter endRuleFilter
            ){
        LinkArmory<TradeSettlementRuleCommandEntity, TradeSettlementRuleFilterFactory.DynamicContext, TradeSettlementRuleFilterBackEntity> linkArmory =
                new LinkArmory<>("交易结算规则过滤链",scRuleFilter,outTradeNoRuleFilter,settlableRuleFilter,endRuleFilter);
        //返回链对象
        return linkArmory.getLogicLink();
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DynamicContext{

        private MarketPayOrderEntity marketPayOrderEntity;

        private GroupBuyTeamEntity groupBuyTeamEntity;
    }

}
