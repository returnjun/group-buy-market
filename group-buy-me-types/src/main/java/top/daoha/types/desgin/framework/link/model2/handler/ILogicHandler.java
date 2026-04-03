package top.daoha.types.desgin.framework.link.model2.handler;

public interface ILogicHandler<T, D, R> {

    /**
     * 下一个节点
     *
     * @param requestParameter
     * @param dynamicContext
     * @return
     */
    default R next(T requestParameter, D dynamicContext) {
        return null;
    }

    /**
     *
     * @param requestParameter 请求参数
     * @param dynamicContext   动态上下文
     * @return
     * @throws Exception
     */
    R apply(T requestParameter, D dynamicContext) throws Exception;
}
