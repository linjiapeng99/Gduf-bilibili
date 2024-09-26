package com.gduf.bilibili.service.config;

import com.gduf.bilibili.domain.constant.UserMomentsConstant;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import java.util.List;

@Configuration
public class RockerMQConfig {
    @Value("${rocketmq.name.server.address}")
    private String nameServerAddr;
    @Autowired
    private RedisTemplate<String,String>redisTemplate;
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
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                for (MessageExt msg : list) {
                    System.out.println(msg);
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        return consumer;
    }

}
