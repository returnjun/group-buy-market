package top.daoha.domain.tag.service;

/**
 * @ClassName : ITagService
 * @Description :
 * @github:
 * @Author : 24209
 * @Date: 2026/3/26  19:47
 */

public interface ITagService {

    /**
     * 执行标签任务
     * @param tagId 标签id
     * @param batchId 批次id
     */
    void execTagBatchJob(String tagId,String batchId);
}
