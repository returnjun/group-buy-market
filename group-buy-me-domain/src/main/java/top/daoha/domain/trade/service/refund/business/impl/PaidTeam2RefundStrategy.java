package top.daoha.domain.trade.service.refund.business.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.daoha.domain.trade.adapter.post.ITradePort;
import top.daoha.domain.trade.adapter.repository.ITradeRepository;
import top.daoha.domain.trade.model.aggregate.GroupBuyRefundAggregate;
import top.daoha.domain.trade.model.entity.GroupBuyTeamEntity;
import top.daoha.domain.trade.model.entity.NotifyTaskEntity;
import top.daoha.domain.trade.model.entity.TradeRefundOrderEntity;
import top.daoha.domain.trade.model.valobj.GroupBuyProgressVO;
import top.daoha.domain.trade.model.valobj.TeamRefundSuccess;
import top.daoha.domain.trade.service.ITradeTaskService;
import top.daoha.domain.trade.service.lock.factory.TradeLockRuleFilterFactory;
import top.daoha.domain.trade.service.refund.business.IRefundOrderStrategy;
import top.daoha.types.enums.GroupBuyOrderEnumVO;
import top.daoha.types.exception.AppException;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service("paidTeam2RefundStrategy")
public class PaidTeam2RefundStrategy implements IRefundOrderStrategy {

    @Resource
    private ITradeRepository tradeRepository;

    @Resource
    private ITradeTaskService tradeTaskService;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    public void refundOrder(TradeRefundOrderEntity tradeRefundOrderEntity) {
        log.info("退单，已支付，已成团。订单信息:{}",tradeRefundOrderEntity);

        GroupBuyTeamEntity groupinfo = tradeRepository.queryGroupBuyTeamByTeamId(tradeRefundOrderEntity.getTeamId());
        if(null == groupinfo||groupinfo.getTeamId().isEmpty()){
            log.info("退单，已支付，已成团。查询不到拼团信息，订单信息:{}",tradeRefundOrderEntity);
        }
        Integer completeCount = groupinfo.getCompleteCount();

        GroupBuyOrderEnumVO status = completeCount==1?GroupBuyOrderEnumVO.FAIL:GroupBuyOrderEnumVO.COMPLETE_FAIL;


        //先更更新订单状态
        GroupBuyRefundAggregate groupBuyRefundAggregate =
                GroupBuyRefundAggregate.buildPaidTeam2RefundAggregate(tradeRefundOrderEntity,-1,-1,status);


        NotifyTaskEntity notifyTaskEntity = tradeRepository.paidTeam2Refund(groupBuyRefundAggregate);

        //然后发送MQ消息
        if(null!=notifyTaskEntity){
            threadPoolExecutor.execute(()->{
                Map<String, Integer> notifyResult = null;
                try {
                    notifyResult = tradeTaskService.execNotifyJob(notifyTaskEntity);
                    log.error("已支付已拼团-退单成功 result:{}",notifyResult);
                } catch (Exception e) {
                    log.error("已支付已拼团-退单失败 result:{},报错信息：{}",notifyResult,e);
                    throw new AppException(e.getMessage());
                }
            });
        }
    }

    @Override
    public void reverseStock(TeamRefundSuccess teamRefundSuccess) {
        log.info("退单恢复锁单量--已支付已成团的情况，不用改变锁单的回恢复量:{} :{}",teamRefundSuccess.getUserId(),teamRefundSuccess);
    }
}
