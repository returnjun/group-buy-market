package top.daoha.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserGroupBuyOrderDetailEntity {

    /**  用户Id */
    private String userId;
    /** 拼团Id */
    private String teamId;
    /** 活动Id */
    private Long activityId;
    /** 目标个数 */
    private Integer targetCount;
    /** 完成个数 */
    private Integer completeCount;
    /** 锁单个数 */
    private Integer lockCount;
    /** 开始时间 */
    private Date validStartTime;
    /** 结束时间 */
    private Date validEndTime;
    /** 外部订单号 */
    private String outTradeNo;
}
