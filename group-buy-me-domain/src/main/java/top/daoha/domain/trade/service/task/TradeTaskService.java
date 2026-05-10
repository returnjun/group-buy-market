package top.daoha.domain.trade.service.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.daoha.domain.trade.adapter.post.ITradePort;
import top.daoha.domain.trade.adapter.repository.ITradeRepository;
import top.daoha.domain.trade.model.entity.NotifyTaskEntity;
import top.daoha.domain.trade.service.ITradeTaskService;
import top.daoha.types.enums.NotifyTaskHTTPEnumVO;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@Service
@Slf4j
public class TradeTaskService implements ITradeTaskService {

    @Resource
    private ITradeRepository tradeRepository;

    @Resource
    private ITradePort tradePort;

    @Resource
    private ThreadPoolExecutor threadPoolTaskExecutor;


    @Override
    public Map<String, Integer> execNotifyJob() throws Exception {
        log.info("拼团交易-定时任务执行结算通知任务");
        List<NotifyTaskEntity> notifyTaskEntityList = tradeRepository.queryUnExecutedNotifyTaskList();

        return execNotifyJob(notifyTaskEntityList);
    }

    @Override
    public Map<String, Integer> execNotifyJob(String teamId) throws Exception {
        log.info("拼团交易-主动通过teamId执行结算通知任务");
        List<NotifyTaskEntity> notifyTaskEntityList = tradeRepository.queryUnExecutedNotifyTaskList(teamId);

        return execNotifyJob(notifyTaskEntityList);
    }

    @Override
    public Map<String, Integer> execNotifyJob(NotifyTaskEntity notifyTaskEntity) throws Exception {
        log.info("拼团交易-主动通过teamId:{} notifyTaskEntity:{}",notifyTaskEntity.getTeamId(),notifyTaskEntity.toString());
        return execNotifyJob(Collections.singletonList(notifyTaskEntity));
    }

    private Map<String, Integer> execNotifyJob(List<NotifyTaskEntity> notifyTaskEntityList) throws Exception {
        int successCount = 0,errorcount= 0,retryCount=0;
        for (NotifyTaskEntity notifyTaskEntity : notifyTaskEntityList) {

            String response = tradePort.groupBuyNotify(notifyTaskEntity);

            if(NotifyTaskHTTPEnumVO.SUCCESS.getCode().equals(response)){
                int update = tradeRepository.updateNotifyTaskStatusSuccess(notifyTaskEntity);
                if(1==update){
                    successCount+=1;
                }
            }else if(NotifyTaskHTTPEnumVO.ERROR.getCode().equals(response)){
                if(notifyTaskEntity.getNotifyCount()< 5){
                    int update = tradeRepository.updateNotifyTaskStatusRetry(notifyTaskEntity);
                    if(1==update){
                        retryCount+=1;
                    }
                }else {
                    int update = tradeRepository.updateNotifyTaskStatusError(notifyTaskEntity);
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
