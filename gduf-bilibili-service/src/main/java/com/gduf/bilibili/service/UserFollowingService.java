package com.gduf.bilibili.service;

import com.gduf.bilibili.dao.FollowingGroupDao;
import com.gduf.bilibili.dao.UserFollowingDao;
import com.gduf.bilibili.domain.FollowingGroup;
import com.gduf.bilibili.domain.User;
import com.gduf.bilibili.domain.UserFollowing;
import com.gduf.bilibili.domain.UserInfo;
import com.gduf.bilibili.domain.constant.UserContant;
import com.gduf.bilibili.exception.ConditionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserFollowingService {
    @Autowired
    private UserFollowingDao userFollowingDao;

    @Autowired
    private FollowingGroupService followingGroupService;

    @Autowired
    private UserService userService;
    @Autowired
    private FollowingGroupDao followingGroupDao;

    /**
     * 添加用户关注
     *
     * @param userFollowing
     */
    @Transactional
    public void addUserFollowings(UserFollowing userFollowing) {
        Long groupId = userFollowing.getGroupId();
        if (groupId == null) {
            FollowingGroup followingGroup = followingGroupService.getByType(UserContant.FOLLOWING_GROUP_TYPE_DEFAULT);
            userFollowing.setGroupId(followingGroup.getId());
        } else {
            FollowingGroup followingGroup = followingGroupService.getById(userFollowing.getGroupId());
            if (followingGroup == null) {
                throw new ConditionException("关注分组不存在！");
            }
        }
        Long followingId = userFollowing.getFollowingId();
        User user = userService.getById(followingId);
        if (user == null) {
            throw new ConditionException("关注的用户不存在！");
        }
        userFollowingDao.deleteUserFollowing(userFollowing.getUserId(), followingId);
        userFollowing.setCreateTime(new Date());
        userFollowingDao.addUserFollowings(userFollowing);
    }

    /**
     * 第一步：获取关注用户列表
     * 第二步：根据关注用户的id查询关注用户的基本信息
     * 第三步：将关注用户按关注分组进行分类
     *
     * @param userId
     * @return
     */
    public List<FollowingGroup> getUserFollowings(Long userId) {
        //获取用户关注记录
        List<UserFollowing> list = userFollowingDao.getUserFollowings(userId);
        //获取用户关注记录的用户id
        Set<Long> followingIdSet = list.stream().map(UserFollowing::getFollowingId).collect(Collectors.toSet());
        List<UserInfo> userInfoList = new ArrayList<>();
        //获取用户关注的up主的账户信息
        if (!followingIdSet.isEmpty()) {
            userInfoList = userService.getUserInfoByUserIds(followingIdSet);
        }
        //关注的用户的信息匹配
        for (UserFollowing userFollowing : list) {
            for (UserInfo userInfo : userInfoList) {
                if (userFollowing.getFollowingId().equals(userInfo.getUserId())) {
                    userFollowing.setUserInfo(userInfo);
                }
            }
        }
        //获取用户相关的关注分类
        List<FollowingGroup> groupList = followingGroupService.getByUserId(userId);
        //创建一个默认全部关注
        FollowingGroup allGroup = new FollowingGroup();
        allGroup.setName(UserContant.USER_FOLLOWING_GROUP_ALL_NAME);
        //将用户关注的所有up主的信息全都设置进去全部关注的分组中
        allGroup.setFollowingUserInfoList(userInfoList);
        List<FollowingGroup> result = new ArrayList<>();
        result.add(allGroup);
        for (FollowingGroup group : groupList) {
            List<UserInfo> infoList = new ArrayList<>();
            for (UserFollowing userFollowing : list) {
                if (group.getId().equals(userFollowing.getGroupId())) {
                    infoList.add(userFollowing.getUserInfo());
                }
            }
            group.setFollowingUserInfoList(infoList);
            result.add(group);
        }
        return result;
    }

    /**
     * 获取用户粉丝列表
     * 第一步：获取当前用户的粉丝列表
     * 第二步：根据粉丝的用户id获取粉丝的账户信息
     * 第三步：查询当前用户是否与粉丝互粉
     * @param userId
     * @return
     */
    public List<UserFollowing> getUserFans(Long userId) {
        //获取粉丝记录
        List<UserFollowing> fanList = userFollowingDao.getUserFans(userId);
        Set<Long> fanIdSet = fanList.stream().map(UserFollowing::getUserId).collect(Collectors.toSet());
        //获取我的关注列表
        List<UserFollowing> followingList = userFollowingDao.getUserFollowings(userId);
        List<UserInfo>userInfoList=new ArrayList<>();
        //设置粉丝的账户信息
        if(!fanIdSet.isEmpty()){
            userInfoList=userService.getUserInfoByUserIds(fanIdSet);
        }

        for (UserFollowing fan : fanList) {
            //匹配粉丝对应的账户信息
            for (UserInfo userInfo : userInfoList) {
                if(fan.getUserId().equals(userInfo.getUserId())){
                    userInfo.setFollowed(false);
                    fan.setUserInfo(userInfo);
                }
            }
            //遍历我的关注，匹配我与粉丝是否互粉
            for (UserFollowing userFollowing : followingList) {
                if(userFollowing.getFollowingId().equals(fan.getUserId())){
                    fan.getUserInfo().setFollowed(true);
                }
            }
        }
        return fanList;
    }

    /**
     * 添加用户关注分组
     * @param followingGroup
     * @return
     */
    public Long addUserFollowingGroups(FollowingGroup followingGroup) {
        followingGroup.setCreateTime(new Date());
        followingGroup.setType(UserContant.FOLLOWING_GROUP_TYPE_USER);
        followingGroupService.addUserFollowingGroups(followingGroup);
        return followingGroup.getId();
    }

    /**
     * 获取用户关注分组
     * @param userId
     * @return
     */
    public List<FollowingGroup> getUserFollowingGroups(Long userId) {
        return followingGroupService.getUserFollowingGroups(userId);
    }

    /**
     * 校验用户是否被当前登录用户关注
     * @param userInfoList
     * @param userId
     * @return
     */
    public List<UserInfo> checkFollowingStatus(List<UserInfo> userInfoList, Long userId) {
        List<UserFollowing> followingList = userFollowingDao.getUserFollowings(userId);
        for (UserInfo userInfo : userInfoList) {
            userInfo.setFollowed(false);
            for (UserFollowing userFollowing : followingList) {
                if(userInfo.getUserId().equals(userFollowing.getFollowingId())){
                    userInfo.setFollowed(true);
                }
            }
        }
        return userInfoList;
    }
}
