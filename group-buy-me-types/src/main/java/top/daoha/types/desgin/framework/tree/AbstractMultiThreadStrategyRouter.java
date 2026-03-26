package top.daoha.types.desgin.framework.tree;

import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName : AbstractMultiThreadStrategyRouter
 * @Description :
 * @github:
 * @Author : 24209
 * @Date: 2026/3/25  14:01
 */

public abstract class AbstractMultiThreadStrategyRouter <T, D, R> implements StrategyHandler<T, D, R>,StrategyMapper<T, D, R> {

    @Getter
    @Setter
    protected StrategyHandler<T, D, R> defaultStrategyHandler = StrategyHandler.DEFAULT;

    public R router(T requestParameter, D dynamicContext) throws Exception {
        StrategyHandler<T, D, R> strategyHandler = get(requestParameter, dynamicContext);
        return (null != strategyHandler ? strategyHandler : defaultStrategyHandler).apply(requestParameter, dynamicContext);
    }

    @Override
    public R apply(T requestParameter, D dynamicContext) throws Exception {
        mutiThread(requestParameter,dynamicContext);
        return doApply(requestParameter,dynamicContext);
    }

    protected abstract void mutiThread(T requestParameter, D dynamicContext) throws Exception;

    protected abstract R doApply(T requestParameter, D dynamicContext) throws Exception;

}