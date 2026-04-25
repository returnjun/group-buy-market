package top.daoha.infrastructure.adapter.port;

import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.stereotype.Service;
import top.daoha.domain.trade.adapter.post.ITradePort;
import top.daoha.domain.trade.model.entity.NotifyTaskEntity;
import top.daoha.domain.trade.model.valobj.NotifyTypeEnumVO;
import top.daoha.infrastructure.event.EventPublisher;
import top.daoha.infrastructure.gateway.GroupBuyNotifyService;
import top.daoha.infrastructure.redis.IRedisService;
import top.daoha.types.enums.NotifyTaskHTTPEnumVO;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;


@Service
public class TradePort implements ITradePort {

    @Resource
    private GroupBuyNotifyService groupBuyNotifyService;
    @Resource
    private IRedisService redisService;
    @Resource
    private EventPublisher publisher;

    @Override
    public String groupBuyNotify(NotifyTaskEntity notifyTaskEntity) throws Exception {
        RLock lock = redisService.getLock(notifyTaskEntity.lockKey());
        try{
            if(lock.tryLock(3,0, TimeUnit.SECONDS)){
                try {
                    //回调方式是HTTP
                    if(NotifyTypeEnumVO.HTTP.getCode().equals(notifyTaskEntity.getNotifyType())){
                        if(StringUtils.isBlank(notifyTaskEntity.getNotifyUrl())||"暂无".equals(notifyTaskEntity.getNotifyUrl())){
                            return NotifyTaskHTTPEnumVO.SUCCESS.getCode();
                        }
                        return groupBuyNotifyService.groupBuyNotify(notifyTaskEntity.getNotifyUrl(),notifyTaskEntity.getParameterJson());
                    }
                    //回调方式是MQ
                    if(NotifyTypeEnumVO.MQ.getCode().equals(notifyTaskEntity.getNotifyType())){
                        publisher.publish(notifyTaskEntity.getNotifyMQ(),notifyTaskEntity.getParameterJson());
                        return NotifyTaskHTTPEnumVO.SUCCESS.getCode();
                    }

                }finally {
                    if(lock.isLocked()&&lock.isHeldByCurrentThread()){}
                    lock.unlock();
                }
            }
            return NotifyTaskHTTPEnumVO.NULL.getCode();
        }catch (Exception e){
            Thread.currentThread().interrupt();
            return NotifyTaskHTTPEnumVO.NULL.getCode();
        }
    }
}
