package top.daoha.domain.trade.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import top.daoha.types.enums.GroupBuyOrderEnumVO;

import java.util.Arrays;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum RefundTypeEnumVO {

    UNPAID_UNLOCK("unpaid_unlock", "unpaid2RefundStrategy", "，未支付，未成团") {
        @Override
        public boolean matches(GroupBuyOrderEnumVO groupBuyOrderEnumVO, TradeOrderStatusEnumVO tradeOrderStatusEnumVO) {
            return GroupBuyOrderEnumVO.PROGRESS.equals(groupBuyOrderEnumVO) && TradeOrderStatusEnumVO.CREATE.equals(tradeOrderStatusEnumVO);
        }
    },
    PAID_UNFORMED("paid_unfromed", "paid2RefundStrategy", "，已支付，未成团") {
        @Override
        public boolean matches(GroupBuyOrderEnumVO groupBuyOrderEnumVO, TradeOrderStatusEnumVO tradeOrderStatusEnumVO){
            return GroupBuyOrderEnumVO.PROGRESS.equals(groupBuyOrderEnumVO)&& TradeOrderStatusEnumVO.COMPLETED.equals(tradeOrderStatusEnumVO);
        }
    },

    PAID_FORMED("paid_formed", "paidTeam2RefundStrategy", "，已支付，已完成团") {
        @Override
        public boolean matches(GroupBuyOrderEnumVO groupBuyOrderEnumVO, TradeOrderStatusEnumVO tradeOrderStatusEnumVO){
            return GroupBuyOrderEnumVO.COMPLETE.equals(groupBuyOrderEnumVO)&& TradeOrderStatusEnumVO.COMPLETED.equals(tradeOrderStatusEnumVO);
        }
    },

    ;


    private String code;
    private String strategy;
    private String info;

    public abstract boolean matches(GroupBuyOrderEnumVO groupBuyOrderEnumVO, TradeOrderStatusEnumVO tradeOrderStatusEnumVO);

    /**
     * 根据状态组合获取对应的退款策略枚举
     */
    public static RefundTypeEnumVO getRefundStrategy(GroupBuyOrderEnumVO groupBuyOrderEnumVO, TradeOrderStatusEnumVO tradeOrderStatusEnumVO) {
        return Arrays.stream(values())
                .filter(refundType -> refundType.matches(groupBuyOrderEnumVO, tradeOrderStatusEnumVO))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("不支持的退款状态组合: groupBuyOrderStatus=" + groupBuyOrderEnumVO + ", tradeOrderStatus=" + tradeOrderStatusEnumVO));
    }


    public static RefundTypeEnumVO valueOf(Integer code) {
        switch (code) {
            case 1:
                return UNPAID_UNLOCK;
            case 2:
                return PAID_UNFORMED;
            case 3:
                return PAID_FORMED;
        }
        throw new RuntimeException("退单类型枚举值不存在: " + code);
    }
}
