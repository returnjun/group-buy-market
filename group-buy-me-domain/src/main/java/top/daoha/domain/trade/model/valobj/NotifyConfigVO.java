package top.daoha.domain.trade.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotifyConfigVO {
    /** 通知类型 */
    private NotifyTypeEnumVO notifyType;
    /** 通知URL */
    private String notifyUrl;
    /** 通知MQ */
    private String notifyMQ;
}
