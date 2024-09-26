package com.gduf.bilibili.service.util;

import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.CountDownLatch2;
import org.apache.rocketmq.common.message.Message;

import java.util.concurrent.TimeUnit;

public class RocketMQUtil {
    /**
     * 同步发送消息
     * @param producer 消息生产者
     * @param msg 要发送的消息
     * @throws Exception
     */
    public static void syncSendMessage(DefaultMQProducer producer, Message msg) throws Exception{
        SendResult result = producer.send(msg);
        System.out.println(result);
    }

    /**
     * 异步发送消息
     * @param producer 生产者
     * @param msg 要发送的消息
     * @throws Exception
     */
    public static void  ayncSendMessage (DefaultMQProducer producer,Message msg) throws Exception{
        int messageCount=2;
        CountDownLatch2 countDownLatch=new CountDownLatch2(messageCount);
        for (int i = 0; i < messageCount; i++) {
            producer.send(msg, new SendCallback() {
                //成功回调
                @Override
                public void onSuccess(SendResult sendResult) {
                    countDownLatch.countDown();
                    System.out.println(sendResult.getMsgId());
                }
                //失败回调
                @Override
                public void onException(Throwable throwable) {
                    countDownLatch.countDown();
                    System.out.println("发送消息的时候出现了异常"+throwable);
                    throwable.printStackTrace();
                }
            });
            countDownLatch.await(5, TimeUnit.SECONDS);
        }
    }
}
