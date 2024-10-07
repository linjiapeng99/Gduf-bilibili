package com.gduf.bilibili.api;

import com.gduf.bilibili.api.support.UserSupport;
import com.gduf.bilibili.domain.JsonResponse;
import com.gduf.bilibili.domain.auth.UserAuthorities;
import com.gduf.bilibili.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserAuthApi {
    @Autowired
    private UserSupport userSupport;
    @Autowired
    private UserAuthService userAuthService;

    /**
     *
     * 获取角色权限
     * @return
     */
    @GetMapping("/user-authrities")
    public JsonResponse<UserAuthorities> getUserAuthrities() {
        Long userId = userSupport.getCurrentUserId();
        UserAuthorities userAuthorities = userAuthService.getUserAuthrities(userId);
        return JsonResponse.success(userAuthorities);
    }
}
