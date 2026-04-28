package top.daoha.domain.trade.service.lock.factory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import top.daoha.domain.trade.model.entity.GroupBuyActivityEntity;
import top.daoha.domain.trade.model.entity.TradeLockRuleCommandEntity;
import top.daoha.domain.trade.model.entity.TradeLockRuleFilterBackEntity;
import top.daoha.domain.trade.service.lock.filter.ActivityUsabilityRuleFilter;
import top.daoha.domain.trade.service.lock.filter.TeamStockOccupyRuleFilter;
import top.daoha.domain.trade.service.lock.filter.UserTakeLimitRuleFilter;
import top.daoha.types.desgin.framework.link.model2.LinkArmory;
import top.daoha.types.desgin.framework.link.model2.chain.BusinessLinkedList;

@Slf4j
@Service
public class TradeLockRuleFilterFactory {

    private  DynamicContext dynamicContext;

    @Bean("tradeRuleFilter1")
    public BusinessLinkedList<TradeLockRuleCommandEntity,DynamicContext, TradeLockRuleFilterBackEntity> tradeRuleFilter(
            ActivityUsabilityRuleFilter activityUsabilityRuleFilter,
            UserTakeLimitRuleFilter userTakeLimitRuleFilter,
            TeamStockOccupyRuleFilter teamStockOccupyRuleFilter){
        LinkArmory<TradeLockRuleCommandEntity,DynamicContext, TradeLockRuleFilterBackEntity> linkArmory =
                new LinkArmory<>("交易过滤责任链",activityUsabilityRuleFilter,userTakeLimitRuleFilter,teamStockOccupyRuleFilter);
        //返回链对象
        return linkArmory.getLogicLink();
    }




    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DynamicContext{

        private String teamStockKey= "group_buy_market_team_stock_key_";

        private GroupBuyActivityEntity groupBuyActivityEntity;

        private Integer userTakeOrderCount;

        public String generateTeamStockKey(String teamId){
            return teamStockKey+groupBuyActivityEntity.getActivityId()+"_"+teamId;
        }

        public String generateRecoveryTeamStockKey(String teamId){
            return teamStockKey+groupBuyActivityEntity.getActivityId()+"_"+teamId+"_recovery";
        }

    }

}
