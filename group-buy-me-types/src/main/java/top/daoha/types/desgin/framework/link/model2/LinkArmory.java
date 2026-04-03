package top.daoha.types.desgin.framework.link.model2;

import org.springframework.web.util.pattern.PathPattern;
import top.daoha.types.desgin.framework.link.model1.ILogicLink;
import top.daoha.types.desgin.framework.link.model2.chain.BusinessLinkedList;
import top.daoha.types.desgin.framework.link.model2.handler.ILogicHandler;

public class LinkArmory<T,D,R>  {

    private final BusinessLinkedList<T,D,R> logicLink;

    // ...是可变参数的意思，可变参数的本质：编译器会将其转换为数组
    //负责接收一堆 Handler，把它们装进链表里，组装成一个logicLink链表
    public LinkArmory(String linkName  , ILogicHandler<T,D,R>... logicLinks) {
        logicLink = new BusinessLinkedList<>(linkName);
        for (ILogicHandler<T,D,R> businessLinkedList : logicLinks) {
            logicLink.add(businessLinkedList);
        }
    }

    public BusinessLinkedList<T,D,R> getLogicLink() {
        return logicLink;
    }
}
