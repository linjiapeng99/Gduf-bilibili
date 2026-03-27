package com.gduf.videoservice.dao;

import com.gduf.bilibilicommon.domain.Tag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TagDao {
    Integer addTags(Tag tag);

    List<Tag> getTags(String name);

    Integer deleteTags(Tag tag);
}
