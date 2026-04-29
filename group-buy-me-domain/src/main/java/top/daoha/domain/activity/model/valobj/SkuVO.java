package top.daoha.domain.activity.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @ClassName : SkuVO
 * @Description :
 * @github:
 * @Author : 24209
 * @Date: 2026/3/25  16:17
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//这是商品的对象，主要是用于展示信息
public class SkuVO {
    //商品id
    private String goodsId;
    //商品名称
    private String goodsName;
    //商品价格
    private BigDecimal originalPrice;


}
