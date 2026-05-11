package top.daoha.domain.trade.service;


import top.daoha.domain.activity.model.entity.UserGroupBuyOrderDetailEntity;
import top.daoha.domain.trade.model.entity.TradeRefundBehaviorEntity;
import top.daoha.domain.trade.model.entity.TradeRefundCommandEntity;
import top.daoha.domain.trade.model.valobj.TeamRefundSuccess;

import java.util.List;

public interface ITradeRefundOrderService {
       //用户退单
       TradeRefundBehaviorEntity refundOrder(TradeRefundCommandEntity tradeRefundCommandEntity) throws Exception;

       //退单恢复锁单库存
       void restoreTeamLockStock(TeamRefundSuccess teamRefundSuccess);

       /**
        * 查询超时未支付订单列表
        * 条件：当前时间不在活动时间范围内、状态为0（初始锁定）、out_trade_time为空
        * @return 超时未支付订单列表，限制10条
        */
       List<UserGroupBuyOrderDetailEntity> queryTimeoutUnpaidOrderList();
}
