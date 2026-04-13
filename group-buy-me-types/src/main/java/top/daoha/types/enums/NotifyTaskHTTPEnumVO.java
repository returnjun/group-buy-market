package top.daoha.types.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum NotifyTaskHTTPEnumVO {
    SUCCESS("success", "成功"),
    ERROR("error", "失败"),
    NULL(null, "控制行"),

    ;

    /** 状态码 */
    private String code;
    private String info;

}
