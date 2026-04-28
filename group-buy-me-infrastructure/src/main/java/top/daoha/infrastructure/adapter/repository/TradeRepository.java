package top.daoha.infrastructure.adapter.repository;


import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.daoha.domain.trade.adapter.repository.ITradeRepository;
import top.daoha.domain.trade.model.aggregate.GroupBuyOrderAggregate;
import top.daoha.domain.trade.model.aggregate.GroupBuyTeamSettlementAggregate;
import top.daoha.domain.trade.model.entity.*;
import top.daoha.domain.trade.model.valobj.GroupBuyProgressVO;
import top.daoha.domain.trade.model.valobj.NotifyConfigVO;
import top.daoha.domain.trade.model.valobj.NotifyTypeEnumVO;
import top.daoha.domain.trade.model.valobj.TradeOrderStatusEnum;
import top.daoha.infrastructure.dao.IGroupBuyActivityDao;
import top.daoha.infrastructure.dao.IGroupBuyOrderDao;
import top.daoha.infrastructure.dao.IGroupBuyOrderListDao;
import top.daoha.infrastructure.dao.INotifyTaskDao;
import top.daoha.infrastructure.dao.po.GroupBuyActivity;
import top.daoha.infrastructure.dao.po.GroupBuyOrder;
import top.daoha.infrastructure.dao.po.GroupBuyOrderList;
import top.daoha.infrastructure.dao.po.NotifyTask;
import top.daoha.infrastructure.dcc.DCCService;
import top.daoha.infrastructure.redis.IRedisService;
import top.daoha.types.common.Constants;
import top.daoha.types.enums.ActivityStatusEnumVO;
import top.daoha.types.enums.GroupBuyOrderEnumVO;
import top.daoha.types.enums.ResponseCode;
import top.daoha.types.exception.AppException;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Repository
public class TradeRepository implements ITradeRepository {

    @Resource
    private IGroupBuyOrderDao groupBuyOrderDao;

    @Resource
    private IGroupBuyOrderListDao groupBuyOrderListDao;

    @Resource
    private IGroupBuyActivityDao groupBuyActivityDao;

    @Resource
    private INotifyTaskDao notifyTaskDao;

    @Resource
    private DCCService dccService;

    @Value("${spring.rabbitmq.config.producer.topic_team_success.routing_key}")
    private String topic_team_success;

    @Resource
    private IRedisService redisService;


    @Override
    public MarketPayOrderEntity queryNoPayMarketPayOrderByOutTradeNo(String userId, String outTradeNo) {


        GroupBuyOrderList req = new GroupBuyOrderList();
        req.setUserId(userId);
        req.setOutTradeNo(outTradeNo);
        GroupBuyOrderList res = groupBuyOrderListDao.queryGroupBuyOrderRecordByOutTradeNo(req);
        if (null == res) {
            return null;
        }

        MarketPayOrderEntity marketPayOrderEntity = new MarketPayOrderEntity();
        marketPayOrderEntity.setOrderId(res.getOrderId());
        marketPayOrderEntity.setTeamId(res.getTeamId());
        marketPayOrderEntity.setOriginalPrice(res.getOriginalPrice());
        marketPayOrderEntity.setDeductionPrice(res.getDeductionPrice());
        marketPayOrderEntity.setPayPrice(res.getPayPrice());
        marketPayOrderEntity.setStatus(TradeOrderStatusEnum.valueof(res.getStatus()));

        return marketPayOrderEntity;
    }

    @Override
    public GroupBuyProgressVO queryGroupBuyProgress(String teamId) {

        GroupBuyOrder groupBuyOrder = groupBuyOrderDao.queryGroupBuyProgress(teamId);
        if (null == groupBuyOrder) return null;

        GroupBuyProgressVO groupBuyProgressVO = new GroupBuyProgressVO();
        groupBuyProgressVO.setTargetCount(groupBuyOrder.getTargetCount());
        groupBuyProgressVO.setCompleteCount(groupBuyOrder.getCompleteCount());
        groupBuyProgressVO.setLockCount(groupBuyOrder.getLockCount());
        return groupBuyProgressVO;
    }

    @Transactional(timeout = 500)
    @Override
    public MarketPayOrderEntity lockMarketPayOrder(GroupBuyOrderAggregate groupBuyOrderAggregate) {
        PayActivityEntity payActivityEntity = groupBuyOrderAggregate.getPayActivityEntity();
        PayDiscountEntity payDiscountEntity = groupBuyOrderAggregate.getPayDiscountEntity();
        NotifyConfigVO notifyConfigVO = payDiscountEntity.getNotifyConfigVO();
        UserEntity userEntity = groupBuyOrderAggregate.getUserEntity();
        String userId = userEntity.getUserId();
        Integer orderCount = groupBuyOrderAggregate.getOrderCount();

        /// 获得拼团id
        String teamId = payActivityEntity.getTeamId();

        //1 先对group buy order表进行操作 新的拼团就创建一行，存在的拼团就lock+1
        if (StringUtils.isBlank(teamId)) {//就是说是这个人发起的拼团
            teamId = RandomStringUtils.randomNumeric(8);
            GroupBuyOrder groupBuyOrder = new GroupBuyOrder();
            groupBuyOrder.setTeamId(teamId);
            groupBuyOrder.setActivityId(payActivityEntity.getActivityId());
            groupBuyOrder.setSource(payDiscountEntity.getSource());
            groupBuyOrder.setChannel(payDiscountEntity.getChannel());
            groupBuyOrder.setOriginalPrice(payDiscountEntity.getOriginalPrice());
            groupBuyOrder.setDeductionPrice(payDiscountEntity.getDeductionPrice());
            groupBuyOrder.setPayPrice(payDiscountEntity.getPayPrice());
            groupBuyOrder.setTargetCount(payActivityEntity.getTargetCount());
            groupBuyOrder.setCompleteCount(0);
            groupBuyOrder.setLockCount(1);
            groupBuyOrder.setNotifyType(notifyConfigVO.getNotifyType().getCode());
            groupBuyOrder.setNotifyMQ(notifyConfigVO.getNotifyMQ());
            groupBuyOrder.setNotifyUrl(notifyConfigVO.getNotifyUrl());

            Date currentTime = new Date();
            Calendar calender = Calendar.getInstance();
            calender.setTime(currentTime);
            calender.add(Calendar.MINUTE, payActivityEntity.getValidTime());

            groupBuyOrder.setValidStartTime(currentTime);
            groupBuyOrder.setValidEndTime(calender.getTime());

            groupBuyOrderDao.insert(groupBuyOrder);
        } else {
            int updateAddLockCount = groupBuyOrderDao.updateAddLockCount(teamId);
            if (1 != updateAddLockCount) {//这里的意思就是没有被更新成功
                throw new AppException(ResponseCode.E0005);
            }
        }

        /// 接下来对groupbuyorderlist表进行操作
        String orderId = RandomStringUtils.randomNumeric(12);

        GroupBuyOrderList groupBuyOrderList = new GroupBuyOrderList();
        groupBuyOrderList.setOrderId(orderId);
        groupBuyOrderList.setUserId(userId);
        groupBuyOrderList.setTeamId(teamId);
        groupBuyOrderList.setActivityId(payActivityEntity.getActivityId());
        groupBuyOrderList.setStartTime(payActivityEntity.getStartTime());
        groupBuyOrderList.setEndTime(payActivityEntity.getEndTime());
        groupBuyOrderList.setGoodsId(payDiscountEntity.getGoodsId());
        groupBuyOrderList.setSource(payDiscountEntity.getSource());
        groupBuyOrderList.setChannel(payDiscountEntity.getChannel());
        groupBuyOrderList.setOriginalPrice(payDiscountEntity.getOriginalPrice());
        groupBuyOrderList.setDeductionPrice(payDiscountEntity.getDeductionPrice());
        groupBuyOrderList.setPayPrice(payDiscountEntity.getPayPrice());
        groupBuyOrderList.setStatus(TradeOrderStatusEnum.CREATE.getCode());
        groupBuyOrderList.setOutTradeNo(payDiscountEntity.getOutTradeNo());
        groupBuyOrderList.setBizId(payActivityEntity.getActivityId() + Constants.UNDERLINE + userEntity.getUserId() + Constants.UNDERLINE + (orderCount + 1));

        try {
            groupBuyOrderListDao.insert(groupBuyOrderList);
        } catch (DuplicateKeyException e) {
            throw new AppException(ResponseCode.INDEX_EXCEPTION);
        }


        MarketPayOrderEntity marketPayOrderEntity = new MarketPayOrderEntity();
        marketPayOrderEntity.setOrderId(orderId);
        marketPayOrderEntity.setTeamId(teamId);
        marketPayOrderEntity.setOriginalPrice(payDiscountEntity.getOriginalPrice());
        marketPayOrderEntity.setDeductionPrice(payDiscountEntity.getDeductionPrice());
        marketPayOrderEntity.setPayPrice(payDiscountEntity.getPayPrice());
        marketPayOrderEntity.setStatus(TradeOrderStatusEnum.CREATE);
        return marketPayOrderEntity;
    }

    @Override
    public GroupBuyActivityEntity queryGroupBuyActivityByActivityId(Long activityId) {
        GroupBuyActivity groupBuyActivity = groupBuyActivityDao.queryGroupBuyActivityByActivityId(activityId);

        return GroupBuyActivityEntity.builder()
                .activityId(activityId)
                .activityName(groupBuyActivity.getActivityName())
                .discountId(groupBuyActivity.getDiscountId())
                .groupType(groupBuyActivity.getGroupType())
                .takeLimitCount(groupBuyActivity.getTakeLimitCount())
                .target(groupBuyActivity.getTarget())
                .validTime(groupBuyActivity.getValidTime())
                .status(ActivityStatusEnumVO.valueOf(groupBuyActivity.getStatus()))
                .startTime(groupBuyActivity.getStartTime())
                .endTime(groupBuyActivity.getEndTime())
                .tagId(groupBuyActivity.getTagId())
                .tagScope(groupBuyActivity.getTagScope())
                .build();
    }

    @Override
    public Integer queryOrderCountByActivityId(Long activityId, String userId) {
        GroupBuyOrderList groupBuyOrderList = new GroupBuyOrderList();
        groupBuyOrderList.setActivityId(activityId);
        groupBuyOrderList.setUserId(userId);
        return groupBuyOrderListDao.queryOrderCountByActivityId(groupBuyOrderList);
    }

    @Override
    public GroupBuyTeamEntity queryGroupBuyTeamByTeamId(String teamId) {
        GroupBuyOrder groupBuyOrder = groupBuyOrderDao.queryGroupBuyTeamByTeamId(teamId);

        return GroupBuyTeamEntity.builder()
                .teamId(teamId)
                .activityId(groupBuyOrder.getActivityId())
                .targetCount(groupBuyOrder.getTargetCount())
                .completeCount(groupBuyOrder.getCompleteCount())
                .lockCount(groupBuyOrder.getLockCount())
                .status(GroupBuyOrderEnumVO.valueOf(groupBuyOrder.getStatus()))
                .validStartTime(groupBuyOrder.getValidStartTime())
                .validEndTime(groupBuyOrder.getValidEndTime())
                .notifyConfigVO(NotifyConfigVO.builder()
                        .notifyType(NotifyTypeEnumVO.valueOf(groupBuyOrder.getNotifyType()))
                        .notifyUrl(groupBuyOrder.getNotifyUrl())
                        .notifyMQ(topic_team_success)
                        .build())
                .build();
    }

    @Transactional(timeout = 500)
    @Override
    public NotifyTaskEntity settlementMarketPayOrder(GroupBuyTeamSettlementAggregate groupBuyTeamSettlementAggregate) {
        UserEntity userEntity = groupBuyTeamSettlementAggregate.getUserEntity();
        GroupBuyTeamEntity groupBuyTeamEntity = groupBuyTeamSettlementAggregate.getGroupBuyTeamEntity();
        NotifyConfigVO notifyConfigVO = groupBuyTeamEntity.getNotifyConfigVO();
        TradePaySuccessEntity tradePaySuccessEntity = groupBuyTeamSettlementAggregate.getTradePaySuccessEntity();


        GroupBuyOrderList groupBuyOrderListReq = GroupBuyOrderList.builder()
                .userId(userEntity.getUserId())
                .activityId(groupBuyTeamEntity.getActivityId())
                .teamId(groupBuyTeamEntity.getTeamId())
                .outTradeNo(tradePaySuccessEntity.getOutTradeNo())
                .source(tradePaySuccessEntity.getSource())
                .channel(tradePaySuccessEntity.getChannel())
                .outTradeTime(tradePaySuccessEntity.getOutTradeTime())
                .build();

        //这里是将orderList表的订单的状态改变完成
        int rowsCount = groupBuyOrderListDao.updateStatus2COMPLETE(groupBuyOrderListReq);
        if (1 != rowsCount) {
            throw new AppException(ResponseCode.UPDATE_ZERO);
        }
        //这个是将group_buy_order表中的就是拼团表中的完成数量+1
        int updaCount = groupBuyOrderDao.updateAddCompleteCount(groupBuyTeamEntity.getTeamId());
        if (1 != updaCount) {
            throw new AppException(ResponseCode.UPDATE_ZERO);
        }
        //如果拼团表中的完成数量满了那么整个状态就完成了
        if (groupBuyTeamEntity.getTargetCount() - groupBuyTeamEntity.getCompleteCount() == 1) {
            updaCount = groupBuyOrderDao.updateOrderStatus2COMPLETE(groupBuyTeamEntity.getTeamId());
            if (1 != updaCount) {
                throw new AppException(ResponseCode.UPDATE_ZERO);
            }
            //这个拼团中所有的用户都支付完成了
            List<String> outTradeNolist = groupBuyOrderListDao.queryGroupBuyCompleterOrderOutTradeNoListByTeamId(groupBuyTeamEntity.getTeamId());
            //现在将所有完成的信息写进回调表中
            NotifyTask notifyTask = new NotifyTask();
            notifyTask.setActivityId(groupBuyTeamEntity.getActivityId());
            notifyTask.setTeamId(groupBuyTeamEntity.getTeamId());
            notifyTask.setNotifyType(notifyConfigVO.getNotifyType().getCode());
            notifyTask.setNotifyMQ(NotifyTypeEnumVO.MQ.equals(notifyConfigVO.getNotifyType()) ? notifyConfigVO.getNotifyMQ() : null);
            notifyTask.setNotifyUrl(NotifyTypeEnumVO.HTTP.equals(notifyConfigVO.getNotifyType()) ? notifyConfigVO.getNotifyUrl() : null);
            notifyTask.setNotifyCount(0);
            notifyTask.setNotifyStatus(0);

            notifyTask.setParameterJson(JSON.toJSONString(new HashMap<String, Object>() {{
                put("teamId", groupBuyTeamEntity.getTeamId());
                put("outTradeNoList", outTradeNolist);
            }}));
            notifyTaskDao.insert(notifyTask);

            return NotifyTaskEntity.builder()
                    .teamId(notifyTask.getTeamId())
                    .notifyType(notifyTask.getNotifyType())
                    .notifyUrl(notifyTask.getNotifyUrl())
                    .notifyMQ(notifyTask.getNotifyMQ())
                    .notifyUrlCount(notifyTask.getNotifyCount())
                    .parameterJson(notifyTask.getParameterJson())
                    .build();
        }
        return null;
    }

    @Override
    public boolean isSCBlackIntercept(String source, String channel) {
        return dccService.isScBlackList(source, channel);
    }

    @Override
    public List<NotifyTaskEntity> queryUnExecutedNotifyTaskList() {
        List<NotifyTask> notifyTasks = notifyTaskDao.queryUnExecutedNotifyTaskList();
        if (notifyTasks == null || notifyTasks.size() == 0) {
            return new ArrayList<>();
        }
        List<NotifyTaskEntity> notifyTaskEntities = new ArrayList<>();

        for (NotifyTask notifyTask : notifyTasks) {
            NotifyTaskEntity notifyTaskEntity = NotifyTaskEntity.builder()
                    .teamId(notifyTask.getTeamId())
                    .notifyUrlCount(notifyTask.getNotifyCount())
                    .parameterJson(notifyTask.getParameterJson())
                    .notifyUrl(notifyTask.getNotifyUrl())
                    .build();
            notifyTaskEntities.add(notifyTaskEntity);
        }

        return notifyTaskEntities;
    }

    @Override
    public List<NotifyTaskEntity> queryUnExecutedNotifyTaskList(String teamId) {
        NotifyTask notifyTask = notifyTaskDao.queryUnExecutedNotifyTaskByTeamId(teamId);
        if (notifyTask == null) {
            return new ArrayList<>();
        }
        return Collections.singletonList(NotifyTaskEntity.builder()
                .teamId(notifyTask.getTeamId())
                .notifyUrlCount(notifyTask.getNotifyCount())
                .parameterJson(notifyTask.getParameterJson())
                .notifyUrl(notifyTask.getNotifyUrl())
                .build());
    }

    @Override
    public int updateNotifyTaskStatusSuccess(String teamId) {
        return notifyTaskDao.updateNotifyTaskStatusSuccess(teamId);
    }

    @Override
    public int updateNotifyTaskStatusRetry(String teamId) {
        return notifyTaskDao.updateNotifyTaskStatusRetry(teamId);
    }

    @Override
    public int updateNotifyTaskStatusError(String teamId) {
        return notifyTaskDao.updateNotifyTaskStatusError(teamId);
    }

    @Override
    public boolean occupyTeamStock(String teamStockKey, String recoveryTeamStockKey, Integer target, Integer validTime) {
        Long recoveryCount = redisService.getAtomicLong(recoveryTeamStockKey);
        recoveryCount = recoveryCount == null ? 0 : recoveryCount;

        //索引是从0开始的所以这块+1
        long occupy = redisService.incr(teamStockKey)+1;
        if(occupy > target+recoveryCount){
            redisService.setAtomicLong(teamStockKey, target);
            return false;
        }

        String lockKey = teamStockKey+Constants.UNDERLINE+occupy;
        //这里是分布式锁的关键，虽然incr是原子的，但是还是有可能出现同一个值的情况，所以这里是一个兜底的分布式锁操作
        Boolean lock = redisService.setNx(lockKey,validTime+60, TimeUnit.MINUTES);

        if(!lock){
            log.info("组队库存加锁失败{}", lockKey);
        }

        return lock;
    }

    @Override
    public void recoveryTeamStock(String recoveryTeamStockKey, Integer validTime) {
        if(StringUtils.isBlank(recoveryTeamStockKey)){
            return;
        }
        redisService.incr(recoveryTeamStockKey);
    }

}
