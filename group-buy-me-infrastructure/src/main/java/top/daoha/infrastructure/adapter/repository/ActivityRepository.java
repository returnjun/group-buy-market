package top.daoha.infrastructure.adapter.repository;

import org.springframework.stereotype.Repository;
import top.daoha.domain.activity.adapter.repository.IActivityRepository;
import top.daoha.domain.activity.model.valobj.DiscountTypeEnum;
import top.daoha.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import top.daoha.domain.activity.model.valobj.SkuVO;
import top.daoha.infrastructure.dao.IGroupBuyActivityDao;
import top.daoha.infrastructure.dao.IGroupBuyDiscountDao;
import top.daoha.infrastructure.dao.ISkuDao;
import top.daoha.infrastructure.dao.po.GroupBuyActivity;
import top.daoha.infrastructure.dao.po.GroupBuyDiscount;
import top.daoha.infrastructure.dao.po.Sku;

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

    @Override
    public GroupBuyActivityDiscountVO queryGroupBuyActivityDiscount(String source, String channel) {
        GroupBuyActivity req = new GroupBuyActivity();
        req.setSource(source);
        req.setChannel(channel);
        GroupBuyActivity res=iGroupBuyActivityDao.queryValidGroupBuyActivity(req);
        String discountId=res.getDiscountId();
        GroupBuyDiscount groupBuyDiscount = iGroupBuyDiscountDao.queryGroupBuyActivityDiscountByDiscountId(discountId);

        GroupBuyActivityDiscountVO.GroupBuyDiscount groupBuyDiscount1 =new GroupBuyActivityDiscountVO.GroupBuyDiscount(
                groupBuyDiscount.getDiscountName(),groupBuyDiscount.getDiscountDesc(), DiscountTypeEnum.get(groupBuyDiscount.getDiscountType()),
                groupBuyDiscount.getMarketPlan(),groupBuyDiscount.getMarketExpr(),groupBuyDiscount.getTagId()
        );


        GroupBuyActivityDiscountVO groupBuyActivityDiscountVO = new GroupBuyActivityDiscountVO();
        groupBuyActivityDiscountVO.setActivityId(res.getActivityId());
        groupBuyActivityDiscountVO.setActivityName(res.getActivityName());
        groupBuyActivityDiscountVO.setSource(res.getSource());
        groupBuyActivityDiscountVO.setChannel(res.getChannel());
        groupBuyActivityDiscountVO.setGoodsId(res.getGoodsId());
        groupBuyActivityDiscountVO.setGroupBuyDiscount(groupBuyDiscount1);

        groupBuyActivityDiscountVO.setGroupType(res.getGroupType());
        groupBuyActivityDiscountVO.setTakeLimitCount(res.getTakeLimitCount());
        groupBuyActivityDiscountVO.setTarget(res.getTarget());
        groupBuyActivityDiscountVO.setValidTime(res.getValidTime());
        groupBuyActivityDiscountVO.setStatus(res.getStatus());
        groupBuyActivityDiscountVO.setStartTime(res.getStartTime());
        groupBuyActivityDiscountVO.setEndTime(res.getEndTime());
        groupBuyActivityDiscountVO.setTagId(res.getTagId());
        groupBuyActivityDiscountVO.setTagScope(res.getTagScope());

        return groupBuyActivityDiscountVO;
    }

    @Override
    public SkuVO querySkuByGoosId(String goodsId) {
        Sku sku = iSkuDao.queryByGoodsId(goodsId);
        SkuVO skuVO = new SkuVO();
        skuVO.setGoodsId(sku.getGoodsId());
        skuVO.setGoodsName(sku.getGoodsName());
        skuVO.setOriginalPrice(sku.getOriginalPrice());
        return skuVO;
    }
}
