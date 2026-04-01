package top.daoha.config;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.daoha.types.annotations.DCCValue;
import top.daoha.types.common.Constants;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class DCCValueBeanFactory implements BeanPostProcessor {
//    Spring的后置处理器接口，允许在Bean初始化前后进行自定义处理
//    实现了这个接口，Spring就会在每个Bean初始化完成时自动调用这个类的方法

    private static final String BASE_CONFIG_PATH = "group_buy_market_dcc_";

    //redis客户端
    private final RedissonClient redissonClient;
    //存储配置和Bean的映射
    private final Map<String,Object> dccObject= new HashMap<>();

    public DCCValueBeanFactory(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Bean("dccTopic")
    public RTopic dccRedisTopicListener(RedissonClient redissonClient) {
//实现了一个动态配置实时更新的功能
        RTopic topic = redissonClient.getTopic("group_buy_market_dcc");
//RTopic是Redisson提供的发布/订阅（Pub/Sub）模式的实现。创建一个名为 "group_buy_market_dcc" 的消息频道
        topic.addListener(String.class, (charSequence, s) -> {

            String[] split = s.split(Constants.SPLIT);
            String attribute = split[0];
            String key = BASE_CONFIG_PATH + attribute;
            String value = split[1];

            RBucket<Object> bucket = redissonClient.getBucket(key);
            boolean exists=bucket.isExists();
            if(!exists)return; // 如果配置不存在，就不处理

            bucket.set(value); // 更新 Redis 中的值

            //查找需要更新的 Bean
            Object objBean = dccObject.get(key);
            if(null==objBean)return;


            //这段代码是在处理Spring的AOP代理对象，目的是获取被代理对象的真实类。
            Class<?> objBeanClass = objBean.getClass();
            if(AopUtils.isAopProxy(objBean)){
                objBeanClass = AopUtils.getTargetClass(objBean);
            }

            try {
                Field field = objBeanClass.getDeclaredField(attribute);
                field.setAccessible(true);// 强制访问私有字段
                field.set(objBean, value);// 设置字段的值
                field.setAccessible(false);// 恢复访问权限
            }catch (Exception e){
                throw new RuntimeException("....");
            }
        });

        return topic;
    }




    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetBeanClass = bean.getClass();
        Object targetBeanObject = bean;

        //如果是代理切面对象，需要获取原始目标对象
        if (AopUtils.isAopProxy(bean)) {
            targetBeanClass = AopUtils.getTargetClass(bean);
            targetBeanObject = AopProxyUtils.getSingletonTarget(bean);
        }

        Field[] fields = targetBeanClass.getDeclaredFields(); // 获取所有字段（包括私有）
        for(Field field:fields){
            if(!field.isAnnotationPresent(DCCValue.class))// 检查是否有@DCCValue注解
                continue;

            DCCValue dccValue = field.getAnnotation(DCCValue.class);
            String value = dccValue.value();
            if(value.isEmpty()){
                throw new RuntimeException("DCCValue注解的value属性不能为空");
            }

            String[] split = value.split(":");
            String key = BASE_CONFIG_PATH.concat(split[0]);
            String defaultValue = split.length==2?split[1]:null;

            String setValue = defaultValue;

            try{

                if(StringUtils.isBlank(defaultValue)){
                    throw new RuntimeException("..");
                }

                RBucket<String> bucket = redissonClient.getBucket(key);
                boolean exists = bucket.isExists();
                if(!exists){
                    bucket.set(defaultValue);// Redis中不存在，设置默认值
                }else {
                    setValue = bucket.get();// Redis中存在，使用Redis中的值
                }

                field.setAccessible(true);// 强制访问私有字段
                field.set(targetBeanObject, setValue);// 设置字段的值
                field.setAccessible(false);// 恢复访问权限

            }catch (Exception e){
                throw new RuntimeException("....");
            }

            dccObject.put(key, targetBeanObject);

        }
        return bean;
    }
}
/**
 * @Component
 * public class OrderService {
 *     @DCCValue("order_timeout:3000")
 *     private String timeout;
 *     public void processOrder() {
 *         System.out.println("超时时间：" + timeout);
 *     }
 * }
 * 执行流程：
 * Spring创建OrderService对象
 * postProcessAfterInitialization被调用
 * 发现timeout字段有@DCCValue注解
 * 从Redis读取group_buy_market_dcc_order_timeout
 * 如果Redis没有，写入默认值3000
 * 通过反射将值注入到timeout字段
 * 最终timeout的值为Redis中的配置
 */