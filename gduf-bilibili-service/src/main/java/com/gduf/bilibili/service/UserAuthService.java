package com.gduf.bilibili.service;

import com.gduf.bilibili.domain.auth.*;
import com.gduf.bilibili.domain.constant.AuthRoleConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserAuthService {
    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private AuthRoleService authRoleService;

    /**
     * 获取用户权限
     * @param userId
     * @return
     */
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

    /**
     * 添加用户默认角色
     * @param userId
     */
    public void addUserDefaultRole(Long userId) {
        UserRole userRole=new UserRole();
        AuthRole authRole=authRoleService.getRoleByCode(AuthRoleConstant.ROLE_LV0);
        userRole.setUserId(userId);
        userRole.setRoleId(authRole.getId());
        userRole.setCreateTime(new Date());
        userRoleService.addUserRole(userRole);
    }
}
