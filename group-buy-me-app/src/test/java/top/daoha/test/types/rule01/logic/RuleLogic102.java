package top.daoha.test.types.rule01.logic;


import cn.bugstack.wrench.design.framework.link.model1.AbstractLogicLink;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.daoha.test.types.rule02.factory.Rule02TradeRuleFactory;

@Slf4j
@Service
public class RuleLogic102 extends AbstractLogicLink<String, Rule02TradeRuleFactory.DynamicContext,String> {

    @Override
    public String apply(String requestParameter, Rule02TradeRuleFactory.DynamicContext dynamicContext) {

        log.info("link model01 RuleLogic102");

        return "link model01 单实例链";
    }

}
