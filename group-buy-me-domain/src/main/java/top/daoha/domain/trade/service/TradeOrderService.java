package top.daoha.domain.trade.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.daoha.domain.trade.adapter.repository.ITradeRepository;
import top.daoha.domain.trade.model.aggregate.GroupBuyOrderAggregate;
import top.daoha.domain.trade.model.entity.*;
import top.daoha.domain.trade.model.valobj.GroupBuyProgressVO;
import top.daoha.domain.trade.service.factory.TradeRuleFilterFactory;
import top.daoha.types.desgin.framework.link.model2.chain.BusinessLinkedList;

import javax.annotation.Resource;
@Slf4j
@Service
public class TradeOrderService implements ITradeOrderService {

    @Resource
    private  ITradeRepository tradeRepository;

    @Resource(name = "tradeRuleFilter1")
    BusinessLinkedList<TradeRuleCommandEntity, TradeRuleFilterFactory.DynamicContext,TradeRuleFilterBackEntity> tradeRuleFilter;

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
    public MarketPayOrderEntity lockMarketPayOrder(UserEntity userEntity, PayActivityEntity payActivityEntity, PayDiscountEntity payDiscountEntity) throws Exception {
        //最终返回的是链的最后一个节点的结果
        TradeRuleFilterBackEntity apply = tradeRuleFilter.apply(TradeRuleCommandEntity.builder()
                .activityId(payActivityEntity.getActivityId())
                .userId(userEntity.getUserId())
                .build(),
                new TradeRuleFilterFactory.DynamicContext()
        );

        Integer userTakeOrderCount = apply.getUserTakeOrderCount();


        //锁定预购订单，第一个就创建，已存在就+1
        GroupBuyOrderAggregate groupBuyOrderAggregate = new GroupBuyOrderAggregate(userEntity,payActivityEntity,payDiscountEntity,userTakeOrderCount);

        return tradeRepository.lockMarketPayOrder(groupBuyOrderAggregate);
    }
}
