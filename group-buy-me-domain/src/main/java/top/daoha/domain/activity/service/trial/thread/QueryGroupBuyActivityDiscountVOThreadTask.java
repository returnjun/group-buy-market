package top.daoha.domain.activity.service.trial.thread;

import top.daoha.domain.activity.adapter.repository.IActivityRepository;
import top.daoha.domain.activity.model.valobj.GroupBuyActivityDiscountVO;

import java.util.concurrent.Callable;

/**
 * @ClassName : QueryGroupBuyActivityDiscountVOThreadTask
 * @Description :
 * @github:
 * @Author : 24209
 * @Date: 2026/3/25  16:43
 */

public class QueryGroupBuyActivityDiscountVOThreadTask implements Callable<GroupBuyActivityDiscountVO> {

    private final String source;

    private final String channel;

    private final IActivityRepository repository;

    public QueryGroupBuyActivityDiscountVOThreadTask(String source, String channel, IActivityRepository repository) {
        this.source = source;
        this.channel = channel;
        this.repository = repository;
    }

    @Override
    public GroupBuyActivityDiscountVO call() throws Exception {
        return repository.queryGroupBuyActivityDiscount(source,channel);
    }
}
