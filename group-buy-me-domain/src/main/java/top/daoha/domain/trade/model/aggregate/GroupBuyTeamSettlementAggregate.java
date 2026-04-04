package top.daoha.domain.trade.model.aggregate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import top.daoha.domain.trade.model.entity.GroupBuyTeamEntity;
import top.daoha.domain.trade.model.entity.TradePaySuccessEntity;
import top.daoha.domain.trade.model.entity.UserEntity;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GroupBuyTeamSettlementAggregate {
    private UserEntity userEntity;
    private GroupBuyTeamEntity groupBuyTeamEntity;
    private TradePaySuccessEntity tradePaySuccessEntity;
}
