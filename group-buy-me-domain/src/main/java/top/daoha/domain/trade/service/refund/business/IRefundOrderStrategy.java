package top.daoha.domain.trade.service.refund.business;

import top.daoha.domain.trade.model.entity.TradeRefundOrderEntity;
import top.daoha.domain.trade.model.valobj.TeamRefundSuccess;

public interface IRefundOrderStrategy {
    //退单的策略
    void refundOrder(TradeRefundOrderEntity tradeRefundOrderEntity);

    void reverseStock(TeamRefundSuccess teamRefundSuccess);
}
