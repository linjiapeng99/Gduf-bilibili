package com.gduf.bilibili.service;

import com.gduf.bilibili.dao.CollectionGroupDao;
import com.gduf.bilibili.domain.CollectionGroup;
import com.gduf.bilibili.domain.constant.VideoConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class CollectionGroupService {
    @Autowired
    private CollectionGroupDao collectionGroupDao;

    public Long addCollectionGroup(CollectionGroup collectionGroup) {
        collectionGroup.setCreateTime(new Date());
        collectionGroup.setType(VideoConstant.COLLECTION_GROUP_TYPE_VIDEO);
        collectionGroupDao.addCollectionGroup(collectionGroup);
        return collectionGroup.getId();
    }

    public List<CollectionGroup> getCollectionGroups(Long userId) {
        return collectionGroupDao.getCollectionGroups(userId);
    }
}
