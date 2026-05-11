package top.daoha.domain.trade.model.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class PayActivityEntity {

    /** 拼单组队ID */
    private String teamId;
    /** 活动ID */
    private Long activityId;
    /** 活动名称 */
    private String activityName;
    /** 活动开始时间 */
    private Date startTime;
    /** 活动结束时间 */
    private Date endTime;
    /** 优惠下单后持续时间 */
    private Integer validTime;
    /** 目标数量 */
    private Integer targetCount;

    public Date createEndtime() {
        // 1. 非空校验，视你的业务逻辑也可以选择 return null;
        if (this.startTime == null || this.validTime == null) {
            throw new IllegalArgumentException("开始时间(startTime)或拼团时长(validTime)不能为空");
        }

        // 2. 将 validTime (分钟) 转换为毫秒并相加
        // 注意：使用 60000L (60 * 1000) 确保以 long 类型计算，防止 Integer 溢出
        long endMillis = this.startTime.getTime() + (this.validTime * 60000L);

        // 3. 赋值给 endTime 属性
        this.endTime = new Date(endMillis);

        // 4. 返回计算后的 endTime
        return this.endTime;
    }
}
