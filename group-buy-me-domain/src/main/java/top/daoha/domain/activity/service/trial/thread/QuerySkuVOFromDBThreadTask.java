package top.daoha.domain.activity.service.trial.thread;

import top.daoha.domain.activity.adapter.repository.IActivityRepository;
import top.daoha.domain.activity.model.valobj.GroupBuyActivityDiscountVO;
import top.daoha.domain.activity.model.valobj.SkuVO;

import java.util.concurrent.Callable;

/**
 * @ClassName : QuerySkuVOFromDBThreadTask
 * @Description :
 * @github:
 * @Author : 24209
 * @Date: 2026/3/26  10:38
 */

public class QuerySkuVOFromDBThreadTask implements Callable<SkuVO> {

    private final String goodsId;

    private final IActivityRepository repository;

    public QuerySkuVOFromDBThreadTask(String goodsId, IActivityRepository repository) {
        this.goodsId = goodsId;
        this.repository = repository;
    }

    @Override
    public SkuVO call() throws Exception {
        return repository.querySkuByGoosId(goodsId);
    }
}
