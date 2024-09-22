package com.gduf.bilibili.api;

import com.gduf.bilibili.domain.JsonResponse;
import com.gduf.bilibili.domain.UserFollowing;
import com.gduf.bilibili.service.UserFollowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserFollowingApi {
    @Autowired
    private UserFollowingService userFollowingService;
    @PostMapping("/user-followings")
    public JsonResponse<String>addUserFollowings(UserFollowing userFollowing){
        userFollowingService.addUserFollowings(userFollowing);
        return JsonResponse.success();
    }
}
