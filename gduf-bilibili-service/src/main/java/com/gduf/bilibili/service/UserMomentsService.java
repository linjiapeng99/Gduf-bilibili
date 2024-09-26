package com.gduf.bilibili.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gduf.bilibili.dao.UserMomentsDao;
import com.gduf.bilibili.domain.UserMoments;
import com.gduf.bilibili.domain.constant.UserMomentsConstant;
import com.gduf.bilibili.service.util.RocketMQUtil;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Service
public class UserMomentsService {
    @Autowired
    private UserMomentsDao userMomemtsDao;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private RedisTemplate<String,String>redisTemplate;

    /**
     * 添加用户动态
     * @param userMoments
     * @throws Exception
     */
    public void addUserMoments(UserMoments userMoments) throws Exception {
        userMoments.setCreateTime(new Date());
        userMomemtsDao.addUserMoments(userMoments);
        //获取springboot容器，目的是获取到生产者的bean，可以使用直接注入
        DefaultMQProducer producer =(DefaultMQProducer)applicationContext.getBean("momentsProducer");
        //构建要发送的消息
        Message msg=new Message(UserMomentsConstant.TOPIC_MOMENTS, JSONObject.toJSONString(userMoments).getBytes(StandardCharsets.UTF_8));
        RocketMQUtil.syncSendMessage(producer,msg);
    }

    /**
     * 获取用户动态
     * @param userId
     * @return
     */
    public List<UserMoments> getUserSubscribedMoments(Long userId) {
        String key="subscribed-"+userId;
        String listStr = redisTemplate.opsForValue().get(key);
        return JSONArray.parseArray(listStr,UserMoments.class);
    }
}
