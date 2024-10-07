package com.gduf.bilibili.service;

import com.gduf.bilibili.dao.UserRoleDao;
import com.gduf.bilibili.domain.auth.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserRoleService {
    @Autowired
    private UserRoleDao userRoleDao;

    /**
     * 根据用户id获取用户角色
     * @param userId
     * @return
     */
    public List<UserRole> getUserRoleByUserId(Long userId) {
        return userRoleDao.getUserRoleByUserId(userId);
    }

    /**
     * 添加 用户（默认角色）
     * @param userRole
     */
    public void addUserRole(UserRole userRole) {
        userRoleDao.addUserRole(userRole);
    }
}
