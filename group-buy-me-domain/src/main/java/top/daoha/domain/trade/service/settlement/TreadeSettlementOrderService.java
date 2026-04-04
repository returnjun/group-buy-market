package top.daoha.domain.trade.service.settlement;


import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.daoha.domain.trade.adapter.repository.ITradeRepository;
import top.daoha.domain.trade.model.aggregate.GroupBuyTeamSettlementAggregate;
import top.daoha.domain.trade.model.entity.*;
import top.daoha.domain.trade.service.ITradeSettlementOrderService;

import javax.annotation.Resource;

@Service
@Slf4j
public class TreadeSettlementOrderService implements ITradeSettlementOrderService {

    @Resource
    private ITradeRepository tradeRepository;

    @Override
    public TradePaySettlementEntity settlementOrder(TradePaySuccessEntity tradePaySuccessEntity) {

        // 1 查询拼团信息
        MarketPayOrderEntity marketPayOrderEntity = tradeRepository.queryNoPayMarketPayOrderByOutTradeNo(tradePaySuccessEntity.getUserId(), tradePaySuccessEntity.getOutTradeNo());
        if(null==marketPayOrderEntity){
            log.error("预购订单不存在");
            return null;
        }
        log.info("查看根据userid和tradeNO查到的订单信息:{}",JSON.toJSONString(marketPayOrderEntity));
        // 2 查询拼团信息
        GroupBuyTeamEntity groupBuyTeamEntity = tradeRepository.queryGroupBuyTeamByTeamId(marketPayOrderEntity.getTeamId());

        log.info("查看根据teamId查到的订单信息:{}",JSON.toJSONString(groupBuyTeamEntity));
        GroupBuyTeamSettlementAggregate build = GroupBuyTeamSettlementAggregate.builder()
                .userEntity(new UserEntity(tradePaySuccessEntity.getUserId()))
                .groupBuyTeamEntity(groupBuyTeamEntity)
                .tradePaySuccessEntity(tradePaySuccessEntity)
                .build();

        //结算处理
        tradeRepository.settlementMarketPayOrder(build);

        return TradePaySettlementEntity.builder()
                .source(tradePaySuccessEntity.getSource())
                .channel(tradePaySuccessEntity.getChannel())
                .userId(tradePaySuccessEntity.getUserId())
                .teamId(marketPayOrderEntity.getTeamId())
                .activityId(groupBuyTeamEntity.getActivityId())
                .outTradeNo(tradePaySuccessEntity.getOutTradeNo())
                .build();
    }
}
