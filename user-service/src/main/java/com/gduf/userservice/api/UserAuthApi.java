package com.gduf.userservice.api;

import com.gduf.bilibilicommon.domain.JsonResponse;
import com.gduf.bilibilicommon.domain.auth.UserAuthorities;
import com.gduf.userservice.service.UserAuthService;
import com.gduf.userservice.support.UserSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user-service")
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