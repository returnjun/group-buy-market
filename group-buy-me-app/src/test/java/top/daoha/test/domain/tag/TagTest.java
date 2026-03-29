package top.daoha.test.domain.tag;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RBitSet;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.daoha.domain.tag.service.ITagService;
import top.daoha.infrastructure.redis.IRedisService;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TagTest {

    @Resource
    private ITagService iTagService;

    @Resource
    private IRedisService iRedisService;

    @Test
    public void test1() throws Exception{
        iTagService.execTagBatchJob("RQ_KJHKL98UU78H66554GFDV","10001");
    }

    @Test
    public void test2() throws Exception{
        RBitSet bitSet = iRedisService.getBitSet("RQ_KJHKL98UU78H66554GFDV");
        log.info("标签任务执行结果:{}",bitSet.get(iRedisService.getIndexFromUserId("liergou")));
        log.info("标签任务执行结果:{}",bitSet.get(iRedisService.getIndexFromUserId("xiaoguge")));
    }
}
