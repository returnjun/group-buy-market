package top.daoha.domain.trade.service.refund.business.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.daoha.domain.trade.adapter.repository.ITradeRepository;
import top.daoha.domain.trade.model.aggregate.GroupBuyRefundAggregate;
import top.daoha.domain.trade.model.entity.NotifyTaskEntity;
import top.daoha.domain.trade.model.entity.TradeRefundOrderEntity;
import top.daoha.domain.trade.service.ITradeTaskService;
import top.daoha.domain.trade.service.refund.business.IRefundOrderStrategy;
import top.daoha.types.exception.AppException;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service("unpaid2RefundStrategy")
public class Unpaid2RefundStrategy implements IRefundOrderStrategy {

    @Resource
    private ITradeRepository tradeRepository;

    @Resource
    private ITradeTaskService tradeTaskService;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    public void refundOrder(TradeRefundOrderEntity tradeRefundOrderEntity) {
        log.info("退单，未支付，未成团。订单信息:{}",tradeRefundOrderEntity);
        GroupBuyRefundAggregate groupBuyRefundAggregate =
                GroupBuyRefundAggregate.buildUnpaid2RefundAggregate(tradeRefundOrderEntity,-1);

        NotifyTaskEntity notifyTaskEntity = tradeRepository.unpaid2Refund(groupBuyRefundAggregate);

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
}
