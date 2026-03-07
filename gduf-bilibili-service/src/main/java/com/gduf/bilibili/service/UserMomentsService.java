package com.gduf.bilibili.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gduf.bilibili.dao.UserMomentsDao;
import com.gduf.bilibili.domain.*;
import com.gduf.bilibili.domain.constant.UserMomentsConstant;
import com.gduf.bilibili.service.util.RocketMQUtil;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserMomentsService {
    @Autowired
    private UserMomentsDao userMomemtsDao;
    @Autowired
    private ApplicationContext applicationContext;
    @Autowired
    private UserService userService;
    @Autowired
    private VideoService videoService;
    @Autowired
    private RedisTemplate<String,String>redisTemplate;

    @Value("${fdfs.http.storage-addr}")
    private String fastdfsUrl;

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
    public PageResult<UserMoments> pageListMoments(Integer size, Integer no,
                                                   Long userId, String type) {
        Map<String, Object> params = new HashMap<>();
        params.put("start", (no-1)*size);
        params.put("limit", size);
        params.put("userId", userId);
        params.put("type", type);
        Integer total = userMomemtsDao.pageCountMoments(params);
        List<UserMoments> list = new ArrayList<>();
        if(total > 0){
            list = userMomemtsDao.pageListMoments(params);
            if(!list.isEmpty()){
                //处理不同类型的动态
                this.processVideoMoment(list.stream()
                        .filter(item -> UserMomentsConstant.TYPE_VIDEO
                                .equals(item.getType())).collect(Collectors.toList()));
                this.processImgMoment(list.stream()
                        .filter(item -> UserMomentsConstant.TYPE_IMG
                                .equals(item.getType())).collect(Collectors.toList()));
                //匹配对应用户信息
                Set<Long> userIdSet = list.stream()
                        .map(UserMoments :: getUserId).collect(Collectors.toSet());
                List<UserInfo> userInfoList = userService.getUserInfoByUserIds(userIdSet);
                list.forEach(moment -> userInfoList.forEach(userInfo -> {
                    if(moment.getUserId().equals(userInfo.getUserId())){
                        moment.setUserInfo(userInfo);
                    }
                }));
            }
        }
        return new PageResult<>(total, list);
    }

    private void processImgMoment(List<UserMoments> list) {
        list.forEach(moment -> {
            Content content = moment.getContent();
            ImgContent contentDetail = content.getContentDetail().toJavaObject(ImgContent.class);
            contentDetail.setImg(fastdfsUrl + contentDetail.getImg());
            content.setContentDetail(JSONObject.parseObject(JSONObject.toJSONString(contentDetail)));
            moment.setContent(content);
        });
    }

    private void processVideoMoment(List<UserMoments> list) {
        List<Video> videoList = list.stream()
                .map(UserMoments::getContent)
                .map(content -> content.getContentDetail().toJavaObject(Video.class))
                .collect(Collectors.toList());
        List<Video> newVideoList = videoService.getVideoCount(videoList);
        newVideoList.forEach(video -> video.setThumbnail(fastdfsUrl+video.getThumbnail()));
        list.forEach(moment -> newVideoList.forEach(video ->{
            if(video.getId().equals(moment.getContent().getContentDetail().getLong("id"))){
                JSONObject contentDetail = JSONObject.parseObject(JSONObject.toJSONString(video));
                moment.getContent().setContentDetail(contentDetail);
            }
        }));
    }
}
