package top.daoha.domain.trade.service.settlement;


import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import top.daoha.domain.trade.adapter.post.ITradePort;
import top.daoha.domain.trade.adapter.repository.ITradeRepository;
import top.daoha.domain.trade.model.aggregate.GroupBuyTeamSettlementAggregate;
import top.daoha.domain.trade.model.entity.*;
import top.daoha.domain.trade.service.ITradeSettlementOrderService;
import top.daoha.domain.trade.service.settlement.factory.TradeSettlementRuleFilterFactory;
import top.daoha.types.desgin.framework.link.model2.chain.BusinessLinkedList;
import top.daoha.types.enums.NotifyTaskHTTPEnumVO;
import top.daoha.types.exception.AppException;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@Service
@Slf4j
public class TradeSettlementOrderService implements ITradeSettlementOrderService {

    @Resource
    private ITradeRepository tradeRepository;
    
    @Resource
    private ITradePort tradePort;

    @Resource
    private ThreadPoolExecutor threadPoolTaskExecutor;
    

    @Resource(name = "tradeSettlementRuleFilter1")
    BusinessLinkedList<TradeSettlementRuleCommandEntity, TradeSettlementRuleFilterFactory.DynamicContext, TradeSettlementRuleFilterBackEntity>  tradeSettlementRuleFilterBackEntity;

    @Override
    public TradePaySettlementEntity settlementOrder(TradePaySuccessEntity tradePaySuccessEntity) throws Exception {

        // 1 查询拼团信息
        TradeSettlementRuleCommandEntity build1 = TradeSettlementRuleCommandEntity.builder()
                .source(tradePaySuccessEntity.getSource())
                .channel(tradePaySuccessEntity.getChannel())
                .outTradeTime(tradePaySuccessEntity.getOutTradeTime())
                .outTradeNo(tradePaySuccessEntity.getOutTradeNo())
                .userId(tradePaySuccessEntity.getUserId())
                .build();

        TradeSettlementRuleFilterBackEntity tradeEntity = tradeSettlementRuleFilterBackEntity.apply(build1,new TradeSettlementRuleFilterFactory.DynamicContext());

        String teamId = tradeEntity.getTeamId();
        // 2 查询拼团信息
        GroupBuyTeamEntity groupBuyTeamEntity = GroupBuyTeamEntity.builder()
                .teamId(teamId)
                .activityId(tradeEntity.getActivityId())
                .targetCount(tradeEntity.getTargetCount())
                .status(tradeEntity.getStatus())
                .lockCount(tradeEntity.getLockCount())
                .completeCount(tradeEntity.getCompleteCount())
                .validStartTime(tradeEntity.getValidStartTime())
                .validEndTime(tradeEntity.getValidEndTime())
                .notifyConfigVO(tradeEntity.getNotifyConfigVO())
                .build();

        log.info("查看根据teamId查到的订单信息:{}",JSON.toJSONString(groupBuyTeamEntity));
        GroupBuyTeamSettlementAggregate build = GroupBuyTeamSettlementAggregate.builder()
                .userEntity(new UserEntity(tradePaySuccessEntity.getUserId()))
                .groupBuyTeamEntity(groupBuyTeamEntity)
                .tradePaySuccessEntity(tradePaySuccessEntity)
                .build();

        //结算处理
        NotifyTaskEntity notifyTaskEntity=tradeRepository.settlementMarketPayOrder(build);


        //主动组队回调处理，处理失败也会有定时任务补偿，这样让回调任务压力减小
        if(null != notifyTaskEntity) {

            threadPoolTaskExecutor.execute(()->{
                    Map<String, Integer> notifyResultMap = null;
                    try {
                        notifyResultMap = execSettlementNotifyJob(notifyTaskEntity);
                    } catch (Exception e) {
                        throw new AppException(e.getMessage());
                    }
                    log.info("回调通知拼团完结 result:{}", JSON.toJSONString(notifyResultMap));
            });

        }


        return TradePaySettlementEntity.builder()
                .source(tradePaySuccessEntity.getSource())
                .channel(tradePaySuccessEntity.getChannel())
                .userId(tradePaySuccessEntity.getUserId())
                .teamId(tradeEntity.getTeamId())
                .activityId(groupBuyTeamEntity.getActivityId())
                .outTradeNo(tradePaySuccessEntity.getOutTradeNo())
                .build();
    }

    @Override
    public Map<String, Integer> execSettlementNotifyJob() throws Exception {
        log.info("拼团交易-定时任务执行结算通知任务");
        List<NotifyTaskEntity> notifyTaskEntityList = tradeRepository.queryUnExecutedNotifyTaskList();

        return execSettlementNotifyJob(notifyTaskEntityList);
    }

    @Override
    public Map<String, Integer> execSettlementNotifyJob(String teamId) throws Exception {
        log.info("拼团交易-主动通过teamId执行结算通知任务");
        List<NotifyTaskEntity> notifyTaskEntityList = tradeRepository.queryUnExecutedNotifyTaskList(teamId);

        return execSettlementNotifyJob(notifyTaskEntityList);
    }

    @Override
    public Map<String, Integer> execSettlementNotifyJob(NotifyTaskEntity notifyTaskEntity) throws Exception {
        log.info("拼团交易-主动通过teamId:{} notifyTaskEntity:{}",notifyTaskEntity.getTeamId(),notifyTaskEntity.toString());
        return execSettlementNotifyJob(Collections.singletonList(notifyTaskEntity));
    }

    private Map<String, Integer> execSettlementNotifyJob(List<NotifyTaskEntity> notifyTaskEntityList) throws Exception {
        int successCount = 0,errorcount= 0,retryCount=0;
        for (NotifyTaskEntity notifyTaskEntity : notifyTaskEntityList) {

            String response = tradePort.groupBuyNotify(notifyTaskEntity);

            if(NotifyTaskHTTPEnumVO.SUCCESS.getCode().equals(response)){
                int update = tradeRepository.updateNotifyTaskStatusSuccess(notifyTaskEntity.getTeamId());
                if(1==update){
                    successCount+=1;
                }
            }else if(NotifyTaskHTTPEnumVO.ERROR.getCode().equals(response)){
                if(notifyTaskEntity.getNotifyUrlCount()< 5){
                    int update = tradeRepository.updateNotifyTaskStatusRetry(notifyTaskEntity.getTeamId());
                    if(1==update){
                        retryCount=1;
                    }
                }else {
                    int update = tradeRepository.updateNotifyTaskStatusError(notifyTaskEntity.getTeamId());
                    if(1==update){
                        errorcount+=1;
                    }
                }
            }
        }
        Map<String,Integer> resultMap = new HashMap<>();
        resultMap.put("waitCount", notifyTaskEntityList.size());
        resultMap.put("successCount",successCount);
        resultMap.put("errorcount",errorcount);
        resultMap.put("retrycount",retryCount);
        return resultMap;
    }

}
