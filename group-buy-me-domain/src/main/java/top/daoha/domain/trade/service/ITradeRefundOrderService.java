package top.daoha.domain.trade.service;


import top.daoha.domain.trade.model.entity.TradeRefundBehaviorEntity;
import top.daoha.domain.trade.model.entity.TradeRefundCommandEntity;
import top.daoha.domain.trade.model.valobj.TeamRefundSuccess;

public interface ITradeRefundOrderService {
       //用户退单
       TradeRefundBehaviorEntity refundOrder(TradeRefundCommandEntity tradeRefundCommandEntity);

       //退单恢复锁单库存
       void restoreTeamLockStock(TeamRefundSuccess teamRefundSuccess);
}
