package top.daoha.domain.trade.service.settlement.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.daoha.domain.trade.adapter.repository.ITradeRepository;
import top.daoha.domain.trade.model.entity.TradeSettlementRuleCommandEntity;
import top.daoha.domain.trade.model.entity.TradeSettlementRuleFilterBackEntity;
import top.daoha.domain.trade.service.settlement.factory.TradeSettlementRuleFilterFactory;
import top.daoha.types.desgin.framework.link.model2.handler.ILogicHandler;
import top.daoha.types.enums.ResponseCode;
import top.daoha.types.exception.AppException;

import javax.annotation.Resource;

@Slf4j
@Service
public class SCRuleFilter implements ILogicHandler<TradeSettlementRuleCommandEntity, TradeSettlementRuleFilterFactory.DynamicContext, TradeSettlementRuleFilterBackEntity> {
    @Resource
    private ITradeRepository tradeRepository;

    @Override
    public TradeSettlementRuleFilterBackEntity apply(TradeSettlementRuleCommandEntity requestParameter, TradeSettlementRuleFilterFactory.DynamicContext dynamicContext) throws Exception {
        log.info("结算规则过滤-渠道黑名单校验{} outTradeNo:{}",requestParameter.getUserId(),requestParameter.getOutTradeNo());

        //检查是管控被拦截了
        boolean isIntercept =tradeRepository.isSCBlackIntercept(requestParameter.getSource(),requestParameter.getChannel());
        if(isIntercept){
            throw new AppException(ResponseCode.E0105);
        }
        return next(requestParameter,dynamicContext);
    }
}
