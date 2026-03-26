package top.daoha.types.desgin.framework.tree;

/**
 * @ClassName : StrategyHandler
 * @Description :策略处理器
 * @github:
 * @Author : 24209
 * @Date: 2026/3/22  12:09
 */

public interface StrategyHandler<T, D, R> {

    StrategyHandler DEFAULT = (T, D) -> null;

    /**
     *
     * @param requestParameter   输入的业务数据
     * @param dynamicContext     执行时的上下文环境（比如用户权限、配置信息等）
     * @return
     * @throws Exception
     */
    R apply(T requestParameter, D dynamicContext) throws Exception;
}
