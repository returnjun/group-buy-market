package top.daoha.domain.trade.service;

import top.daoha.domain.trade.model.entity.MarketPayOrderEntity;
import top.daoha.domain.trade.model.entity.PayActivityEntity;
import top.daoha.domain.trade.model.entity.PayDiscountEntity;
import top.daoha.domain.trade.model.entity.UserEntity;
import top.daoha.domain.trade.model.valobj.GroupBuyProgressVO;

public interface ITradeLockOrderService {

    MarketPayOrderEntity queryNoPayMarketPayOrderByOutTradeNo(String userId,String outTradeNo);

    GroupBuyProgressVO queryGroupBuyProgress(String teamId);

    MarketPayOrderEntity lockMarketPayOrder(UserEntity userEntity, PayActivityEntity payActivityEntity, PayDiscountEntity payDiscountEntity) throws Exception;

}
