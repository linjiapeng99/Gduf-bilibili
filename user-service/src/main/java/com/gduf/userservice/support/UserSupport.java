package com.gduf.userservice.support;

import com.gduf.bilibilicommon.exception.ConditionException;
import com.gduf.bilibilicommon.util.TokenUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class UserSupport {
    public  Long getCurrentUserId(){
        //通过springboot获取前端请求
        ServletRequestAttributes requestAttributes= (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String token=requestAttributes.getRequest().getHeader("token");
        Long userId = TokenUtil.verifyToken(token);
        if(userId<0){
            throw new ConditionException("非法用户");
        }
        return userId;
    }
}
