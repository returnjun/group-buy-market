package top.daoha.test.types.rule02.logic;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.daoha.test.types.rule02.factory.Rule02TradeRuleFactory;
import top.daoha.types.desgin.framework.link.model1.AbstractLogicLink;
import top.daoha.types.desgin.framework.link.model2.handler.ILogicHandler;

@Slf4j
@Service
public class RuleLogic202 implements ILogicHandler<String, Rule02TradeRuleFactory.DynamicContext, XxxResponse> {

    @Override
    public XxxResponse apply(String requestParameter, Rule02TradeRuleFactory.DynamicContext dynamicContext) {

        log.info("link model02 RuleLogic202");

        return  new XxxResponse("你好你好你好");
    }

}
