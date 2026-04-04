package top.daoha.domain.trade.service;


import top.daoha.domain.trade.model.entity.TradePaySettlementEntity;
import top.daoha.domain.trade.model.entity.TradePaySuccessEntity;

public interface ITradeSettlementOrderService {
    TradePaySettlementEntity settlementOrder(TradePaySuccessEntity tradePaySuccessEntity);
}
