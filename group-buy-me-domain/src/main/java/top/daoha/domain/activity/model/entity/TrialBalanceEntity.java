package top.daoha.domain.activity.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.daoha.domain.activity.model.valobj.GroupBuyActivityDiscountVO;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName : TrialBalanceEntity
 * @Description : 商品经过seivice后试算的结果
 * @github:
 * @Author : 24209
 * @Date: 2026/3/22  15:26
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TrialBalanceEntity {

    private String goodsId;
    private String goodsName;
    private BigDecimal originalPrice;
    private BigDecimal deductionPrice;
    private Integer targetCount;
    private Date startTime;
    private Date endTime;
    private Boolean isVisible;
    private Boolean isEnable;


    private GroupBuyActivityDiscountVO groupBuyActivityDiscountVO;

}
