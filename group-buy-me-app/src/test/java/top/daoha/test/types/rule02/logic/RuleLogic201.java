package top.daoha.test.types.rule02.logic;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.daoha.test.types.rule02.factory.Rule02TradeRuleFactory;
import top.daoha.types.desgin.framework.link.model1.AbstractLogicLink;
import top.daoha.types.desgin.framework.link.model2.handler.ILogicHandler;

@Slf4j
@Service
public class RuleLogic201 implements ILogicHandler<String, Rule02TradeRuleFactory.DynamicContext, XxxResponse> {

    @Override
    public XxxResponse apply(String requestParameter, Rule02TradeRuleFactory.DynamicContext dynamicContext) throws Exception {

        log.info("link model02 RuleLogic201");

        return next(requestParameter,dynamicContext);
    }

}
