package top.daoha.domain.trade.service.settlement;


import cn.bugstack.wrench.design.framework.link.model2.chain.BusinessLinkedList;
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

import top.daoha.domain.trade.service.task.TradeTaskService;
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

    @Resource
    private TradeTaskService tradeTaskService;
    

    @Resource(name = "tradeSettlementRuleFilter1")
    BusinessLinkedList<TradeSettlementRuleCommandEntity, TradeSettlementRuleFilterFactory.DynamicContext, TradeSettlementRuleFilterBackEntity> tradeSettlementRuleFilterBackEntity;

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
                        notifyResultMap = tradeTaskService.execNotifyJob(notifyTaskEntity);
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



}
