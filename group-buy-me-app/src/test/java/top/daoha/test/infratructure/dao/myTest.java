package top.daoha.test.infratructure.dao;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import top.daoha.infrastructure.dao.IGroupBuyActivityDao;
import top.daoha.infrastructure.dao.IGroupBuyDiscountDao;
import top.daoha.infrastructure.dao.po.GroupBuyActivity;
import top.daoha.infrastructure.dao.po.GroupBuyDiscount;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName : GroupBuyActivityDaoTest
 * @Description :
 * @github:
 * @Author : 24209
 * @Date: 2026/3/21  19:34
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class myTest {

    @Resource
    private IGroupBuyActivityDao iGroupBuyActivityDao;
    @Test
    public void test(){
        List<GroupBuyActivity> groupBuyActivityList=iGroupBuyActivityDao.queryGroupBuyActivityList();
        log.info("测试结果:{}", JSON.toJSONString(groupBuyActivityList));
    }

    @Resource
    private IGroupBuyDiscountDao iGroupBuyDiscountDao;
    @Test
    public void test2(){
        List<GroupBuyDiscount> a=iGroupBuyDiscountDao.queryGroupBuyDiscountList();
        log.info("测试结果:{}", JSON.toJSONString(a));
    }
}
