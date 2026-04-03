package top.daoha.types.desgin.framework.link.model2.chain;

import org.springframework.stereotype.Service;
import top.daoha.types.desgin.framework.link.model2.handler.ILogicHandler;


public class BusinessLinkedList<T,D,R> extends LinkedList<ILogicHandler<T,D,R>> implements ILogicHandler<T,D,R>{

    public  BusinessLinkedList(String name){
        super(name);
    }

    @Override
    public R apply(T requestParameter, D dynamicContext) throws Exception {
        Node<ILogicHandler<T,D,R>> current = this.first;
        do{
            ILogicHandler<T,D,R> temp = current.item;
            R result = temp.apply(requestParameter, dynamicContext);
            if(result!=null){
                return result;
            }
            current = current.next;
        }while(null!=current);

        return null;
    }
}
