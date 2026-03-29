package top.daoha.domain.tag.adapter.repository;

import top.daoha.domain.tag.model.entity.CrowTagsJobEntity;

public interface ITagRepository {

    CrowTagsJobEntity queryTagBatchJobRntity(String tagId, String batchId);

    void addCrowdTagsUserId(String tagId, String userId);

    void updateCrowdTagsStatistics(String tagId, int size);
}
