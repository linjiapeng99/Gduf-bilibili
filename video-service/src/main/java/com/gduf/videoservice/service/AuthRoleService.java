package com.gduf.videoservice.service;

import com.gduf.bilibilicommon.domain.auth.AuthRole;
import com.gduf.bilibilicommon.domain.auth.AuthRoleElementOperation;
import com.gduf.bilibilicommon.domain.auth.AuthRoleMenu;
import com.gduf.videoservice.dao.AuthRoleDao;
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
    @Autowired
    private AuthRoleDao authRoleDao;

    public List<AuthRoleElementOperation> getRoleElementOperationByRoleIds(Set<Long> roleIdSet) {
        return authRoleElementOperationService.getRoleElementOperationByRoleIds(roleIdSet);
    }

    public List<AuthRoleMenu> getRoleMenuByUserIds(Set<Long> roleIdSet) {
        return authRoleMenuService.getRoleMenuByUserIds(roleIdSet);
    }

    public AuthRole getRoleByCode(String code) {
        return authRoleDao.getRoleByCode(code);
    }
}
