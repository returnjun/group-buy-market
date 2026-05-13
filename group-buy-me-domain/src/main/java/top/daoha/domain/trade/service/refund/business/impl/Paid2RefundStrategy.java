package top.daoha.domain.trade.service.refund.business.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.daoha.domain.trade.adapter.repository.ITradeRepository;
import top.daoha.domain.trade.model.aggregate.GroupBuyRefundAggregate;
import top.daoha.domain.trade.model.entity.NotifyTaskEntity;
import top.daoha.domain.trade.model.entity.TradeRefundOrderEntity;
import top.daoha.domain.trade.model.valobj.TeamRefundSuccess;
import top.daoha.domain.trade.service.refund.business.AbstractRefundOrderStrategy;
import javax.annotation.Resource;

@Slf4j
@Service("paid2RefundStrategy")
public class Paid2RefundStrategy extends AbstractRefundOrderStrategy {

    @Resource
    private ITradeRepository tradeRepository;

    @Override
    public void refundOrder(TradeRefundOrderEntity tradeRefundOrderEntity) {
        log.info("退单，已支付，未成团。订单信息:{}",tradeRefundOrderEntity);

        //先更更新订单状态
        GroupBuyRefundAggregate groupBuyRefundAggregate =
                GroupBuyRefundAggregate.buildPaid2RefundAggregate(tradeRefundOrderEntity,-1,-1);
        NotifyTaskEntity notifyTaskEntity = tradeRepository.paid2Refund(groupBuyRefundAggregate);

        //然后发送MQ消息
        sendRefundNotifyMessage(notifyTaskEntity,"已支付未成团");
    }

    @Override
    public void reverseStock(TeamRefundSuccess teamRefundSuccess) {
        log.info("退单恢复锁单量--未支付未成团的情况，但是有锁单记录:{} :{}",teamRefundSuccess.getUserId(),teamRefundSuccess);
        doReverseStock(teamRefundSuccess, "已支付，未成团，但有锁单记录，要恢复锁单库存");
    }
}
