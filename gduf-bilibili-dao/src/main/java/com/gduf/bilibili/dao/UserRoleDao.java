package com.gduf.bilibili.dao;

import com.gduf.bilibili.domain.auth.UserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserRoleDao {
    /**
     * 根据用户id获取用户角色
     * @param userId
     * @return
     */
    List<UserRole> getUserRoleByUserId(Long userId);

    /**
     * 添加用户角色
     * @param userRole
     */
    void addUserRole(UserRole userRole);
}
