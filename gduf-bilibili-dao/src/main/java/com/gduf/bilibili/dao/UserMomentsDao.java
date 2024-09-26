package com.gduf.bilibili.dao;

import com.gduf.bilibili.domain.UserMoments;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMomentsDao {

    Integer addUserMoments(UserMoments userMoments);
}
