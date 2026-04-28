package top.daoha.trigger.job;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import top.daoha.domain.trade.service.ITradeSettlementOrderService;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class GroupBuyNotifyJob {
    @Resource
    private ITradeSettlementOrderService tradeSettlementOrderService;

    @Resource
    private RedissonClient redissonClient;


    @Scheduled(cron = "0/45 * * * * ?")
    public void exec(){
        //加锁，分布式应用，任务调度可能N个机子同时执行，这里增加抢占机制，谁抢到谁执行
        RLock lock = redissonClient.getLock("group_buy_market_notify_job_exec");
        try{
            boolean isLocked = lock.tryLock(3,0, TimeUnit.SECONDS);
            if(!isLocked) return;

            Map<String, Integer> stringIntegerMap = tradeSettlementOrderService.execSettlementNotifyJob();
            log.info("定时任务，回调通知拼团完结任务 result:{}", JSON.toJSONString(stringIntegerMap));
        }catch (Exception e){
            log.info("定时任务，回调通知拼团完结任务失败");
        }finally {
            if(lock.isLocked()&&lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }
}
