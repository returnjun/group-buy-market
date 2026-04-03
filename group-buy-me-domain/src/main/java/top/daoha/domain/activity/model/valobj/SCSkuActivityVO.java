package top.daoha.domain.activity.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//这是一个关于互动和商品关联的对象
public class SCSkuActivityVO {

    /** 来源 */
    private String source;
    /** 渠道 */
    private String channel;
    /** 活动ID */
    private Long activityId;
    /**  商品id */
    private String goodsId;

}
