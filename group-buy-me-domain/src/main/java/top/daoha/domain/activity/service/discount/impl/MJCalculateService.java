package top.daoha.domain.activity.service.discount.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.daoha.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import top.daoha.domain.activity.service.discount.AbstractDiscountCalculateService;
import top.daoha.types.common.Constants;

import java.math.BigDecimal;

/**
 * @ClassName : MJCalculateService
 * @Description :
 * @github:
 * @Author : 24209
 * @Date: 2026/3/26  15:42
 */
@Slf4j
@Service("MJ")
public class MJCalculateService extends AbstractDiscountCalculateService {
    @Override
    protected BigDecimal doCalculate(BigDecimal originalPrice, GroupBuyActivityDiscountVO.GroupBuyDiscount groupBuyDiscount) {
        String marketExpr = groupBuyDiscount.getMarketExpr();
        String[] spilt = marketExpr.split(Constants.SPLIT);
        BigDecimal x = new BigDecimal(spilt[0]);
        BigDecimal y = new BigDecimal(spilt[1]);

        if (originalPrice.compareTo(x) <= 0) {//满足满减条件
            if (originalPrice.compareTo(y) > 0)
                return originalPrice.subtract(y);
            else
                return new BigDecimal("0.01");

        }
        return originalPrice;
    }
}
