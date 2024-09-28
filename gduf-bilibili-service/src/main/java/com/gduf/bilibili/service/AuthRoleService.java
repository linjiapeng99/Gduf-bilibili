package com.gduf.bilibili.service;

import com.gduf.bilibili.domain.auth.AuthMenu;
import com.gduf.bilibili.domain.auth.AuthRoleElementOperation;
import com.gduf.bilibili.domain.auth.AuthRoleMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AuthRoleService {
    @Autowired
    private AuthRoleElementOperationService authRoleElementOperationService;
    @Autowired
    private AuthRoleMenuService authRoleMenuService;

    public List<AuthRoleElementOperation> getRoleElementOperationByRoleIds(Set<Long> roleIdSet) {
        return authRoleElementOperationService.getRoleElementOperationByRoleIds(roleIdSet);
    }

    public List<AuthRoleMenu> getRoleMenuByUserIds(Set<Long> roleIdSet) {
        return authRoleMenuService.getRoleMenuByUserIds(roleIdSet);
    }
}
