package top.daoha.trigger.job;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import top.daoha.domain.trade.service.ITradeSettlementOrderService;

import javax.annotation.Resource;
import java.util.Map;

@Slf4j
@Service
public class GroupBuyNotifyJob {
    @Resource
    private ITradeSettlementOrderService tradeSettlementOrderService;


    @Scheduled(cron = "0/45 * * * * ?")
    public void exec(){
        try{
            Map<String, Integer> stringIntegerMap = tradeSettlementOrderService.execSettlementNotifyJob();
            log.info("定时任务，回调通知拼团完结任务 result:{}", JSON.toJSONString(stringIntegerMap));
        }catch (Exception e){
            log.info("定时任务，回调通知拼团完结任务失败");
        }
    }
}
