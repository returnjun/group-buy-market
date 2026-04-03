package top.daoha.domain.activity.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import top.daoha.types.common.Constants;

import java.util.Objects;

/**
 * @ClassName : GroupBuyActivityDiscountVO
 * @Description :
 * @github:
 * @Author : 24209
 * @Date: 2026/3/25  15:02
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//这是一个包含活动，商品，折扣策略，价格的综合的VO，十分重要
public class GroupBuyActivityDiscountVO {
    /** 活动ID */
    private Long activityId;
    /** 活动名称 */
    private String activityName;
    /** 来源 */
    private String source;
    /** 渠道 */
    private String channel;
    /**  商品id */
    private String goodsId;
    /** 折扣ID */
    private GroupBuyDiscount groupBuyDiscount;
    /** 拼团方式（0自动成团、1达成目标拼团） */
    private Integer groupType;
    /** 拼团次数限制 */
    private Integer takeLimitCount;
    /** 拼团目标 */
    private Integer target;
    /** 拼团时长（分钟） */
    private Integer validTime;
    /** 活动状态（0创建、1生效、2过期、3废弃） */
    private Integer status;
    /** 活动开始时间 */
    private java.util.Date startTime;
    /** 活动结束时间 */
    private java.util.Date endTime;
    /** 人群标签规则标识 */
    private String tagId;
    /** 人群标签规则范围（多选；1可见限制、2参与限制） */
    private String tagScope;


    public boolean isVisible(){
        if(StringUtils.isBlank(this.tagScope)){
            return TagScopeEnumVO.Visible.getAllow();
        }
        String[] split = this.tagScope.split(Constants.SPLIT);
        if(split.length>0&& Objects.equals("1",split[0])){
            return TagScopeEnumVO.Visible.getRefuse();
        }
        return TagScopeEnumVO.Visible.getAllow();
    }

    public boolean isEnable(){
        if(StringUtils.isBlank(this.tagScope)){
            return TagScopeEnumVO.Enable.getAllow();
        }
        String[] split = this.tagScope.split(Constants.SPLIT);
        if(split.length==2&& Objects.equals("2",split[1])){
            return TagScopeEnumVO.Enable.getRefuse();
        }
        return TagScopeEnumVO.Enable.getAllow();
    }




    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GroupBuyDiscount{
        /** * 折扣标题  */
        private String discountName;
        /** 折扣描述*/
        private String discountDesc;
        /**折扣类型（0:base、1:tag）  */
        private DiscountTypeEnum discountType;
        /**营销优惠计划（ZJ:直减、MJ:满减、N元购） */
        private String marketPlan;
        /**营销优惠表达式  */
        private String marketExpr;
        /**人群标签，特定优惠限定 */
        private String tagId;
    }
}
