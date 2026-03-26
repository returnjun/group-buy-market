package top.daoha.types.desgin.framework.tree;

/**
 * @ClassName : StrategyMapper
 * @Description :策略映射器
 * @github:
 * @Author : 24209
 * @Date: 2026/3/22  12:07
 */

public interface StrategyMapper<T, D, R> {
    StrategyHandler<T, D, R> get(T requestParameter, D dynamicContext);
}
