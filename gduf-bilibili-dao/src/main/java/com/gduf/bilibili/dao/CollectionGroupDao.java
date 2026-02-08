package com.gduf.bilibili.dao;

import com.gduf.bilibili.domain.CollectionGroup;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CollectionGroupDao {

    Integer addCollectionGroup(CollectionGroup collectionGroup);

    List<CollectionGroup> getCollectionGroups(Long userId);
}
