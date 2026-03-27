package com.gduf.danmuservice.dao;

import com.gduf.bilibilicommon.domain.Danmu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface DanmuDao {
    Integer addDanmu(Danmu danmu);

    List<Danmu> getDanmus(Map<String, Object> map);
}
