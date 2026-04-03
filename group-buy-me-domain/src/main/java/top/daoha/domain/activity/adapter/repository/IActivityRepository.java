package top.daoha.domain.activity.adapter.repository;

import top.daoha.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import top.daoha.domain.activity.model.valobj.SCSkuActivityVO;
import top.daoha.domain.activity.model.valobj.SkuVO;

/**
 * @ClassName : IActivityReposity
 * @Description :
 * @github:
 * @Author : 24209
 * @Date: 2026/3/25  15:00
 */
//数据库操作的核心接口。实现内容放在infrastructure层
public interface IActivityRepository {
    GroupBuyActivityDiscountVO queryGroupBuyActivityDiscount(Long activityId);

    SkuVO querySkuByGoosId(String goodsId);

    SCSkuActivityVO queryByGoodsId(String source, String channel, String goodsId);

    boolean isTagCrowRange(String tagId, String userId);

    boolean downgradeSwitch();

    boolean cutRange(String userId);
}
