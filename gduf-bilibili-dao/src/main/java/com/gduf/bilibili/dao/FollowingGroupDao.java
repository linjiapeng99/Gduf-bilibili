package com.gduf.bilibili.dao;

import com.gduf.bilibili.domain.FollowingGroup;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FollowingGroupDao {

    FollowingGroup getByType(String type);

    FollowingGroup getById(Long id);

    List<FollowingGroup> getByUserId(Long userId);

    Integer addUserFollowingGroups(FollowingGroup followingGroup);

    List<FollowingGroup> getUserFollowingGroups(Long userId);
}
