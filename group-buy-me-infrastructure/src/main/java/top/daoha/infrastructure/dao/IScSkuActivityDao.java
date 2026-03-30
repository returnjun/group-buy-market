package top.daoha.infrastructure.dao;

import org.apache.ibatis.annotations.Mapper;
import top.daoha.infrastructure.dao.po.ScSkuActivity;

@Mapper
public interface IScSkuActivityDao {
    ScSkuActivity queryByGoodsId(ScSkuActivity scSkuActivity);
}
