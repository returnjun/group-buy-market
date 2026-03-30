package top.daoha.infrastructure.dao.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ScSkuActivity {
    private int id;
    /** 来源 */
    private String source;
    /** 渠道 */
    private String channel;
    /** 活动ID */
    private Long activityId;
    /**  商品id */
    private String goodsId;
    /** 创建时间 */
    private java.util.Date createTime;
    /** 更新时间 */
    private java.util.Date updateTime;
}
