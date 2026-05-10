package top.daoha.test.domain.trade;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.daoha.api.IMarketTradeService;
import top.daoha.api.dto.LockMarketPayOrderRequestDTO;
import top.daoha.api.dto.LockMarketPayOrderResponseDTO;
import top.daoha.api.response.Response;
import top.daoha.domain.trade.model.entity.TradeRefundBehaviorEntity;
import top.daoha.domain.trade.model.entity.TradeRefundCommandEntity;
import top.daoha.domain.trade.service.ITradeRefundOrderService;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;

/**
 * 锁单、恢复、锁单
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2025/7/29 10:34
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ITradeReverseStockServiceTest {

    @Resource
    private ITradeRefundOrderService tradeRefundOrderService;

    @Resource
    private IMarketTradeService marketTradeService;

    @Test
    public void test_refundOrder() throws Exception {
        TradeRefundCommandEntity tradeRefundCommandEntity = TradeRefundCommandEntity.builder()
                .userId("xfg102")
                .outTradeNo("487289523679")
                .source("s01")
                .channel("c01")
                .build();

        TradeRefundBehaviorEntity tradeRefundBehaviorEntity = tradeRefundOrderService.refundOrder(tradeRefundCommandEntity);

        log.info("请求参数:{}", JSON.toJSONString(tradeRefundCommandEntity));
        log.info("测试结果:{}", JSON.toJSONString(tradeRefundBehaviorEntity));

        // 暂停，等待MQ消息。处理完后，手动关闭程序
        new CountDownLatch(1).await();
    }

    @Test
    public void test_lockMarketPayOrder() throws InterruptedException {
        String teamId = null;
        for (int i = 1; i < 4; i++) {
            LockMarketPayOrderRequestDTO lockMarketPayOrderRequestDTO = new LockMarketPayOrderRequestDTO();
            lockMarketPayOrderRequestDTO.setUserId("xfg10" + i);
            lockMarketPayOrderRequestDTO.setTeamId(teamId);
            lockMarketPayOrderRequestDTO.setActivityId(100123L);
            lockMarketPayOrderRequestDTO.setGoodsId("9890001");
            lockMarketPayOrderRequestDTO.setSource("s01");
            lockMarketPayOrderRequestDTO.setChannel("c01");
            lockMarketPayOrderRequestDTO.setNotifyMQ();
            lockMarketPayOrderRequestDTO.setOutTradeNo(RandomStringUtils.randomNumeric(12));

            Response<LockMarketPayOrderResponseDTO> lockMarketPayOrderResponseDTOResponse = marketTradeService.lockMarketPayOrder(lockMarketPayOrderRequestDTO);
            teamId = lockMarketPayOrderResponseDTOResponse.getData().getTeamId();

            log.info("第{}笔，测试结果 req:{} res:{}", i, JSON.toJSONString(lockMarketPayOrderRequestDTO), JSON.toJSONString(lockMarketPayOrderResponseDTOResponse));
        }
    }

    @Test
    public void test_lockMarketPayOrder_reverse() throws InterruptedException {
        LockMarketPayOrderRequestDTO lockMarketPayOrderRequestDTO = new LockMarketPayOrderRequestDTO();
        lockMarketPayOrderRequestDTO.setUserId("xfg804");
        lockMarketPayOrderRequestDTO.setTeamId("23165018");
        lockMarketPayOrderRequestDTO.setActivityId(100123L);
        lockMarketPayOrderRequestDTO.setGoodsId("9890001");
        lockMarketPayOrderRequestDTO.setSource("s01");
        lockMarketPayOrderRequestDTO.setChannel("c01");
        lockMarketPayOrderRequestDTO.setNotifyMQ();
        lockMarketPayOrderRequestDTO.setOutTradeNo(RandomStringUtils.randomNumeric(12));

        Response<LockMarketPayOrderResponseDTO> lockMarketPayOrderResponseDTOResponse = marketTradeService.lockMarketPayOrder(lockMarketPayOrderRequestDTO);

        log.info("测试结果 req:{} res:{}", JSON.toJSONString(lockMarketPayOrderRequestDTO), JSON.toJSONString(lockMarketPayOrderResponseDTOResponse));
    }

}
