package com.gduf.bilibili.dao;

import com.gduf.bilibili.domain.Danmu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DanmuDao {
    Integer addDanmu(Danmu danmu);

    List<Danmu> getDanmus(Map<String, Object> map);
}
