package top.daoha.domain.trade.service.refund.factory;

import cn.bugstack.wrench.design.framework.link.model2.LinkArmory;
import cn.bugstack.wrench.design.framework.link.model2.chain.BusinessLinkedList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import top.daoha.domain.trade.model.entity.GroupBuyTeamEntity;
import top.daoha.domain.trade.model.entity.MarketPayOrderEntity;
import top.daoha.domain.trade.model.entity.TradeRefundBehaviorEntity;
import top.daoha.domain.trade.model.entity.TradeRefundCommandEntity;
import top.daoha.domain.trade.service.refund.filter.DataNodeFilter;
import top.daoha.domain.trade.service.refund.filter.RefundOrderNodeFilter;
import top.daoha.domain.trade.service.refund.filter.UniqueRefundNodeFilter;

@Slf4j
@Service
public class TradeRefundFilterFactory {

    private  DynamicContext dynamicContext;


    @Bean("tradeRefundRuleFilter1")
    public BusinessLinkedList<TradeRefundCommandEntity, DynamicContext,TradeRefundBehaviorEntity>
    tradeSettlementRuleFilter(
                DataNodeFilter dataNodeFilter,
                UniqueRefundNodeFilter uniqueRefundNodeFilter,
                RefundOrderNodeFilter refundOrderNodeFilter
            ){
        LinkArmory<TradeRefundCommandEntity, DynamicContext,TradeRefundBehaviorEntity> linkArmory =
                new LinkArmory<>("退单结算规则过滤链",dataNodeFilter,uniqueRefundNodeFilter,refundOrderNodeFilter);
        //返回链对象
        return linkArmory.getLogicLink();
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DynamicContext {

        private MarketPayOrderEntity marketPayOrderEntity;

        private GroupBuyTeamEntity groupBuyTeamEntity;

    }

}
