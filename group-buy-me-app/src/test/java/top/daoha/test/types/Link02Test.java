package top.daoha.test.types;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.daoha.test.types.rule01.factory.Rule01TradeRuleFactory;
import top.daoha.test.types.rule02.factory.Rule02TradeRuleFactory;
import top.daoha.test.types.rule02.logic.XxxResponse;
import top.daoha.types.desgin.framework.link.model1.ILogicLink;
import top.daoha.types.desgin.framework.link.model2.chain.BusinessLinkedList;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class Link02Test {
    @Resource(name = "demo01")
    public BusinessLinkedList<String, Rule02TradeRuleFactory.DynamicContext, XxxResponse> businessLinkedList1;

    @Resource(name = "demo02")
    public BusinessLinkedList<String, Rule02TradeRuleFactory.DynamicContext, XxxResponse> businessLinkedList2;

    @Test
    public void testRule01() throws Exception {
        XxxResponse logic = businessLinkedList1.apply("123", new Rule02TradeRuleFactory.DynamicContext());
        log.info("测试结果:{}", JSON.toJSONString(logic));
    }

    @Test
    public void testRule02() throws Exception {
        XxxResponse logic = businessLinkedList2.apply("123", new Rule02TradeRuleFactory.DynamicContext());
        log.info("测试结果:{}", JSON.toJSONString(logic));
    }
}
