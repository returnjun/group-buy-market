package top.daoha.domain.trade.service.refund.filter;

import cn.bugstack.wrench.design.framework.link.model2.handler.ILogicHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.daoha.domain.trade.adapter.repository.ITradeRepository;
import top.daoha.domain.trade.model.entity.*;
import top.daoha.domain.trade.model.valobj.TradeOrderStatusEnumVO;
import top.daoha.domain.trade.service.refund.factory.TradeRefundFilterFactory;
import top.daoha.domain.trade.service.settlement.factory.TradeSettlementRuleFilterFactory;
import top.daoha.types.enums.GroupBuyOrderEnumVO;
import top.daoha.types.enums.ResponseCode;
import top.daoha.types.exception.AppException;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@Service
public class DataNodeFilter implements ILogicHandler<TradeRefundCommandEntity, TradeRefundFilterFactory.DynamicContext,TradeRefundBehaviorEntity>{
    @Resource
    private ITradeRepository tradeRepository;

    @Override
    public  TradeRefundBehaviorEntity apply(TradeRefundCommandEntity requestParameter, TradeRefundFilterFactory.DynamicContext dynamicContext) throws Exception {
        //这个节点用来加载数据

        //首先查询相应的订单详细
        MarketPayOrderEntity marketPayOrderEntity = tradeRepository.queryNoPayMarketPayOrderByOutTradeNo(requestParameter.getUserId(),requestParameter.getOutTradeNo());
        String teamId = marketPayOrderEntity.getTeamId();

        //查询拼团信息
        GroupBuyTeamEntity groupBuyTeamEntity = tradeRepository.queryGroupBuyTeamByTeamId(teamId);

        //写入上下文
        dynamicContext.setGroupBuyTeamEntity(groupBuyTeamEntity);
        dynamicContext.setMarketPayOrderEntity(marketPayOrderEntity);

        return next(requestParameter,dynamicContext);
    }
}
