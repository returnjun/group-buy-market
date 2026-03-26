package top.daoha.types.desgin.framework.tree;

import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName : AbstractStrategyRouter
 * @Description :
 * @github:
 * @Author : 24209
 * @Date: 2026/3/22  14:09
 */

public abstract class AbstractStrategyRouter<T, D, R> implements StrategyHandler<T, D, R>,StrategyMapper<T, D, R>{

    @Getter
    @Setter
    private StrategyHandler<T, D, R> defaultStrategyHandler = StrategyHandler.DEFAULT;

    public R router(T requestParameter, D dynamicContext) throws Exception{
        StrategyHandler<T, D, R> strategyHandler = get(requestParameter, dynamicContext);
        return (null !=strategyHandler ? strategyHandler:defaultStrategyHandler).apply(requestParameter,dynamicContext);
    }
}
