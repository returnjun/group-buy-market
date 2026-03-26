package top.daoha.domain.activity.service;

import top.daoha.domain.activity.model.entity.MarketProductEntity;
import top.daoha.domain.activity.model.entity.TrialBalanceEntity;

/**
 * @ClassName : IIndexGroupBuyMarketService
 * @Description :
 * @github:
 * @Author : 24209
 * @Date: 2026/3/22  16:19
 */

public interface IIndexGroupBuyMarketService {
    //输入商品信息然后返回商品折扣后的结果
    TrialBalanceEntity indexMarketTrial(MarketProductEntity marketProductEntity)throws Exception;
}
