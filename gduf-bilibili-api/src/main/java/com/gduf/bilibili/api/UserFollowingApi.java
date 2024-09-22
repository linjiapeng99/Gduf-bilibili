package com.gduf.bilibili.api;

import com.gduf.bilibili.domain.FollowingGroup;
import com.gduf.bilibili.domain.JsonResponse;
import com.gduf.bilibili.domain.UserFollowing;
import com.gduf.bilibili.service.UserFollowingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserFollowingApi {
    @Autowired
    private UserFollowingService userFollowingService;

    /**
     * 新增关注记录
     * @param userFollowing
     * @return
     */
    @PostMapping("/user-followings")
    public JsonResponse<String>addUserFollowings(@RequestBody UserFollowing userFollowing){
        userFollowingService.addUserFollowings(userFollowing);
        return JsonResponse.success();
    }

    /**
     * 查询关注列表
     * @param userId
     * @return
     */
    @GetMapping("/user-followings")
    public JsonResponse<List<FollowingGroup>>getUserFollowings(Long userId){
        List<FollowingGroup> userFollowings = userFollowingService.getUserFollowings(userId);
        return JsonResponse.success(userFollowings);
    }
}
