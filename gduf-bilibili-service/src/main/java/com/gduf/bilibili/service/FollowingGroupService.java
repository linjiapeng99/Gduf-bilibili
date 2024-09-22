package com.gduf.bilibili.service;

import com.gduf.bilibili.dao.FollowingGroupDao;
import com.gduf.bilibili.domain.FollowingGroup;
import com.gduf.bilibili.domain.UserFollowing;
import com.gduf.bilibili.domain.constant.UserContant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FollowingGroupService {
    @Autowired
    private FollowingGroupDao followingGroupDao;

    /**
     * 根据关注类型查询分组
     * @param type
     * @return
     */
    public FollowingGroup getByType(String type) {
        return followingGroupDao.getByType(type);
    }

    /**
     * 根据id查询关注分组
     * @param id
     * @return
     */
    public FollowingGroup getById(Long id) {
        return followingGroupDao.getById(id);
    }

    public List<FollowingGroup> getByUserId(Long userId) {
        return followingGroupDao.getByUserId(userId);
    }

    public void addUserFollowingGroups(FollowingGroup followingGroup) {
        followingGroupDao.addUserFollowingGroups(followingGroup);
    }

    public List<FollowingGroup> getUserFollowingGroups(Long userId) {
        return followingGroupDao.getUserFollowingGroups(userId);
    }
}
