package top.daoha.domain.trade.service;


import top.daoha.domain.trade.model.entity.NotifyTaskEntity;
import top.daoha.domain.trade.model.entity.TradePaySettlementEntity;
import top.daoha.domain.trade.model.entity.TradePaySuccessEntity;

import java.util.Map;

public interface ITradeTaskService {

    Map<String,Integer> execNotifyJob() throws Exception;

    Map<String,Integer> execNotifyJob(String teamId) throws Exception;

    Map<String,Integer> execNotifyJob(NotifyTaskEntity notifyTaskEntity) throws Exception;

}
