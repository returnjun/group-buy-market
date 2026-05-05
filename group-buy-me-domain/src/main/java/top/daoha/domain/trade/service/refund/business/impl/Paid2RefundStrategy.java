package top.daoha.domain.trade.service.refund.business.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.daoha.domain.trade.adapter.post.ITradePort;
import top.daoha.domain.trade.adapter.repository.ITradeRepository;
import top.daoha.domain.trade.model.aggregate.GroupBuyRefundAggregate;
import top.daoha.domain.trade.model.entity.NotifyTaskEntity;
import top.daoha.domain.trade.model.entity.TradeRefundOrderEntity;
import top.daoha.domain.trade.model.valobj.TeamRefundSuccess;
import top.daoha.domain.trade.service.ITradeTaskService;
import top.daoha.domain.trade.service.lock.factory.TradeLockRuleFilterFactory;
import top.daoha.domain.trade.service.refund.business.IRefundOrderStrategy;
import top.daoha.types.exception.AppException;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service("paid2RefundStrategy")
public class Paid2RefundStrategy implements IRefundOrderStrategy {

    @Resource
    private ITradeRepository tradeRepository;

    @Resource
    private ITradeTaskService tradeTaskService;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    public void refundOrder(TradeRefundOrderEntity tradeRefundOrderEntity) {
        log.info("退单，已支付，未成团。订单信息:{}",tradeRefundOrderEntity);

        //先更更新订单状态
        GroupBuyRefundAggregate groupBuyRefundAggregate =
                GroupBuyRefundAggregate.buildPaid2RefundAggregate(tradeRefundOrderEntity,-1,-1);
        NotifyTaskEntity notifyTaskEntity = tradeRepository.paid2Refund(groupBuyRefundAggregate);

        //然后发送MQ消息
        if(null!=notifyTaskEntity){
            threadPoolExecutor.execute(()->{
                Map<String, Integer> notifyResult = null;
                try {
                    notifyResult = tradeTaskService.execNotifyJob(notifyTaskEntity);
                    log.error("拼团交易-退单成功 result:{}",notifyResult);
                } catch (Exception e) {
                    log.error("拼团交易-退单任务失败 result:{},报错信息：{}",notifyResult,e);
                    throw new AppException(e.getMessage());
                }

            });
        }
    }

    @Override
    public void reverseStock(TeamRefundSuccess teamRefundSuccess) {
        log.info("退单恢复锁单量--未支付未成团的情况，但是有锁单记录:{} :{}",teamRefundSuccess.getUserId(),teamRefundSuccess);
        //1 恢复库存key
        String recoveryTeamStockKey = TradeLockRuleFilterFactory.generateRecoveryTeamStockKey(teamRefundSuccess.getActivityId(), teamRefundSuccess.getTeamId());
        //2 退单恢复：未支付未成团，但是又锁单记录，恢复锁单库存
        tradeRepository.refund2AddRecovery(recoveryTeamStockKey,teamRefundSuccess.getOrderId());
    }
}
