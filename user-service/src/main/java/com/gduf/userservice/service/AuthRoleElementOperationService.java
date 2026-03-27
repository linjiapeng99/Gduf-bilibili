package com.gduf.userservice.service;

import com.gduf.bilibilicommon.domain.auth.AuthRoleElementOperation;
import com.gduf.userservice.dao.AuthRoleElementOperationDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class AuthRoleElementOperationService {
    @Autowired
    private AuthRoleElementOperationDao authRoleElementOperationDao;

    public List<AuthRoleElementOperation> getRoleElementOperationByRoleIds(Set<Long> roleIdSet) {
        return authRoleElementOperationDao.getRoleElementOperationByRoleIds(roleIdSet);
    }
}
