package com.gduf.danmuservice.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gduf.bilibilicommon.domain.constant.UserMomentsConstant;
import com.gduf.danmuservice.websocket.WebSocketService;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.List;

@Configuration
public class RockerMQConfig {
    @Value("${rocketmq.name.server.address}")
    private String nameServerAddr;
    //弹幕生产者
    @Bean("danmusProducer")
    public DefaultMQProducer danmusProducer() throws MQClientException {
        DefaultMQProducer producer=new DefaultMQProducer(UserMomentsConstant.GRUOP_DANMUS);
        producer.setNamesrvAddr(nameServerAddr);
        producer.start();
        return producer;
    }

    //弹幕消费者
    @Bean("danmusConsumer")
    public DefaultMQPushConsumer danmusConsumer() throws MQClientException {
        DefaultMQPushConsumer consumer=new DefaultMQPushConsumer(UserMomentsConstant.GRUOP_MOMENTS);
        consumer.setNamesrvAddr(nameServerAddr);
        //消费者订阅
        //参数一：订阅内容主题
        //参数二：订阅内容二级主题，即下级分类
        consumer.subscribe(UserMomentsConstant.TOPIC_DANMUS,"*");
        //添加监听器
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
                MessageExt msg=msgs.get(0);
                byte[] msgByte = msg.getBody();
                String bodyStr = new String(msgByte);
                JSONObject jsonObject= JSON.parseObject(bodyStr);
                String sessionId = jsonObject.getString("sessionId");
                String message=jsonObject.getString("message");
                WebSocketService webSocketService= WebSocketService.WEBSOCKET_MAP.get(sessionId);
                if(webSocketService.getSession().isOpen()){
                    try {
                        webSocketService.sendMessage(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                //该消息已被成功消费
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        return consumer;
    }
}
