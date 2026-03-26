package top.daoha.domain.activity.adapter.repository;

import top.daoha.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import top.daoha.domain.activity.model.valobj.SkuVO;

/**
 * @ClassName : IActivityReposity
 * @Description :
 * @github:
 * @Author : 24209
 * @Date: 2026/3/25  15:00
 */

public interface IActivityRepository {
    GroupBuyActivityDiscountVO queryGroupBuyActivityDiscount(String source,String channel);
    SkuVO querySkuByGoosId(String goodsId);
}
