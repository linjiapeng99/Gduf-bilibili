package com.gduf.bilibili.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface UserCoinDao {

    Integer getUserCoinAmount(Long userId);

    Integer updateUserCoinAmount(@Param("userId") Long userId,@Param("amount")Integer amount,@Param("updateTime") Date updateTime);

}
