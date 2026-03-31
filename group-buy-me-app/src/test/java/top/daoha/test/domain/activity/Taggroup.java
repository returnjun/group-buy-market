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
import top.daoha.domain.tag.service.ITagService;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class Taggroup {
    @Resource
    private ITagService iTagService;

    @Resource
    private IIndexGroupBuyMarketService indexGroupBuyMarketService;

    @Test
    public void test_indexMarketTrial() throws Exception {
        iTagService.execTagBatchJob("RQ_KJHKL98UU78H66554GFDV","10001");


        MarketProductEntity marketProductEntity = new MarketProductEntity();
        marketProductEntity.setUserId("xiaofuge");
        marketProductEntity.setSource("s01");
        marketProductEntity.setChannel("c01");
        marketProductEntity.setGoodsId("9890001");

        TrialBalanceEntity trialBalanceEntity = indexGroupBuyMarketService.indexMarketTrial(marketProductEntity);
        log.info("请求参数：{}", JSON.toJSONString(marketProductEntity));
        log.info("返回结果：{}", JSON.toJSONString(trialBalanceEntity));
    }
}
