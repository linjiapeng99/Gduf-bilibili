package com.gduf.bilibili.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gduf.bilibili.dao.DanmuDao;
import com.gduf.bilibili.domain.Danmu;
import com.mysql.cj.util.StringUtils;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class DanmuService {
    @Autowired
    private DanmuDao danmuDao;
    @Autowired
    private RedisTemplate<String, String>redisTemplate;

    private static final String DANMU_KEY="danmu-video-";
    public void addDanmu(Danmu danmu){
        danmuDao.addDanmu(danmu);
    }
    @Async
    public void asycAddDanmu(Danmu danmu){
        danmuDao.addDanmu(danmu);
    }


    public void addDanmusToRedis(Danmu danmu) {
        String key=DANMU_KEY+danmu.getVideoId();
        String value=redisTemplate.opsForValue().get(key);
        List<Danmu>list=new ArrayList<>();
        if(!StringUtil.isNullOrEmpty(value)){
            list= JSONArray.parseArray(value,Danmu.class);
        }
        list.add(danmu);
        redisTemplate.opsForValue().set(key,JSONObject.toJSONString(list));
    }

    public List<Danmu> getDanmus(Long videoId, String startTime, String endTime) throws Exception {
        String key=DANMU_KEY+videoId;
        String value = redisTemplate.opsForValue().get(key);
        List<Danmu>list;
        if(!StringUtil.isNullOrEmpty(value)){//缓存中存在
            //拿出redis这个视频的所有弹幕
            list=JSONArray.parseArray(value,Danmu.class);
            if(!StringUtil.isNullOrEmpty(startTime)&& !StringUtil.isNullOrEmpty(endTime)){
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date startDate=sdf.parse(startTime);
                Date endDate=sdf.parse(endTime);
                //根据时间查询的弹幕
                List<Danmu>childList=new ArrayList<>();
                for (Danmu danmu : list) {
                    Date createTime=danmu.getCreateTime();
                    if(createTime.after(startDate)&& createTime.before(endDate)){
                        childList.add(danmu);
                    }
                }
                list=childList;
            }
        }else {//缓存中不存在则走数据库
            Map<String,Object> map=new HashMap<>();
            map.put("videoId",videoId);
            map.put("startTime",startTime);
            map.put("endTime",endTime);
            list=danmuDao.getDanmus(map);
            //保存弹幕到redis中
            redisTemplate.opsForValue().set(key,JSONObject.toJSONString(list));
        }
        return list;
    }
}
