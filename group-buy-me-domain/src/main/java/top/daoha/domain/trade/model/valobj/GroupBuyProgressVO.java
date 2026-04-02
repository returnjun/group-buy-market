package top.daoha.domain.trade.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupBuyProgressVO {

    private Integer targetCount;
    private Integer completeCount;
    private Integer lockCount;
}
