package top.daoha.infrastructure.adapter.repository;

import org.redisson.api.RBitSet;
import org.springframework.stereotype.Repository;
import top.daoha.domain.activity.adapter.repository.IActivityRepository;
import top.daoha.domain.activity.model.entity.UserGroupBuyOrderDetailEntity;
import top.daoha.domain.activity.model.valobj.*;
import top.daoha.infrastructure.dao.*;
import top.daoha.infrastructure.dao.po.*;
import top.daoha.infrastructure.dcc.DCCService;
import top.daoha.infrastructure.redis.IRedisService;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

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
    IGroupBuyOrderListDao iGroupBuyOrderListDao;

    @Resource
    IGroupBuyOrderDao  iGroupBuyOrderDao;

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

    @Override
    public List<UserGroupBuyOrderDetailEntity> queryInProgressUserGroupBuyOrderDetailListByOwner(Long activityId, String userId, int ownerCount) {

        GroupBuyOrderList groupBuyOrderListReq = GroupBuyOrderList.builder()
                .userId(userId)
                .activityId(activityId)
                .build();
        groupBuyOrderListReq.setCount(ownerCount);
        //查询出拼团订单信息
        List<GroupBuyOrderList> groupBuyOrderLists = iGroupBuyOrderListDao.queryTeamIdByUserIdActivityId(groupBuyOrderListReq);

        if(groupBuyOrderLists == null || groupBuyOrderLists.isEmpty()) return null;

        Set<String> teamIds = groupBuyOrderLists.stream()
                .map(GroupBuyOrderList::getTeamId)
                .filter(teamId ->teamId !=null&&!teamId.isEmpty())
                .collect(Collectors.toSet());


        List<GroupBuyOrder> groupBuyOrders=iGroupBuyOrderDao.queryGroupBuyProgressByTeamIds(teamIds);
        if(groupBuyOrders == null || groupBuyOrders.isEmpty()) return null;

        Map<String,GroupBuyOrder> groupBuyOrderMap = groupBuyOrders.stream()
                .collect(Collectors.toMap(GroupBuyOrder::getTeamId,order->order));




        List<UserGroupBuyOrderDetailEntity>  userGroupBuyOrderDetailEntities = new ArrayList<>();

        for(GroupBuyOrderList  groupBuyOrderList:groupBuyOrderLists){
            String teamId = groupBuyOrderList.getTeamId();
            GroupBuyOrder groupBuyOrder = groupBuyOrderMap.get(teamId);
            if(null == groupBuyOrder) continue;

            UserGroupBuyOrderDetailEntity userGroupBuyOrderDetailEntity = UserGroupBuyOrderDetailEntity.builder()
                    .teamId(groupBuyOrder.getTeamId())
                    .userId(userId)
                    .activityId(activityId)
                    .targetCount(groupBuyOrder.getTargetCount())
                    .completeCount(groupBuyOrder.getCompleteCount())
                    .lockCount(groupBuyOrder.getLockCount())
                    .validStartTime(groupBuyOrder.getValidStartTime())
                    .validEndTime(groupBuyOrder.getValidEndTime())
                    .outTradeNo(groupBuyOrderList.getOutTradeNo())
                    .build();
            userGroupBuyOrderDetailEntities.add(userGroupBuyOrderDetailEntity);
        }

        return userGroupBuyOrderDetailEntities;
    }

    @Override
    public List<UserGroupBuyOrderDetailEntity> queryInProgressUserGroupBuyOrderDetailListByRandom(Long activityId, String userId, int randomCount) {
        GroupBuyOrderList groupBuyOrderListReq = GroupBuyOrderList.builder()
                .userId(userId)
                .activityId(activityId)
                .build();
        groupBuyOrderListReq.setCount(randomCount*2);//查询两倍但是取其中randomCount
        //查询出拼团订单信息
        List<GroupBuyOrderList> groupBuyOrderLists = iGroupBuyOrderListDao.queryTeamIdByRandom(groupBuyOrderListReq);

        if(groupBuyOrderLists == null || groupBuyOrderLists.isEmpty()) return null;

        if(groupBuyOrderLists.size()>randomCount){
            //随机打乱列表
            Collections.shuffle(groupBuyOrderLists);
            //获取前randomCount个元素
            groupBuyOrderLists = groupBuyOrderLists.subList(0, randomCount);
        }
        Set<String> teamIds = groupBuyOrderLists.stream()
                .map(GroupBuyOrderList::getTeamId)
                .filter(teamId->teamId!=null&&!teamId.isEmpty())
                .collect(Collectors.toSet());

        List<GroupBuyOrder> groupBuyOrders=iGroupBuyOrderDao.queryGroupBuyProgressByTeamIds(teamIds);
        if(groupBuyOrders == null || groupBuyOrders.isEmpty()) return null;

        Map<String,GroupBuyOrder> groupBuyOrderMap = groupBuyOrders.stream()
                .collect(Collectors.toMap(GroupBuyOrder::getTeamId,order->order));




        List<UserGroupBuyOrderDetailEntity>  userGroupBuyOrderDetailEntities = new ArrayList<>();

        for(GroupBuyOrderList  groupBuyOrderList:groupBuyOrderLists){
            String teamId = groupBuyOrderList.getTeamId();
            GroupBuyOrder groupBuyOrder = groupBuyOrderMap.get(teamId);
            if(null == groupBuyOrder) continue;

            UserGroupBuyOrderDetailEntity userGroupBuyOrderDetailEntity = UserGroupBuyOrderDetailEntity.builder()
                    .teamId(groupBuyOrder.getTeamId())
                    .userId(groupBuyOrderList.getUserId())
                    .activityId(activityId)
                    .targetCount(groupBuyOrder.getTargetCount())
                    .completeCount(groupBuyOrder.getCompleteCount())
                    .lockCount(groupBuyOrder.getLockCount())
                    .validStartTime(groupBuyOrder.getValidStartTime())
                    .validEndTime(groupBuyOrder.getValidEndTime())
                    .outTradeNo(groupBuyOrderList.getOutTradeNo())
                    .build();
            userGroupBuyOrderDetailEntities.add(userGroupBuyOrderDetailEntity);
        }
        return userGroupBuyOrderDetailEntities;
    }

    @Override
    public TeamStatisticVO queryTeamStatisticByActivity(Long activityId) {
        //首先查询出所有的队伍信息
        List<GroupBuyOrderList> groupBuyOrderLists = iGroupBuyOrderListDao.queryInProgressTeamStatisticByActivity(activityId);
        Set<String> teamIds = groupBuyOrderLists.stream()
                .map(GroupBuyOrderList::getTeamId)
                .filter(teamId->teamId!=null&&!teamId.isEmpty())
                .collect(Collectors.toSet());

        Integer allteamCount = iGroupBuyOrderDao.queryALLTeamCount(teamIds);
        Integer allTeamCompleteCount = iGroupBuyOrderDao.queryALLTeamCompleteCount(teamIds);
        Integer allTeamUserCount = iGroupBuyOrderDao.queryALLUserCount(teamIds);

        return TeamStatisticVO.builder()
                .allTeamCount(allteamCount)
                .allTeamCompleteCount(allTeamCompleteCount)
                .allTeamUserCount(allTeamUserCount)
                .build();
    }
}
