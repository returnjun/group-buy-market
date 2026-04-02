package top.daoha.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName : MarketProdyctEntity
 * @Description : 营销商品信息
 * @github:
 * @Author : 24209
 * @Date: 2026/3/22  15:23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MarketProductEntity {
    private Long activityId;
    private String userId;
    private String goodsId;
    private String source;
    private String channel;
}
