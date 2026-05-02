package top.daoha.domain.trade.service.refund.business.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.daoha.domain.trade.adapter.repository.ITradeRepository;
import top.daoha.domain.trade.model.entity.TradeRefundOrderEntity;
import top.daoha.domain.trade.service.refund.business.IRefundOrderStrategy;

import javax.annotation.Resource;

@Slf4j
@Service("paid2RefundStrategy")
public class Paid2RefundStrategy implements IRefundOrderStrategy {

    @Resource
    private ITradeRepository tradeRepository;

    @Override
    public void refundOrder(TradeRefundOrderEntity tradeRefundOrderEntity) {
        log.info("退单，已支付，未成团。订单信息:{}",tradeRefundOrderEntity);
        tradeRepository.paid2RefundStrategy(tradeRefundOrderEntity);
    }
}
