package top.daoha.infrastructure.adapter.repository;

import org.redisson.api.RBitSet;
import org.springframework.stereotype.Repository;
import top.daoha.domain.activity.adapter.repository.IActivityRepository;
import top.daoha.domain.activity.model.valobj.DiscountTypeEnum;
import top.daoha.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import top.daoha.domain.activity.model.valobj.SCSkuActivityVO;
import top.daoha.domain.activity.model.valobj.SkuVO;
import top.daoha.infrastructure.dao.IGroupBuyActivityDao;
import top.daoha.infrastructure.dao.IGroupBuyDiscountDao;
import top.daoha.infrastructure.dao.IScSkuActivityDao;
import top.daoha.infrastructure.dao.ISkuDao;
import top.daoha.infrastructure.dao.po.GroupBuyActivity;
import top.daoha.infrastructure.dao.po.GroupBuyDiscount;
import top.daoha.infrastructure.dao.po.ScSkuActivity;
import top.daoha.infrastructure.dao.po.Sku;
import top.daoha.infrastructure.dcc.DCCService;
import top.daoha.infrastructure.redis.IRedisService;

import javax.annotation.Resource;

/**
 * @ClassName : ActivityRepository
 * @Description :
 * @github:
 * @Author : 24209
 * @Date: 2026/3/25  16:50
 */
@Repository
public class ActivityRepository implements IActivityRepository {
    @Resource
    IGroupBuyActivityDao iGroupBuyActivityDao;

    @Resource
    IGroupBuyDiscountDao iGroupBuyDiscountDao;

    @Resource
    ISkuDao iSkuDao;

    @Resource
    IScSkuActivityDao iScSkuActivityDao;

    @Resource
    private IRedisService iRedisService;

    @Resource
    private DCCService dccService;


    @Override
    public GroupBuyActivityDiscountVO queryGroupBuyActivityDiscount(Long activityId) {


        GroupBuyActivity groupBuyActivity = iGroupBuyActivityDao.queryGroupBuyActivityDiscountByActivityId(activityId);
        if (null == groupBuyActivity) return null;

        String discountId = groupBuyActivity.getDiscountId();

        GroupBuyDiscount groupBuyDiscount = iGroupBuyDiscountDao.queryGroupBuyActivityDiscountByDiscountId(discountId);
        if (null == groupBuyDiscount) return null;


        GroupBuyActivityDiscountVO.GroupBuyDiscount groupBuyDiscount1 = new GroupBuyActivityDiscountVO.GroupBuyDiscount(
                groupBuyDiscount.getDiscountName(), groupBuyDiscount.getDiscountDesc(), DiscountTypeEnum.get(groupBuyDiscount.getDiscountType()),
                groupBuyDiscount.getMarketPlan(), groupBuyDiscount.getMarketExpr(), groupBuyDiscount.getTagId()
        );


        GroupBuyActivityDiscountVO groupBuyActivityDiscountVO = new GroupBuyActivityDiscountVO();
        groupBuyActivityDiscountVO.setActivityId(activityId);
        groupBuyActivityDiscountVO.setActivityName(groupBuyActivity.getActivityName());

        groupBuyActivityDiscountVO.setGroupBuyDiscount(groupBuyDiscount1);

        groupBuyActivityDiscountVO.setGroupType(groupBuyActivity.getGroupType());
        groupBuyActivityDiscountVO.setTakeLimitCount(groupBuyActivity.getTakeLimitCount());
        groupBuyActivityDiscountVO.setTarget(groupBuyActivity.getTarget());
        groupBuyActivityDiscountVO.setValidTime(groupBuyActivity.getValidTime());
        groupBuyActivityDiscountVO.setStatus(groupBuyActivity.getStatus());
        groupBuyActivityDiscountVO.setStartTime(groupBuyActivity.getStartTime());
        groupBuyActivityDiscountVO.setEndTime(groupBuyActivity.getEndTime());
        groupBuyActivityDiscountVO.setTagId(groupBuyActivity.getTagId());
        groupBuyActivityDiscountVO.setTagScope(groupBuyActivity.getTagScope());

        return groupBuyActivityDiscountVO;
    }

    @Override
    public SkuVO querySkuByGoosId(String goodsId) {
        Sku sku = iSkuDao.queryByGoodsId(goodsId);
        if (null == sku) return null;
        SkuVO skuVO = new SkuVO();
        skuVO.setGoodsId(sku.getGoodsId());
        skuVO.setGoodsName(sku.getGoodsName());
        skuVO.setOriginalPrice(sku.getOriginalPrice());
        return skuVO;
    }


    @Override
    public SCSkuActivityVO queryByGoodsId(String source, String channel, String goodsId) {
        ScSkuActivity scSkuActivity = new ScSkuActivity();
        scSkuActivity.setSource(source);
        scSkuActivity.setChannel(channel);
        scSkuActivity.setGoodsId(goodsId);

        scSkuActivity = iScSkuActivityDao.queryByGoodsId(scSkuActivity);
        if (null == scSkuActivity) return null;

        SCSkuActivityVO scSkuActivityVO = new SCSkuActivityVO();
        scSkuActivityVO.setActivityId(scSkuActivity.getActivityId());
        scSkuActivityVO.setGoodsId(scSkuActivity.getGoodsId());
        scSkuActivityVO.setSource(scSkuActivity.getSource());
        scSkuActivityVO.setChannel(scSkuActivity.getChannel());
        return scSkuActivityVO;
    }

    @Override
    public boolean isTagCrowRange(String tagId, String userId) {
        RBitSet bitSet = iRedisService.getBitSet(tagId);
        if (!bitSet.isExists()) return true;
        return bitSet.get(iRedisService.getIndexFromUserId(userId));
    }

    @Override
    public boolean downgradeSwitch() {
        return dccService.isDowngradeSwitch();
    }

    @Override
    public boolean cutRange(String userId) {
        return dccService.isCutRange(userId);
    }
}
