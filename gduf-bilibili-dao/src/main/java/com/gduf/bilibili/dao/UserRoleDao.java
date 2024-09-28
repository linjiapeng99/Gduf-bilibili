package com.gduf.bilibili.dao;

import com.gduf.bilibili.domain.auth.UserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserRoleDao {
    List<UserRole> getUserRoleByUserId(Long userId);
}
