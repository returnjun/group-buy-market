package top.daoha.domain.trade.service.refund.business;

import lombok.extern.slf4j.Slf4j;
import top.daoha.domain.trade.adapter.repository.ITradeRepository;
import top.daoha.domain.trade.model.entity.NotifyTaskEntity;
import top.daoha.domain.trade.model.valobj.TeamRefundSuccess;
import top.daoha.domain.trade.service.ITradeTaskService;
import top.daoha.domain.trade.service.lock.factory.TradeLockRuleFilterFactory;
import top.daoha.types.exception.AppException;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public abstract class AbstractRefundOrderStrategy implements IRefundOrderStrategy{

    @Resource
    protected ITradeRepository repository;

    @Resource
    protected ITradeTaskService tradeTaskService;

    @Resource
    protected ThreadPoolExecutor threadPoolExecutor;

    protected void sendRefundNotifyMessage(NotifyTaskEntity notifyTaskEntity, String refundType) {
        //然后发送MQ消息
        if(null!=notifyTaskEntity){
            threadPoolExecutor.execute(()->{
                Map<String, Integer> notifyResult = null;
                try {
                    notifyResult = tradeTaskService.execNotifyJob(notifyTaskEntity);
                    log.error("拼团交易 - 退单类型:{} - 退单成功 result:{}",refundType,notifyResult);
                } catch (Exception e) {
                    log.error("拼团交易 - 退单类型:{} - 退单任务失败 result:{},报错信息：{}",refundType,notifyResult,e);
                    throw new AppException(e.getMessage());
                }
            });
        }
    }

    protected  void doReverseStock(TeamRefundSuccess teamRefundSuccess,String refundType){
        log.info("退单恢复锁单量--:{}的情况，但是有锁单记录 :{}",refundType,teamRefundSuccess);
        //1 恢复库存key
        String recoveryTeamStockKey = TradeLockRuleFilterFactory.generateRecoveryTeamStockKey(teamRefundSuccess.getActivityId(), teamRefundSuccess.getTeamId());
        //2 退单恢复：未支付未成团，但是又锁单记录，恢复锁单库存
        repository.refund2AddRecovery(recoveryTeamStockKey,teamRefundSuccess.getOrderId());
    }

}
