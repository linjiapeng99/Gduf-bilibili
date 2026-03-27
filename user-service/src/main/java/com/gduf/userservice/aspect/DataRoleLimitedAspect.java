package com.gduf.userservice.aspect;

import com.gduf.bilibilicommon.domain.UserMoments;
import com.gduf.bilibilicommon.domain.auth.UserRole;
import com.gduf.bilibilicommon.domain.constant.AuthRoleConstant;
import com.gduf.bilibilicommon.exception.ConditionException;
import com.gduf.userservice.service.UserRoleService;
import com.gduf.userservice.support.UserSupport;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Order(1)
@Aspect
@Component
public class DataRoleLimitedAspect {
    @Autowired
    private UserSupport userSupport;
    @Autowired
    private UserRoleService userRoleService;

    @Pointcut("@annotation(com.gduf.bilibilicommon.domain.annotation.DataLimitedRole)")
    public void check() {
    }

    @Before("check()")
    public void doBefore(JoinPoint joinPoint) {
        Long userId = userSupport.getCurrentUserId();
        //获取用户角色
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
        Set<String> roleCodeSet = userRoleList.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof UserMoments) {
                UserMoments userMoments = (UserMoments) arg;
                String type = userMoments.getType();
                if(roleCodeSet.contains(AuthRoleConstant.ROLE_LV0) &&!"0".equals(type)){
                    throw new ConditionException("参数异常！");
                }
            }
        }
    }
}
