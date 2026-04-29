package top.daoha.infrastructure.adapter.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.daoha.infrastructure.dcc.DCCService;
import top.daoha.infrastructure.redis.IRedisService;

import javax.annotation.Resource;
import java.util.function.Supplier;

public abstract class AbstractRepository {

    private Logger logger = LoggerFactory.getLogger(AbstractRepository.class);

    @Resource
    protected IRedisService iRedisService;

    @Resource
    protected DCCService dccService;


    protected <T> T getFromCacheOrDb(String cacheKey, Supplier<T> dbFallback){
        if(dccService.isCacheOpenSwitch()){
            T cacheValue = iRedisService.getValue(cacheKey);

            if(null != cacheValue){
                return cacheValue;
            }

            T dbResult = dbFallback.get();

            if(null ==dbResult){
                return null;
            }

            iRedisService.setValue(cacheKey, dbResult);

            return dbResult;
        }else{
            // 缓存未开启，直接从数据库获取
            logger.warn("缓存降级 {}", cacheKey);
            return dbFallback.get();
        }
    }

    protected <T> T getFromCacheOrDb(String cacheKey, Supplier<T> dbFallback, long expired) {
        // 判断是否开启缓存
        if (dccService.isCacheOpenSwitch()) {
            // 从缓存获取
            T cacheResult = iRedisService.getValue(cacheKey);
            // 缓存存在则直接返回
            if (null != cacheResult) {
                return cacheResult;
            }
            // 缓存不存在则从数据库获取
            T dbResult = dbFallback.get();
            // 数据库查询结果为空则直接返回
            if (null == dbResult) {
                return null;
            }
            // 写入缓存（带过期时间）
            iRedisService.setValue(cacheKey, dbResult, expired);
            return dbResult;
        } else {
            // 缓存未开启，直接从数据库获取
            logger.warn("缓存降级 {}", cacheKey);
            return dbFallback.get();
        }
    }

}
