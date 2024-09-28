package com.gduf.bilibili.service;

import com.gduf.bilibili.dao.AuthRoleMenuDao;
import com.gduf.bilibili.domain.auth.AuthMenu;
import com.gduf.bilibili.domain.auth.AuthRoleMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AuthRoleMenuService {
    @Autowired
    private AuthRoleMenuDao authRoleMenuDao;
    public List<AuthRoleMenu> getRoleMenuByUserIds(Set<Long> roleIdSet) {
        return authRoleMenuDao.getRoleMenuByUserIds(roleIdSet);
    }
}
