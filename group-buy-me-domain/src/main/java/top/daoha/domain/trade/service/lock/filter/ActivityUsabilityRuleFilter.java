package top.daoha.domain.trade.service.lock.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.daoha.domain.trade.adapter.repository.ITradeRepository;
import top.daoha.domain.trade.model.entity.GroupBuyActivityEntity;
import top.daoha.domain.trade.model.entity.TradeLockRuleCommandEntity;
import top.daoha.domain.trade.model.entity.TradeLockRuleFilterBackEntity;
import top.daoha.domain.trade.service.lock.factory.TradeRuleFilterFactory;
import top.daoha.types.desgin.framework.link.model2.handler.ILogicHandler;
import top.daoha.types.enums.ActivityStatusEnumVO;
import top.daoha.types.enums.ResponseCode;
import top.daoha.types.exception.AppException;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Service
public class ActivityUsabilityRuleFilter implements ILogicHandler<TradeLockRuleCommandEntity, TradeRuleFilterFactory.DynamicContext, TradeLockRuleFilterBackEntity> {

    @Resource
    private ITradeRepository tradeRepository;

    @Override
    public TradeLockRuleFilterBackEntity apply(TradeLockRuleCommandEntity requestParameter, TradeRuleFilterFactory.DynamicContext dynamicContext) throws Exception {
        log.info("交易规则过滤-活动的可用性校验:{} activityId: {}", requestParameter.getUserId(), requestParameter.getActivityId());

        //查找
        GroupBuyActivityEntity groupBuyActivityEntity = tradeRepository.queryGroupBuyActivityByActivityId(requestParameter.getActivityId());
        //验证活动是否有效
        if(!ActivityStatusEnumVO.EFFECTIVE.equals(groupBuyActivityEntity.getStatus())){
            log.info("活动的可用性校验，非生效状态 activityId:{}", requestParameter.getActivityId());
            throw new AppException(ResponseCode.E0101);
            // 拼团活动未生效
        }
        //验证时间是否在活动时间范围内
        Date currentTime=new Date();
        if(currentTime.before(groupBuyActivityEntity.getStartTime())||currentTime.after(groupBuyActivityEntity.getEndTime())){
            log.info("活动的可用性校验，非可参与时间范围 activityId:{}", requestParameter.getActivityId());
            throw new AppException(ResponseCode.E0102);
        }
        dynamicContext.setGroupBuyActivityEntity(groupBuyActivityEntity);

        return next(requestParameter,dynamicContext);
    }
}
