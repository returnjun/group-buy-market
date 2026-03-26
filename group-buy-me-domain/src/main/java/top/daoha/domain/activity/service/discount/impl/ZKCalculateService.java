package top.daoha.domain.activity.service.discount.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.daoha.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import top.daoha.domain.activity.service.discount.AbstractDiscountCalculateService;

import java.math.BigDecimal;

/**
 * @ClassName : ZKCalculateService
 * @Description :
 * @github:
 * @Author : 24209
 * @Date: 2026/3/26  15:44
 */
@Slf4j
@Service("ZK")
public class ZKCalculateService extends AbstractDiscountCalculateService {

    @Override
    protected BigDecimal doCalculate(BigDecimal originalPrice, GroupBuyActivityDiscountVO.GroupBuyDiscount groupBuyDiscount) {
        String marketExpr = groupBuyDiscount.getMarketExpr();
        BigDecimal k=new BigDecimal(marketExpr);
        BigDecimal res = originalPrice.multiply(k);
        if(res.compareTo(BigDecimal.ZERO)<=0)
            return new BigDecimal("0.01");
        return res;
    }
}
