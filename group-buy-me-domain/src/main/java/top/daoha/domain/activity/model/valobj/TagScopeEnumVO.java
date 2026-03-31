package top.daoha.domain.activity.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum TagScopeEnumVO {
    Visible(true,false, "是否可见限制"),
    Enable(true,false, "是否参与限制"),
    ;

    private Boolean allow;
    private Boolean refuse;
    private String info;
}
