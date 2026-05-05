package top.daoha.trigger.listener;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import top.daoha.domain.trade.model.valobj.TeamRefundSuccess;
import top.daoha.domain.trade.service.refund.TradeRefundOrderService;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 结算完成消息监听
 * @create 2025-03-08 13:49
 */
@Slf4j
@Component
public class RefundSuccessTopicListener {

    private final TradeRefundOrderService tradeRefundOrderService;

    public RefundSuccessTopicListener(TradeRefundOrderService tradeRefundOrderService) {
        this.tradeRefundOrderService = tradeRefundOrderService;
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = "${spring.rabbitmq.config.producer.topic_team_refund.queue}"),
                    exchange = @Exchange(value = "${spring.rabbitmq.config.producer.exchange}", type = ExchangeTypes.TOPIC),
                    key = "${spring.rabbitmq.config.producer.topic_team_refund.routing_key}"
            )
    )
    public void listener(String message) {
        log.info("退单成功（接收消息）- 现在尝试恢复拼团队伍锁单量:{}", message);
        TeamRefundSuccess teamRefundSuccess = JSON.parseObject(message, TeamRefundSuccess.class);
        try{
            tradeRefundOrderService.restoreTeamLockStock(teamRefundSuccess);
        }catch (Exception e){
            log.info("接收消息（退单成功）-恢复拼团队伍锁单量失败:{}",message,e);
            throw new RuntimeException(e);
        }
    }

}
