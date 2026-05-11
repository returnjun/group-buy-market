package top.daoha.domain.trade.service.refund;

import cn.bugstack.wrench.design.framework.link.model2.chain.BusinessLinkedList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.daoha.domain.activity.model.entity.UserGroupBuyOrderDetailEntity;
import top.daoha.domain.trade.adapter.repository.ITradeRepository;
import top.daoha.domain.trade.model.entity.*;
import top.daoha.domain.trade.model.valobj.RefundTypeEnumVO;
import top.daoha.domain.trade.model.valobj.TeamRefundSuccess;
import top.daoha.domain.trade.service.ITradeRefundOrderService;
import top.daoha.domain.trade.service.refund.business.IRefundOrderStrategy;
import top.daoha.domain.trade.service.refund.factory.TradeRefundFilterFactory;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class TradeRefundOrderService implements ITradeRefundOrderService {

    private final ITradeRepository tradeRepository;

    private final Map<String, IRefundOrderStrategy> refundOrderStrategyMap;

    public TradeRefundOrderService(ITradeRepository tradeRepository, Map<String, IRefundOrderStrategy> refundOrderStrategyMap) {
        this.tradeRepository = tradeRepository;
        this.refundOrderStrategyMap = refundOrderStrategyMap;
    }

    @Resource(name = "tradeRefundRuleFilter1")
    private BusinessLinkedList<TradeRefundCommandEntity, TradeRefundFilterFactory.DynamicContext,TradeRefundBehaviorEntity>  tradeRefundRuleFilter;

    @Override
    public TradeRefundBehaviorEntity refundOrder(TradeRefundCommandEntity tradeRefundCommandEntity) throws Exception {
        log.info("逆向流程，退单操作 userId:{} outTrade:{}",tradeRefundCommandEntity.getUserId(),tradeRefundCommandEntity.getOutTradeNo());
        return tradeRefundRuleFilter.apply(tradeRefundCommandEntity,new TradeRefundFilterFactory.DynamicContext());
    }

    @Override
    public void restoreTeamLockStock(TeamRefundSuccess teamRefundSuccess) {
        log.info("逆向流程，恢复锁单量 useId:{} activity:{} teamId:{}",teamRefundSuccess.getUserId(),teamRefundSuccess.getActivityId(),teamRefundSuccess.getTeamId());
        String type = teamRefundSuccess.getType();

        //根据枚举值获取对应的退单类型
        RefundTypeEnumVO refundTypeEnumVO = RefundTypeEnumVO.getRefundTypeEnumVOByCode(type);
        IRefundOrderStrategy refundOrderStrategy = refundOrderStrategyMap.get(refundTypeEnumVO.getStrategy());

        //逆向库存操作，恢复锁单量
        refundOrderStrategy.reverseStock(teamRefundSuccess);

    }

    @Override
    public List<UserGroupBuyOrderDetailEntity> queryTimeoutUnpaidOrderList() {
        log.info("扫描数据，超时组队未支付订单");
        return tradeRepository.queryTimeoutUnpaidOrderList();
    }
}
