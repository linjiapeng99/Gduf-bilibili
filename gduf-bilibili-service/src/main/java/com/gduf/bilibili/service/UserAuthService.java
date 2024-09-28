package com.gduf.bilibili.service;

import com.gduf.bilibili.domain.auth.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserAuthService {
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private AuthRoleService authRoleService;

    public UserAuthorities getUserAuthrities(Long userId) {
        //查询拥有的用户角色
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
        //获取角色id
        Set<Long>roleIdSet=userRoleList.stream().map(UserRole::getRoleId).collect(Collectors.toSet());
        List<AuthRoleElementOperation>roleElementOperationList=authRoleService.getRoleElementOperationByRoleIds(roleIdSet);
        List<AuthRoleMenu>menuList=authRoleService.getRoleMenuByUserIds(roleIdSet);
        UserAuthorities userAuthorities=new UserAuthorities();
        userAuthorities.setAuthElementOperationList(roleElementOperationList);
        userAuthorities.setAuthMenuList(menuList);
        return userAuthorities;
    }
}
