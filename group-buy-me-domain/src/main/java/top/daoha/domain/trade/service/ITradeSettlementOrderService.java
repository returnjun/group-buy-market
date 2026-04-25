package top.daoha.domain.trade.service;


import top.daoha.domain.trade.model.entity.NotifyTaskEntity;
import top.daoha.domain.trade.model.entity.TradePaySettlementEntity;
import top.daoha.domain.trade.model.entity.TradePaySuccessEntity;

import java.util.Map;

public interface ITradeSettlementOrderService {

    TradePaySettlementEntity settlementOrder(TradePaySuccessEntity tradePaySuccessEntity) throws Exception;

    Map<String,Integer> execSettlementNotifyJob() throws Exception;

    Map<String,Integer> execSettlementNotifyJob(String teamId) throws Exception;

    Map<String,Integer> execSettlementNotifyJob(NotifyTaskEntity notifyTaskEntity) throws Exception;

}
