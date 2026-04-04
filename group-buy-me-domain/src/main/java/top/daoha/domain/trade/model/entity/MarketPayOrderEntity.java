package top.daoha.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.daoha.domain.trade.model.valobj.TradeOrderStatusEnum;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class MarketPayOrderEntity {
    /** 拼团组队ID */
    private String teamId;
    /** 预购订单ID */
    private String orderId;
    /** 折扣金额 */
    private BigDecimal deductionPrice;
    /** 交易订单状态枚举 */
    private TradeOrderStatusEnum status;
}
