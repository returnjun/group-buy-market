package top.daoha.domain.trade.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.daoha.domain.trade.model.entity.*;
import top.daoha.domain.trade.model.valobj.GroupBuyProgressVO;
import top.daoha.types.enums.GroupBuyOrderEnumVO;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupBuyRefundAggregate {

    private TradeRefundOrderEntity tradeRefundOrderEntity;

    private GroupBuyProgressVO groupBuyProgressVO;
    /**
     * 拼团枚举 0拼团中 1完成 2失败 3完成含失败
     */
    private GroupBuyOrderEnumVO groupBuyOrderEnumVO;

    public static GroupBuyRefundAggregate buildUnpaid2RefundAggregate(TradeRefundOrderEntity tradeRefundOrderEntity,Integer lockCount){

        GroupBuyRefundAggregate groupBuyRefundAggregate = new GroupBuyRefundAggregate();
        groupBuyRefundAggregate.setTradeRefundOrderEntity(tradeRefundOrderEntity);
        groupBuyRefundAggregate.setGroupBuyProgressVO(GroupBuyProgressVO.builder()
                        .lockCount(lockCount).build());
        return groupBuyRefundAggregate;
    }
    public static GroupBuyRefundAggregate buildPaid2RefundAggregate(TradeRefundOrderEntity tradeRefundOrderEntity,Integer lockCount,Integer completeCount){

        GroupBuyRefundAggregate groupBuyRefundAggregate = new GroupBuyRefundAggregate();
        groupBuyRefundAggregate.setTradeRefundOrderEntity(tradeRefundOrderEntity);
        groupBuyRefundAggregate.setGroupBuyProgressVO(GroupBuyProgressVO.builder()
                .lockCount(lockCount)
                .completeCount(completeCount)
                .build());
        return groupBuyRefundAggregate;
    }


    public static GroupBuyRefundAggregate buildPaidTeam2RefundAggregate(TradeRefundOrderEntity tradeRefundOrderEntity, Integer lockCount,Integer completeCount, GroupBuyOrderEnumVO status) {

        GroupBuyRefundAggregate groupBuyRefundAggregate = new GroupBuyRefundAggregate();
        groupBuyRefundAggregate.setTradeRefundOrderEntity(tradeRefundOrderEntity);
        groupBuyRefundAggregate.setGroupBuyOrderEnumVO(status);
        groupBuyRefundAggregate.setGroupBuyProgressVO(GroupBuyProgressVO.builder()
                .lockCount(lockCount)
                .completeCount(completeCount)
                .build());
        return groupBuyRefundAggregate;
    }
}
