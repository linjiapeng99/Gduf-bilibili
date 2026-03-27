package com.gduf.userservice.dao;

import com.gduf.bilibilicommon.domain.CollectionGroup;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CollectionGroupDao {

    Integer addCollectionGroup(CollectionGroup collectionGroup);

    List<CollectionGroup> getCollectionGroups(Long userId);
}
