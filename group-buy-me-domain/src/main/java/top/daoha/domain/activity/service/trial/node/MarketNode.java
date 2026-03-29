package top.daoha.domain.activity.service.trial.node;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.daoha.domain.activity.model.entity.MarketProductEntity;
import top.daoha.domain.activity.model.entity.TrialBalanceEntity;
import top.daoha.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import top.daoha.domain.activity.model.valobj.SkuVO;
import top.daoha.domain.activity.service.discount.IDiscountCalculateService;
import top.daoha.domain.activity.service.trial.AbstractGroupBuyMarketSupport;
import top.daoha.domain.activity.service.trial.factory.DefaultActivityStrategyFactoy;
import top.daoha.domain.activity.service.trial.thread.QueryGroupBuyActivityDiscountVOThreadTask;
import top.daoha.domain.activity.service.trial.thread.QuerySkuVOFromDBThreadTask;
import top.daoha.types.desgin.framework.tree.StrategyHandler;
import top.daoha.types.enums.ResponseCode;
import top.daoha.types.exception.AppException;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @ClassName : MarketNode
 * @Description :营销节点
 * @github:
 * @Author : 24209
 * @Date: 2026/3/22  16:14
 */
@Slf4j
@Service
public class MarketNode extends AbstractGroupBuyMarketSupport<MarketProductEntity, DefaultActivityStrategyFactoy.DynamicContext, TrialBalanceEntity> {

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;
    @Resource
    private EndNode endNode;
    @Resource
    private Map<String, IDiscountCalculateService> discountCalculateServiceMap;

    @Override
    protected void mutiThread(MarketProductEntity requestParameter, DefaultActivityStrategyFactoy.DynamicContext dynamicContext) throws ExecutionException, InterruptedException, TimeoutException {
        QueryGroupBuyActivityDiscountVOThreadTask queryGroupBuyActivityDiscountVOThreadTask = new QueryGroupBuyActivityDiscountVOThreadTask(requestParameter.getSource(), requestParameter.getChannel(), iActivityRepository);
        FutureTask<GroupBuyActivityDiscountVO> groupBuyActivityDiscountVOFutureTask = new FutureTask<>(queryGroupBuyActivityDiscountVOThreadTask);
        threadPoolExecutor.execute(groupBuyActivityDiscountVOFutureTask);

        QuerySkuVOFromDBThreadTask querySkuVOFromDBThreadTask = new QuerySkuVOFromDBThreadTask(requestParameter.getGoodsId(), iActivityRepository);
        FutureTask<SkuVO> skuVOFutureTask = new FutureTask<>(querySkuVOFromDBThreadTask);
        threadPoolExecutor.execute(skuVOFutureTask);

        dynamicContext.setGroupBuyActivityDiscountVO(groupBuyActivityDiscountVOFutureTask.get(timeout, TimeUnit.MINUTES));
        dynamicContext.setSkuVO(skuVOFutureTask.get(timeout, TimeUnit.MINUTES));
    }

    @Override
    public TrialBalanceEntity doApply(MarketProductEntity requestParameter, DefaultActivityStrategyFactoy.DynamicContext dynamicContext) throws Exception {
        GroupBuyActivityDiscountVO groupBuyActivityDiscountVO = dynamicContext.getGroupBuyActivityDiscountVO();
        GroupBuyActivityDiscountVO.GroupBuyDiscount groupBuyDiscount = groupBuyActivityDiscountVO.getGroupBuyDiscount();
        String marketPlan = groupBuyDiscount.getMarketPlan();

        SkuVO skuVO = dynamicContext.getSkuVO();
        IDiscountCalculateService iDiscountCalculateService = discountCalculateServiceMap.get(marketPlan);
        if (null == iDiscountCalculateService) {
            throw new AppException(ResponseCode.E0001.getCode());
        }
        BigDecimal calculate1 = iDiscountCalculateService.calculate(requestParameter.getUserId(), skuVO.getOriginalPrice(), groupBuyDiscount);
        dynamicContext.setDeductionPrice(calculate1);

        return router(requestParameter, dynamicContext);
    }

    @Override
    public StrategyHandler<MarketProductEntity, DefaultActivityStrategyFactoy.DynamicContext, TrialBalanceEntity> get(MarketProductEntity requestParameter, DefaultActivityStrategyFactoy.DynamicContext dynamicContext) {
        return endNode;
    }
}
