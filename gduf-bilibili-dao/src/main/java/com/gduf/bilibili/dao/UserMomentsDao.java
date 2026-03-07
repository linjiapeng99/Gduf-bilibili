package com.gduf.bilibili.dao;

import com.gduf.bilibili.domain.UserMoments;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface UserMomentsDao {

    Integer addUserMoments(UserMoments userMoments);

    Integer pageCountMoments(Map<String, Object> params);

    List<UserMoments> pageListMoments(Map<String, Object> params);
}
