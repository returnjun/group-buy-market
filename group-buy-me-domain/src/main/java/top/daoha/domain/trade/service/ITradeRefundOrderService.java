package top.daoha.domain.trade.service;


import top.daoha.domain.trade.model.entity.TradeRefundBehaviorEntity;
import top.daoha.domain.trade.model.entity.TradeRefundCommandEntity;

public interface ITradeRefundOrderService {
       //用户退单
       TradeRefundBehaviorEntity refundOrder(TradeRefundCommandEntity tradeRefundCommandEntity);
}
