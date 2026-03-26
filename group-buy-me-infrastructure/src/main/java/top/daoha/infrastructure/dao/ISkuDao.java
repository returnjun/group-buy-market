package top.daoha.infrastructure.dao;

import org.apache.ibatis.annotations.Mapper;
import top.daoha.infrastructure.dao.po.Sku;

/**
 * @ClassName : ISkuDao
 * @Description :
 * @github:
 * @Author : 24209
 * @Date: 2026/3/25  15:26
 */
@Mapper
public interface ISkuDao {
    Sku queryByGoodsId(String goodsid);
}
