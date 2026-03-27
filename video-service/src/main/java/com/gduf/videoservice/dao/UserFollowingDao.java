package com.gduf.videoservice.dao;

import com.gduf.bilibilicommon.domain.UserFollowing;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserFollowingDao {

    Integer deleteUserFollowing(@Param("userId") Long userId,@Param("followingId") Long followingId);

    Integer addUserFollowings(UserFollowing userFollowing);

    List<UserFollowing> getUserFollowings(Long userId);

    List<UserFollowing> getUserFans(Long userId);

    void updateUserFollowings(UserFollowing userFollowing);

    Integer getUserFansCount(Long userId);
}
