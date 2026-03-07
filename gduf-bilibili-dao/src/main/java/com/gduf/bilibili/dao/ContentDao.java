package com.gduf.bilibili.dao;

import com.gduf.bilibili.domain.Content;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ContentDao {
    Long addContent(Content content);
}
