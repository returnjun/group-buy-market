package top.daoha.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GoodsMarketRequestDTO {
    /**  用户id */
    private String userId;
    /** 渠道 */
    private String source;
    /** 来源 */
    private String channel;
    /**  商品id */
    private String goodsId;
}
