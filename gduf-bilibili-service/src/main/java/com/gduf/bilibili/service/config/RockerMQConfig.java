package com.gduf.bilibili.service.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gduf.bilibili.domain.UserFollowing;
import com.gduf.bilibili.domain.UserMoments;
import com.gduf.bilibili.domain.constant.UserMomentsConstant;
import com.gduf.bilibili.service.UserFollowingService;
import com.mysql.cj.util.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RockerMQConfig {
    @Value("${rocketmq.name.server.address}")
    private String nameServerAddr;
    @Autowired
    private RedisTemplate<String,String>redisTemplate;
    @Autowired
    private UserFollowingService userFollowingService;
    //创建生产者
    @Bean("momentsProducer")
    public DefaultMQProducer momentsProducer() throws MQClientException {
        DefaultMQProducer producer=new DefaultMQProducer(UserMomentsConstant.GRUOP_MOMENTS);
        producer.setNamesrvAddr(nameServerAddr);
        producer.start();
        return producer;
    }

    @Bean("momentsConsumer")
    public DefaultMQPushConsumer momentsConsumer() throws MQClientException {
        DefaultMQPushConsumer consumer=new DefaultMQPushConsumer(UserMomentsConstant.GRUOP_MOMENTS);
        consumer.setNamesrvAddr(nameServerAddr);
        //消费者订阅
        //参数一：订阅内容主题
        //参数二：订阅内容二级主题，即下级分类
        consumer.subscribe(UserMomentsConstant.TOPIC_MOMENTS,"*");
        //添加监听器
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                Message msg=msgs.get(0);
                if(msg==null){
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                String bodyStr=new String(msg.getBody());
                UserMoments userMoments = JSONObject.toJavaObject(JSONObject.parseObject(bodyStr), UserMoments.class);
                Long userId = userMoments.getUserId();
                //获取up主的其他订阅者
                List<UserFollowing> userFans = userFollowingService.getUserFans(userId);
                for (UserFollowing fan : userFans) {
                    //获取用户订阅的其他up主动态
                    String key="subscribed-"+fan.getFollowingId();
                    String subScribedListStr = redisTemplate.opsForValue().get(key);
                    List<UserMoments>subScribedList;
                    if(StringUtils.isNullOrEmpty(subScribedListStr)){
                        subScribedList=new ArrayList<>();
                    }else {
                        subScribedList = JSONArray.parseArray(subScribedListStr, UserMoments.class);
                    }
                    subScribedList.add(userMoments);
                    //将消息转为字符串发送到RocketMQ
                    redisTemplate.opsForValue().set(key,JSONObject.toJSONString(subScribedList));

                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        return consumer;
    }

}
