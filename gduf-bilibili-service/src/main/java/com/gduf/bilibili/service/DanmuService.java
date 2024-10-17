package com.gduf.bilibili.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gduf.bilibili.dao.DanmuDao;
import com.gduf.bilibili.domain.Danmu;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DanmuService {
    @Autowired
    private DanmuDao danmuDao;
    @Autowired
    private RedisTemplate<String, String>redisTemplate;

    public void addDanmu(Danmu danmu){
        danmuDao.addDanmu(danmu);
    }


    public void addDanmusToRedis(Danmu danmu) {
        String key="danmu-video-"+danmu.getVideoId();
        String value=redisTemplate.opsForValue().get(key);
        List<Danmu>list=new ArrayList<>();
        if(!StringUtils.isNullOrEmpty(value)){
            list= JSONArray.parseArray(value,Danmu.class);
        }
        list.add(danmu);
        redisTemplate.opsForValue().set(key,JSONObject.toJSONString(danmu));
    }
}
