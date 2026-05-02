package top.daoha.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradeRefundCommandEntity {
    /** 用户ID */
    private String userId;
    /** 订单号 */
    private String outTradeNo;
    /** 来源 */
    private String source;
    /** 渠道 */
    private String channel;
}
