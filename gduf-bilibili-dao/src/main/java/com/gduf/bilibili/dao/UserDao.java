package com.gduf.bilibili.dao;

import com.alibaba.fastjson.JSONObject;
import com.gduf.bilibili.domain.RefreshTokenDetail;
import com.gduf.bilibili.domain.User;
import com.gduf.bilibili.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
public interface UserDao {
    //根据手机号查找用户
    User getUserByPhone(String phone);

    Integer addUser(User user);

    Integer addUserInfo(UserInfo userInfo);

    User getUserById(Long id);

    UserInfo getUserInfoByUserId(Long userId);

    Integer updateUsers(User user);

    User getUserByPhoneOrEmail(String phoneOrEmail);

    void updateUserInfo(UserInfo userInfo);

    List<UserInfo> getUserInfoByUserIds(Set<Long> userIdList);

    Integer pageCountUserInfos(Map<String, Object> param);

    List<UserInfo> pageListUserInfos(Map<String, Object> param);

    void deleteRefreshToken(@Param("refreshToken") String refreshToken,@Param("userId") Long userId);

    void addRefreshToken(@Param("refreshToken") String refreshToken,@Param("userId") Long userId,@Param("createTime") Date createTime);

    RefreshTokenDetail getRefreshTokenDetail(String refreshToken);

    List<UserInfo> batchGetUserInfoByUserIds(Set<Long> userIdList);
}
