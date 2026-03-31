package top.daoha.domain.tag.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import top.daoha.domain.tag.adapter.repository.ITagRepository;
import top.daoha.domain.tag.model.entity.CrowTagsJobEntity;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName : TagService
 * @Description :
 * @github:
 * @Author : 24209
 * @Date: 2026/3/26  19:47
 */
@Slf4j
@Service
public class TagService implements ITagService{

    @Resource
    private ITagRepository tagRepository;


    @Override
    public void execTagBatchJob(String tagId, String batchId) {
        //1查询批次任务
        CrowTagsJobEntity crowTagsJobEntity = tagRepository.queryTagBatchJobRntity(tagId, batchId);

        //2采集用户数据,根据上面的任务采集用户数据，比如说花费超过100元的用户，就给用户打上消费金额标签N次


        //3数据写入记录
        List<String> userIdList=new ArrayList<String>(){
            {
                add("xiaofuge");
                add("liergou");
            }
        };

        //4一般人群标签处理
        for (String userId : userIdList) {
            tagRepository.addCrowdTagsUserId(tagId, userId);
        }

        //5更新人群标签统计量
        tagRepository.updateCrowdTagsStatistics(tagId,userIdList.size());
    }
}
