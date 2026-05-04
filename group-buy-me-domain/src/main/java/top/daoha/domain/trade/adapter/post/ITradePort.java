package top.daoha.domain.trade.adapter.post;

import top.daoha.domain.trade.model.entity.NotifyTaskEntity;

import java.util.Map;

public interface ITradePort {


    String groupBuyNotify(NotifyTaskEntity notifyTaskEntity) throws Exception;


}
