package top.daoha.domain.activity.service.discount;

import lombok.extern.slf4j.Slf4j;
import top.daoha.domain.activity.adapter.repository.IActivityRepository;
import top.daoha.domain.activity.model.valobj.DiscountTypeEnum;
import top.daoha.domain.activity.model.valobj.GroupBuyActivityDiscountVO;

import javax.annotation.Resource;
import java.math.BigDecimal;

/**
 * @ClassName : AbstractDiscountCalculateService
 * @Description :
 * @github:
 * @Author : 24209
 * @Date: 2026/3/26  15:23
 */
@Slf4j
public abstract class AbstractDiscountCalculateService implements IDiscountCalculateService{

    @Resource
    protected IActivityRepository iActivityRepository;

    @Override
    public BigDecimal calculate(String userId, BigDecimal originalPrice, GroupBuyActivityDiscountVO.GroupBuyDiscount groupBuyDiscount) {
        //1首先进行人群标签过滤
        if(DiscountTypeEnum.TAG!=groupBuyDiscount.getDiscountType()){
            if(!filterTagId(userId,groupBuyDiscount.getTagId())) {
                log.info("用户{}未被人群标签{}过滤", userId, groupBuyDiscount.getTagId());
                return originalPrice;
            }
        }
        //通过过滤的进行折扣计算
        return doCalculate(originalPrice,groupBuyDiscount);
    }

    private boolean filterTagId(String userId,String tagId){
        //过滤人群逻辑后期编写
        return iActivityRepository.isTagCrowRange(userId,tagId);
    }

    protected abstract BigDecimal doCalculate(BigDecimal originalPrice, GroupBuyActivityDiscountVO.GroupBuyDiscount groupBuyDiscount);
}
