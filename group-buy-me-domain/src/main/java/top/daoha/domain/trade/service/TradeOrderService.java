package top.daoha.domain.trade.service;

import org.springframework.stereotype.Service;
import top.daoha.domain.trade.adapter.repository.ITradeRepository;
import top.daoha.domain.trade.model.aggregate.GroupBuyOrderAggregate;
import top.daoha.domain.trade.model.entity.MarketPayOrderEntity;
import top.daoha.domain.trade.model.entity.PayActivityEntity;
import top.daoha.domain.trade.model.entity.PayDiscountEntity;
import top.daoha.domain.trade.model.entity.UserEntity;
import top.daoha.domain.trade.model.valobj.GroupBuyProgressVO;

import javax.annotation.Resource;
@Service
public class TradeOrderService implements ITradeOrderService {

    @Resource
    private  ITradeRepository tradeRepository;

    //
    @Override
    public MarketPayOrderEntity queryNoPayMarketPayOrderByOutTradeNo(String userId, String outTradeNo) {
        //使用userId和outTradeNo在group buy order list表中查询预购订单是否存在
        return tradeRepository.queryNoPayMarketPayOrderByOutTradeNo(userId, outTradeNo);
    }

    @Override
    public GroupBuyProgressVO queryGroupBuyProgress(String teamId) {
        //使用teamId在group buy order表中查询拼团进度
        return tradeRepository.queryGroupBuyProgress(teamId);
    }

    @Override
    public MarketPayOrderEntity lockMarketPayOrder(UserEntity userEntity, PayActivityEntity payActivityEntity, PayDiscountEntity payDiscountEntity) {
        //锁定预购订单，第一个就创建，已存在就+1
        GroupBuyOrderAggregate groupBuyOrderAggregate = new GroupBuyOrderAggregate(userEntity,payActivityEntity,payDiscountEntity);

        return tradeRepository.lockMarketPayOrder(groupBuyOrderAggregate);
    }
}
