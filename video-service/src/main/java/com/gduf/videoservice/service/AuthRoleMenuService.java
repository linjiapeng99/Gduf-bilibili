package com.gduf.videoservice.service;

import com.gduf.bilibilicommon.domain.auth.AuthRoleMenu;
import com.gduf.videoservice.dao.AuthRoleMenuDao;
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
