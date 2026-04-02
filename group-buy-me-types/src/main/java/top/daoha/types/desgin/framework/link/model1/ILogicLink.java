package top.daoha.types.desgin.framework.link.model1;

public interface ILogicLink<T,D,R> extends ILogicChainArmory<T,D,R> {

    /**
     * 执行逻辑链
     * @param requestParameter 请求参数
     * @param dynamicContext 动态上下文
     * @return 响应结果
     */
    R apply(T requestParameter,D dynamicContext);

}
