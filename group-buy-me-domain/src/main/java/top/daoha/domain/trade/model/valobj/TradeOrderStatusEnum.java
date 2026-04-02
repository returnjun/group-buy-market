package top.daoha.domain.trade.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum TradeOrderStatusEnum {

    CREATE(0,"初始创建"),
    COMPLETED(1,"消费完成"),
    CLOSE(2,"超时关单"),
    ;


    private Integer code;
    private String info;


    public static TradeOrderStatusEnum valueof(Integer code) {
        switch(code){
            case 0: return CREATE;
            case 1: return COMPLETED;
            case 2: return CLOSE;
            default: return null;
        }
    }
}
