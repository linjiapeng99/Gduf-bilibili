package com.gduf.bilibili.service.handler;

import com.gduf.bilibili.domain.JsonResponse;
import com.gduf.bilibili.exception.ConditionException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)//全局优先
public class CommonGlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    @ResponseBody//返回类型是responseBody的
    public JsonResponse<String>commonExceptionHandler(HttpServletRequest request,Exception e){
        String errorMsg=e.getMessage();
        if(e instanceof ConditionException){
            String errorCode=((ConditionException)(e)).getCode();
            return new JsonResponse<>(errorCode,errorMsg);
        }else {
            return new JsonResponse<>("500",errorMsg);
        }
    }
}
