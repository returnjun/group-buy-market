package top.daoha.domain.trade.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public enum NotifyTypeEnumVO {
    HTTP("HTTP","HTTP通知"),
    MQ("MQ","MQ通知"),
    ;

    private String code;
    private String info;


}
