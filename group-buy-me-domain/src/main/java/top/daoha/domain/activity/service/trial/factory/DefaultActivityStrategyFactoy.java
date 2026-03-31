package top.daoha.domain.activity.service.trial.factory;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import top.daoha.domain.activity.model.entity.MarketProductEntity;
import top.daoha.domain.activity.model.entity.TrialBalanceEntity;
import top.daoha.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import top.daoha.domain.activity.model.valobj.SkuVO;
import top.daoha.domain.activity.service.trial.node.RootNode;
import top.daoha.types.desgin.framework.tree.StrategyHandler;

import java.math.BigDecimal;

/**
 * @ClassName : DefaultActivityStrategyFactoy
 * @Description :
 * @github:
 * @Author : 24209
 * @Date: 2026/3/22  15:48
 */
@Service
public class DefaultActivityStrategyFactoy {

    private final RootNode rootNode;

    public DefaultActivityStrategyFactoy(RootNode rootNode) {
        this.rootNode = rootNode;
    }

    public StrategyHandler<MarketProductEntity,DynamicContext, TrialBalanceEntity> strategyHandler(){
        return rootNode;
    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DynamicContext{
        //拼团活动营销配置值对象
        private GroupBuyActivityDiscountVO groupBuyActivityDiscountVO;
        //商品信息
        private SkuVO skuVO;
        //折扣最终价格
        private BigDecimal deductionPrice;
        //活动是否可见
        private Boolean visible;
        //活动是否可参与
        private Boolean enabled;
    }
}
