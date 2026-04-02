package top.daoha.types.desgin.framework.link.model1;

public abstract class AbstractLogicLink<T,D,R> implements ILogicLink<T,D,R> {

    private ILogicLink<T,D,R> next;


    @Override
    public ILogicLink<T, D, R> next() {
        //返回自己这个next节点
        return next;
    }

    @Override
    public ILogicLink<T, D, R> appendNext(ILogicLink<T, D, R> next) {
        //追加下一个逻辑链，先赋值在返回
        this.next = next;
        return next;
    }

    protected  R next(T requestParameter,D dynamicContext) throws Exception{

        return next.apply(requestParameter,dynamicContext);
    }
}
