package top.daoha.domain.trade.adapter.post;

import top.daoha.domain.trade.model.entity.NotifyTaskEntity;

public interface ITradePort {


    String groupBuyNotify(NotifyTaskEntity notifyTaskEntity) throws Exception;

}
