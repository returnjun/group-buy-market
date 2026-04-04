package top.daoha.domain.trade.service.factory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import top.daoha.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import top.daoha.domain.activity.model.valobj.SkuVO;
import top.daoha.domain.trade.model.entity.GroupBuyActivityEntity;
import top.daoha.domain.trade.model.entity.TradeRuleCommandEntity;
import top.daoha.domain.trade.model.entity.TradeRuleFilterBackEntity;
import top.daoha.domain.trade.service.filter.ActivityUsabilityRuleFilter;
import top.daoha.domain.trade.service.filter.UserTakeLimitRuleFilter;
import top.daoha.types.desgin.framework.link.model2.LinkArmory;
import top.daoha.types.desgin.framework.link.model2.chain.BusinessLinkedList;

import java.math.BigDecimal;

@Slf4j
@Service
public class TradeRuleFilterFactory {

    private  DynamicContext dynamicContext;

    @Bean("tradeRuleFilter1")
    public BusinessLinkedList<TradeRuleCommandEntity,DynamicContext,TradeRuleFilterBackEntity> tradeRuleFilter(ActivityUsabilityRuleFilter activityUsabilityRuleFilter,UserTakeLimitRuleFilter userTakeLimitRuleFilter){
        LinkArmory<TradeRuleCommandEntity,DynamicContext,TradeRuleFilterBackEntity> linkArmory = new LinkArmory<>("交易过滤责任链",activityUsabilityRuleFilter,userTakeLimitRuleFilter);
        //返回链对象
        return linkArmory.getLogicLink();
    }




    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DynamicContext{
        private GroupBuyActivityEntity groupBuyActivityEntity;
    }

}
