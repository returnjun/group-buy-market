package top.daoha.domain.trade.service.lock.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
public class TeamStockOccupyRuleFilter implements ILogicHandler<TradeLockRuleCommandEntity, TradeLockRuleFilterFactory.DynamicContext, TradeLockRuleFilterBackEntity> {

    @Resource
    private ITradeRepository tradeRepository;

    @Override
    public TradeLockRuleFilterBackEntity apply(TradeLockRuleCommandEntity requestParameter, TradeLockRuleFilterFactory.DynamicContext dynamicContext) throws Exception {
        log.info("交易规则过滤-redis锁单处理:{} activityId: {} teamId: {}", requestParameter.getUserId(), requestParameter.getActivityId(), requestParameter.getTeamId());

        String teamId = requestParameter.getTeamId();
        if(StringUtils.isBlank(teamId)){

            return TradeLockRuleFilterBackEntity.builder()
                    .userTakeOrderCount(dynamicContext.getUserTakeOrderCount())
                    .build();
        }

        GroupBuyActivityEntity groupBuyActivityEntity = dynamicContext.getGroupBuyActivityEntity();

        Integer target = groupBuyActivityEntity.getTarget();
        Integer validTime = groupBuyActivityEntity.getValidTime();
        String teamStockKey = dynamicContext.generateTeamStockKey(teamId);
        String recoveryTeamStockKey = dynamicContext.generateRecoveryTeamStockKey(teamId);

        boolean status = tradeRepository.occupyTeamStock(teamStockKey,recoveryTeamStockKey,target,validTime);

        if(!status){
            log.warn("交易锁单失败");
            throw new AppException(ResponseCode.E0008);
        }

        return TradeLockRuleFilterBackEntity.builder()
                .userTakeOrderCount(dynamicContext.getUserTakeOrderCount())
                .recoveryTeamStockKey(recoveryTeamStockKey)
                .build();
    }
}
