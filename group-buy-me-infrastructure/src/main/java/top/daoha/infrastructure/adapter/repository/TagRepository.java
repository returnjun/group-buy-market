package top.daoha.infrastructure.adapter.repository;


import com.thoughtworks.xstream.converters.reflection.AbstractReflectionConverter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBitSet;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;
import top.daoha.domain.tag.adapter.repository.ITagRepository;
import top.daoha.domain.tag.model.entity.CrowTagsJobEntity;
import top.daoha.infrastructure.dao.ICrowdTagsDao;
import top.daoha.infrastructure.dao.ICrowdTagsDetailDao;
import top.daoha.infrastructure.dao.ICrowdTagsJobDao;
import top.daoha.infrastructure.dao.po.CrowdTags;
import top.daoha.infrastructure.dao.po.CrowdTagsDetail;
import top.daoha.infrastructure.dao.po.CrowdTagsJob;
import top.daoha.infrastructure.redis.IRedisService;

import javax.annotation.Resource;

@Slf4j
@Repository
public class TagRepository implements ITagRepository {

    @Resource
    IRedisService redisService;

    @Resource
    ICrowdTagsJobDao crowdTagsJobDao;

    @Resource
    ICrowdTagsDao  crowdTagsDao;

    @Resource
    ICrowdTagsDetailDao crowdTagsDetailDao;

    @Override
    public CrowTagsJobEntity queryTagBatchJobRntity(String tagId, String batchId) {
        CrowdTagsJob crowdTagsJob = new CrowdTagsJob();
        crowdTagsJob.setTagId(tagId);
        crowdTagsJob.setBatchId(batchId);
        CrowdTagsJob crowdTagsJob1 = crowdTagsJobDao.queryCrowdTagsJob(crowdTagsJob);
        if(crowdTagsJob1==null){
            log.error("标签任务不存在，标签ID：{}，批次ID：{}",tagId,batchId);
            return null;
        }

        CrowTagsJobEntity crowTagsJobEntity = new CrowTagsJobEntity();


        crowTagsJobEntity.setTagType(crowdTagsJob1.getTagType());
        crowTagsJobEntity.setTagRule(crowdTagsJob1.getTagRule());
        crowTagsJobEntity.setStatStartTime(crowdTagsJob1.getStatStartTime());
        crowTagsJobEntity.setStatEndTime(crowdTagsJob1.getStatEndTime());

        return crowTagsJobEntity;
    }

    @Override
    public void addCrowdTagsUserId(String tagId, String userId) {
        CrowdTagsDetail crowdTagsDetail = new CrowdTagsDetail();
        crowdTagsDetail.setUserId(userId);
        crowdTagsDetail.setTagId(tagId);
        try {
            crowdTagsDetailDao.addCrowdTagsUserId(crowdTagsDetail);

            RBitSet bitSet = redisService.getBitSet(tagId);
            bitSet.set(redisService.getIndexFromUserId(userId));

        }catch (DuplicateKeyException e){
        }

    }

    @Override
    public void updateCrowdTagsStatistics(String tagId, int size) {
        CrowdTags crowdTags = new CrowdTags();
        crowdTags.setTagId(tagId);
        crowdTags.setStatistics(size);
        crowdTagsDao.updateCrowdTagsStatistics(crowdTags);
    }

}
