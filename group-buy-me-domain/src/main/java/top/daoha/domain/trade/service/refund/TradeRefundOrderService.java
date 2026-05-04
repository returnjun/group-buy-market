package top.daoha.domain.trade.service.refund;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.daoha.domain.trade.adapter.repository.ITradeRepository;
import top.daoha.domain.trade.model.entity.*;
import top.daoha.domain.trade.model.valobj.RefundTypeEnumVO;
import top.daoha.domain.trade.model.valobj.TradeOrderStatusEnumVO;
import top.daoha.domain.trade.service.ITradeRefundOrderService;
import top.daoha.domain.trade.service.refund.business.IRefundOrderStrategy;
import top.daoha.types.enums.GroupBuyOrderEnumVO;

import javax.annotation.Resource;
import java.util.Map;

@Service
@Slf4j
public class TradeRefundOrderService implements ITradeRefundOrderService {

    @Resource
    private ITradeRepository tradeRepository;

    private Map<String, IRefundOrderStrategy> refundOrderStrategyMap;

    public TradeRefundOrderService(ITradeRepository tradeRepository, Map<String, IRefundOrderStrategy> refundOrderStrategyMap) {
        this.tradeRepository = tradeRepository;
        this.refundOrderStrategyMap = refundOrderStrategyMap;
    }

    @Override
    public TradeRefundBehaviorEntity refundOrder(TradeRefundCommandEntity tradeRefundCommandEntity) {
        log.info("逆向流程，退单操作 userId:{} outTrade:{}",tradeRefundCommandEntity.getUserId(),tradeRefundCommandEntity.getOutTradeNo());
        MarketPayOrderEntity marketPayOrderEntity = tradeRepository.queryNoPayMarketPayOrderByOutTradeNo(tradeRefundCommandEntity.getUserId(),tradeRefundCommandEntity.getOutTradeNo());
        TradeOrderStatusEnumVO tradeOrderStatusEnumVO = marketPayOrderEntity.getStatus();
        String teamId = marketPayOrderEntity.getTeamId();
        String orderId = marketPayOrderEntity.getOrderId();
        // 1. 如果已经退单过了
        if(TradeOrderStatusEnumVO.CLOSE== tradeOrderStatusEnumVO){
            log.info("订单{}已退单，无需重复操作",orderId);
            return TradeRefundBehaviorEntity.builder()
                    .userId(tradeRefundCommandEntity.getUserId())
                    .orderId(orderId)
                    .teamId(teamId)
                    .tradeRefundBehaviorEnum(TradeRefundBehaviorEntity.TradeRefundBehaviorEnum.REPEAT)
                    .build();
        }

        //2.查询拼团的状态
        GroupBuyTeamEntity groupBuyTeamEntity = tradeRepository.queryGroupBuyTeamByTeamId(teamId);
        GroupBuyOrderEnumVO groupBuyOrderEnumVO = groupBuyTeamEntity.getStatus();

        //3.根据拼团的状态来进行不同的策略
        RefundTypeEnumVO refundTypeEnumVO = RefundTypeEnumVO.getRefundStrategy(groupBuyOrderEnumVO, tradeOrderStatusEnumVO);
        IRefundOrderStrategy refundOrderStrategy = refundOrderStrategyMap.get(refundTypeEnumVO.getStrategy());

        TradeRefundOrderEntity tradeRefundOrderEntity = TradeRefundOrderEntity.builder()
                .userId(tradeRefundCommandEntity.getUserId())
                .orderId(orderId)
                .teamId(teamId)
                .activityId(groupBuyTeamEntity.getActivityId())
                .build();
        refundOrderStrategy.refundOrder(tradeRefundOrderEntity);

        return TradeRefundBehaviorEntity.builder()
                .userId(tradeRefundCommandEntity.getUserId())
                .orderId(orderId)
                .teamId(teamId)
                .tradeRefundBehaviorEnum(TradeRefundBehaviorEntity.TradeRefundBehaviorEnum.SUCCESS)
                .build();
    }
}
