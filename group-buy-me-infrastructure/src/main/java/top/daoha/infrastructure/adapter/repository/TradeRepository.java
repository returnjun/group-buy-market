package top.daoha.infrastructure.adapter.repository;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.daoha.domain.trade.adapter.repository.ITradeRepository;
import top.daoha.domain.trade.model.aggregate.GroupBuyOrderAggregate;
import top.daoha.domain.trade.model.entity.MarketPayOrderEntity;
import top.daoha.domain.trade.model.entity.PayActivityEntity;
import top.daoha.domain.trade.model.entity.PayDiscountEntity;
import top.daoha.domain.trade.model.entity.UserEntity;
import top.daoha.domain.trade.model.valobj.GroupBuyProgressVO;
import top.daoha.domain.trade.model.valobj.TradeOrderStatusEnum;
import top.daoha.infrastructure.dao.IGroupBuyOrderDao;
import top.daoha.infrastructure.dao.IGroupBuyOrderListDao;
import top.daoha.infrastructure.dao.po.GroupBuyOrder;
import top.daoha.infrastructure.dao.po.GroupBuyOrderList;
import top.daoha.types.enums.ResponseCode;
import top.daoha.types.exception.AppException;

import javax.annotation.Resource;


@Repository
public class TradeRepository implements ITradeRepository {

    @Resource
    private IGroupBuyOrderDao groupBuyOrderDao;

    @Resource
    private IGroupBuyOrderListDao groupBuyOrderListDao;


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
        marketPayOrderEntity.setDeductionPrice(res.getDeductionPrice());
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
        UserEntity userEntity = groupBuyOrderAggregate.getUserEntity();
        String userId = userEntity.getUserId();

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
            groupBuyOrder.setPayPrice(payDiscountEntity.getOriginalPrice().subtract(payDiscountEntity.getDeductionPrice()));
            groupBuyOrder.setTargetCount(payActivityEntity.getTargetCount());
            groupBuyOrder.setCompleteCount(0);
            groupBuyOrder.setLockCount(1);

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
        groupBuyOrderList.setStatus(TradeOrderStatusEnum.CREATE.getCode());
        groupBuyOrderList.setOutTradeNo(payDiscountEntity.getOutTradeNo());

        try{
            groupBuyOrderListDao.insert(groupBuyOrderList);
        }catch (DuplicateKeyException e){
            throw new AppException(ResponseCode.INDEX_EXCEPTION);
        }


        MarketPayOrderEntity marketPayOrderEntity = new MarketPayOrderEntity();
        marketPayOrderEntity.setOrderId(orderId);
        marketPayOrderEntity.setDeductionPrice(payDiscountEntity.getDeductionPrice());
        marketPayOrderEntity.setStatus(TradeOrderStatusEnum.CREATE);
        return marketPayOrderEntity;
    }
}
