package top.daoha.test.domain.activity;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.daoha.domain.activity.model.entity.MarketProductEntity;
import top.daoha.domain.activity.model.entity.TrialBalanceEntity;
import top.daoha.domain.activity.service.IIndexGroupBuyMarketService;

import javax.annotation.Resource;

/**
 * @ClassName : test
 * @Description :
 * @github:
 * @Author : 24209
 * @Date: 2026/3/26  11:14
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class threadtest {
    @Resource
    private IIndexGroupBuyMarketService iIndexGroupBuyMarketService;

    @Test
    public void test() throws Exception {
        MarketProductEntity marketProductEntity = new MarketProductEntity("xiaofuge", "9890001", "s01", "c01");
        TrialBalanceEntity trialBalanceEntity = iIndexGroupBuyMarketService.indexMarketTrial(marketProductEntity);
        log.info("请求参数：{}", JSON.toJSONString(marketProductEntity));
        log.info("返回结果：{}", JSON.toJSONString(trialBalanceEntity));
    }

    @Test
    public void test_error() throws Exception {
        MarketProductEntity marketProductEntity = new MarketProductEntity("xiaofuge", "9890002", "s01", "c01");
        TrialBalanceEntity trialBalanceEntity = iIndexGroupBuyMarketService.indexMarketTrial(marketProductEntity);
        log.info("请求参数：{}", JSON.toJSONString(marketProductEntity));
        log.info("返回结果：{}", JSON.toJSONString(trialBalanceEntity));
    }
}

