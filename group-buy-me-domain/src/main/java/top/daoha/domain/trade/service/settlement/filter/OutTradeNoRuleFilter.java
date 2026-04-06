package top.daoha.domain.trade.service.settlement.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.daoha.domain.trade.adapter.repository.ITradeRepository;
import top.daoha.domain.trade.model.entity.MarketPayOrderEntity;
import top.daoha.domain.trade.model.entity.TradeSettlementRuleCommandEntity;
import top.daoha.domain.trade.model.entity.TradeSettlementRuleFilterBackEntity;
import top.daoha.domain.trade.service.settlement.factory.TradeSettlementRuleFilterFactory;
import top.daoha.types.desgin.framework.link.model2.handler.ILogicHandler;
import top.daoha.types.enums.ResponseCode;
import top.daoha.types.exception.AppException;

import javax.annotation.Resource;

@Slf4j
@Service
public class OutTradeNoRuleFilter implements ILogicHandler<TradeSettlementRuleCommandEntity, TradeSettlementRuleFilterFactory.DynamicContext, TradeSettlementRuleFilterBackEntity> {
    @Resource
    private ITradeRepository tradeRepository;

    @Override
    public TradeSettlementRuleFilterBackEntity apply(TradeSettlementRuleCommandEntity requestParameter, TradeSettlementRuleFilterFactory.DynamicContext dynamicContext) throws Exception {

        // 1 查询拼团信息
        MarketPayOrderEntity marketPayOrderEntity = tradeRepository.queryNoPayMarketPayOrderByOutTradeNo(requestParameter.getUserId(), requestParameter.getOutTradeNo());
        if(null==marketPayOrderEntity){
            log.error("预购订单不存在或者用户已经退单，不需要做支付订单结算:{} outTradeNo",requestParameter.getOutTradeNo(),requestParameter.getUserId());
            throw new AppException(ResponseCode.E0104);
        }

        dynamicContext.setMarketPayOrderEntity(marketPayOrderEntity);

        return next(requestParameter,dynamicContext);
    }
}
