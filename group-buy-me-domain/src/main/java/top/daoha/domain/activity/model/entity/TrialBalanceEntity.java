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
//这个是个关于商品的是算结果类，包含商品走完折扣后的信息
public class TrialBalanceEntity {

    /** 商品id */
    private String goodsId;
    /**商品名称 */
    private String goodsName;
    /**商品原价 */
    private BigDecimal originalPrice;
    /**折扣价格 */
    private BigDecimal deductionPrice;
    /**最终价格 */
    private BigDecimal payPrice;
    /**目标人数 */
    private Integer targetCount;
    /**活动开始时间 */
    private Date startTime;
    /**活动结束时间 */
    private Date endTime;
    /**活动是否可见 */
    private Boolean isVisible;
    /**活动是否可参与 */
    private Boolean isEnable;


    private GroupBuyActivityDiscountVO groupBuyActivityDiscountVO;

}
