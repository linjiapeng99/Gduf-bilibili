package com.gduf.bilibili.dao;

import com.gduf.bilibili.domain.Danmu;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DanmuDao {
    Integer addDanmu(Danmu danmu);
}
