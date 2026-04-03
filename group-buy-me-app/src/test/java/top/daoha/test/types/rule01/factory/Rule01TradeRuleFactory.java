package top.daoha.test.types.rule01.factory;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import top.daoha.test.types.rule01.logic.RuleLogic101;
import top.daoha.test.types.rule01.logic.RuleLogic102;
import top.daoha.test.types.rule02.factory.Rule02TradeRuleFactory;
import top.daoha.types.desgin.framework.link.model1.ILogicLink;

import javax.annotation.Resource;

@Service
public class Rule01TradeRuleFactory {

    @Resource
    RuleLogic101 ruleLogic101;

    @Resource
    RuleLogic102 ruleLogic102;

    //关联逻辑链然后返回头节点
    public ILogicLink<String, Rule02TradeRuleFactory.DynamicContext,String> openLogicLink() {
        ruleLogic101.appendNext(ruleLogic102);
        return ruleLogic101;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DynamicContext {
        private String age;
    }
}
