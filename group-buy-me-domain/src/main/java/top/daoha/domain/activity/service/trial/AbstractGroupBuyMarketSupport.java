package top.daoha.domain.activity.service.trial;

import top.daoha.domain.activity.adapter.repository.IActivityRepository;
import top.daoha.types.desgin.framework.tree.AbstractMultiThreadStrategyRouter;
import top.daoha.types.desgin.framework.tree.AbstractStrategyRouter;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * @ClassName : AbstrastGroupBuyMarketSupport
 * @Description :
 * @github:
 * @Author : 24209
 * @Date: 2026/3/22  15:30
 */

public abstract class AbstractGroupBuyMarketSupport<MarketProductEntity,DynamicContext,TrialBalanceEntity> extends AbstractMultiThreadStrategyRouter<MarketProductEntity,DynamicContext,TrialBalanceEntity> {

    protected long timeout = 500;

    @Resource
    protected IActivityRepository iActivityRepository;

    @Override
    protected void mutiThread(MarketProductEntity requestParameter, DynamicContext dynamicContext) throws ExecutionException, InterruptedException, TimeoutException {

    }
}
