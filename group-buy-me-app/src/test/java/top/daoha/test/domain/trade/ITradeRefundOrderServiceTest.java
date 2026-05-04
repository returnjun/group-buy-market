package top.daoha.test.domain.trade;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.daoha.domain.activity.model.entity.MarketProductEntity;
import top.daoha.domain.activity.model.entity.TrialBalanceEntity;
import top.daoha.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import top.daoha.domain.activity.service.IIndexGroupBuyMarketService;
import top.daoha.domain.trade.model.entity.*;
import top.daoha.domain.trade.service.ITradeLockOrderService;
import top.daoha.domain.trade.service.ITradeRefundOrderService;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 交易订单服务测试
 * @create 2025-01-11 11:52
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ITradeRefundOrderServiceTest {

    @Resource
    private ITradeRefundOrderService tradeRefundOrderService;

    @Test
    public void test_refundOrder() throws Exception {
        // 入参信息
        Long activityId = 100123L;
        String userId = "gdk01";
        String goodsId = "9890001";
        String source = "s01";
        String channel = "c01";
        String outTradeNo = "457477950779";

        TradeRefundCommandEntity refundOrder = TradeRefundCommandEntity.builder()
                .userId(userId)
                .source(source)
                .channel(channel)
                .outTradeNo(outTradeNo)
                .build();

        TradeRefundBehaviorEntity tradeRefundBehaviorEntity = tradeRefundOrderService.refundOrder(refundOrder);

        log.info("退单测试结果(New):{}",JSON.toJSONString(tradeRefundBehaviorEntity));

        new CountDownLatch(1).await();
    }

}
