package top.daoha.domain.trade.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TradePaySettlementEntity {
    /** 渠道 */
    private String source;
    /** 来源 */
    private String channel;
    /**  用户id */
    private String userId;
    /**  团队id */
    private String teamId;
    /**  活动id */
    private Long activityId;
    /**  外部订单编号 */
    private String outTradeNo;
}
