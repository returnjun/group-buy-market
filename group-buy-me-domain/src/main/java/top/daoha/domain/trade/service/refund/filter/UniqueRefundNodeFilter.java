package top.daoha.domain.trade.service.refund.filter;

import cn.bugstack.wrench.design.framework.link.model2.handler.ILogicHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.daoha.domain.trade.adapter.repository.ITradeRepository;
import top.daoha.domain.trade.model.entity.GroupBuyTeamEntity;
import top.daoha.domain.trade.model.entity.MarketPayOrderEntity;
import top.daoha.domain.trade.model.entity.TradeRefundBehaviorEntity;
import top.daoha.domain.trade.model.entity.TradeRefundCommandEntity;
import top.daoha.domain.trade.model.valobj.TradeOrderStatusEnumVO;
import top.daoha.domain.trade.service.refund.factory.TradeRefundFilterFactory;
import top.daoha.types.enums.GroupBuyOrderEnumVO;

import javax.annotation.Resource;

@Slf4j
@Service
public class UniqueRefundNodeFilter implements ILogicHandler<TradeRefundCommandEntity, TradeRefundFilterFactory.DynamicContext,TradeRefundBehaviorEntity>{
    @Resource
    private ITradeRepository tradeRepository;

    @Override
    public  TradeRefundBehaviorEntity apply(TradeRefundCommandEntity requestParameter, TradeRefundFilterFactory.DynamicContext dynamicContext) throws Exception {
        //这个节点用来检查退单的情况
        MarketPayOrderEntity marketPayOrderEntity = dynamicContext.getMarketPayOrderEntity();
        TradeOrderStatusEnumVO tradeOrderStatusEnumVO = marketPayOrderEntity.getStatus();
        // 1. 如果已经退单过了
        if(TradeOrderStatusEnumVO.CLOSE== tradeOrderStatusEnumVO){
            log.info("订单{}已退单，无需重复操作",marketPayOrderEntity.getOrderId());
            return TradeRefundBehaviorEntity.builder()
                    .userId(requestParameter.getUserId())
                    .orderId(marketPayOrderEntity.getOrderId())
                    .teamId(marketPayOrderEntity.getTeamId())
                    .tradeRefundBehaviorEnum(TradeRefundBehaviorEntity.TradeRefundBehaviorEnum.REPEAT)
                    .build();
        }

        return next(requestParameter,dynamicContext);
    }
}
