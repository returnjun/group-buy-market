package top.daoha.domain.trade.service.refund.filter;

import cn.bugstack.wrench.design.framework.link.model2.handler.ILogicHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.daoha.domain.trade.adapter.repository.ITradeRepository;
import top.daoha.domain.trade.model.entity.*;
import top.daoha.domain.trade.model.valobj.RefundTypeEnumVO;
import top.daoha.domain.trade.model.valobj.TradeOrderStatusEnumVO;
import top.daoha.domain.trade.service.refund.business.IRefundOrderStrategy;
import top.daoha.domain.trade.service.refund.factory.TradeRefundFilterFactory;
import top.daoha.types.enums.GroupBuyOrderEnumVO;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@Service
public class RefundOrderNodeFilter implements ILogicHandler<TradeRefundCommandEntity, TradeRefundFilterFactory.DynamicContext,TradeRefundBehaviorEntity>{
    @Resource
    private ITradeRepository tradeRepository;

    @Resource
    private Map<String, IRefundOrderStrategy> refundOrderStrategyMap;

    @Override
    public  TradeRefundBehaviorEntity apply(TradeRefundCommandEntity requestParameter, TradeRefundFilterFactory.DynamicContext dynamicContext) throws Exception {
        //这个节点返回最终的数据

        MarketPayOrderEntity marketPayOrderEntity = dynamicContext.getMarketPayOrderEntity();
        TradeOrderStatusEnumVO tradeOrderStatusEnumVO = marketPayOrderEntity.getStatus();

        GroupBuyTeamEntity groupBuyTeamEntity = dynamicContext.getGroupBuyTeamEntity();
        GroupBuyOrderEnumVO groupBuyOrderEnumVO = groupBuyTeamEntity.getStatus();

        RefundTypeEnumVO refundTypeEnumVO = RefundTypeEnumVO.getRefundStrategy(groupBuyOrderEnumVO, tradeOrderStatusEnumVO);
        IRefundOrderStrategy refundOrderStrategy = refundOrderStrategyMap.get(refundTypeEnumVO.getStrategy());

        TradeRefundOrderEntity tradeRefundOrderEntity = TradeRefundOrderEntity.builder()
                .userId(requestParameter.getUserId())
                .orderId(marketPayOrderEntity.getOrderId())
                .teamId(marketPayOrderEntity.getTeamId())
                .activityId(groupBuyTeamEntity.getActivityId())
                .build();
        refundOrderStrategy.refundOrder(tradeRefundOrderEntity);

        return TradeRefundBehaviorEntity.builder()
                .userId(requestParameter.getUserId())
                .orderId(marketPayOrderEntity.getOrderId())
                .teamId(marketPayOrderEntity.getTeamId())
                .tradeRefundBehaviorEnum(TradeRefundBehaviorEntity.TradeRefundBehaviorEnum.SUCCESS)
                .build();
    }
}
