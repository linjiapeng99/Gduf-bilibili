package com.gduf.bilibili.api.aspect;

import com.gduf.bilibili.domain.annotation.ApiLimitedRole;
import com.gduf.bilibili.api.support.UserSupport;
import com.gduf.bilibili.domain.auth.UserRole;
import com.gduf.bilibili.exception.ConditionException;
import com.gduf.bilibili.service.UserRoleService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Order(1)
@Aspect
@Component
public class ApiRoleLimitedAspect {
    @Autowired
    private UserSupport userSupport;
    @Autowired
    private UserRoleService userRoleService;
    @Pointcut("@annotation(com.gduf.bilibili.domain.annotation.ApiLimitedRole)")
    public void check(){}

    @Before("check()&& @annotation(apiLimitedRole)")
    public void doBefore(JoinPoint joinPoint, ApiLimitedRole apiLimitedRole){
        Long userId = userSupport.getCurrentUserId();
        //获取用户具备的权限
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
        //获取注解中的角色数组
        String[] limitedRoleCodeList=apiLimitedRole.limitedRoleCodeList();
        Set<String>limitedRoleCodeSet= Arrays.stream(limitedRoleCodeList).collect(Collectors.toSet());
        Set<String> roleCodeSet = userRoleList.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());
        roleCodeSet.retainAll(limitedRoleCodeSet);
        if(!roleCodeSet.isEmpty()){
            throw new ConditionException("权限不足！");
        }
    }
}