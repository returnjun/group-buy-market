package top.daoha.domain.activity.service.discount;

import top.daoha.domain.activity.model.valobj.GroupBuyActivityDiscountVO;

import java.math.BigDecimal;

/**
 * @ClassName : IDiscountCalculateService
 * @Description :
 * @github:
 * @Author : 24209
 * @Date: 2026/3/26  15:25
 */

public interface IDiscountCalculateService {

    BigDecimal calculate(String userId, BigDecimal originalPrice, GroupBuyActivityDiscountVO.GroupBuyDiscount groupBuyDiscount);
}
