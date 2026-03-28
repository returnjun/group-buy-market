package top.daoha.infrastructure.dao;


import org.apache.ibatis.annotations.Mapper;
import top.daoha.infrastructure.dao.po.CrowdTagsJob;

/**
 * @author Fuzhengwei bugstack.cn @小傅哥
 * @description 人群标签任务
 * @create 2024-12-28 11:50
 */
@Mapper
public interface ICrowdTagsJobDao {

    CrowdTagsJob queryCrowdTagsJob(CrowdTagsJob crowdTagsJobReq);

}
