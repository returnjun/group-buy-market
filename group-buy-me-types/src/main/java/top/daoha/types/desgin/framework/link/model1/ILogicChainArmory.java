package top.daoha.types.desgin.framework.link.model1;

public interface ILogicChainArmory<T,D,R>  {

    /**
     * 获取下一个逻辑链
     * @return 下一个逻辑链
     */
    ILogicLink<T,D,R> next();


    /**
     * 追加下一个逻辑链
     * @param next 下一个逻辑链
     * @return 当前逻辑链
     */
    ILogicLink<T,D,R> appendNext(ILogicLink<T,D,R> next);
}
