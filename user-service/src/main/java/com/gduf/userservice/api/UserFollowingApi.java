package com.gduf.userservice.api;

import com.gduf.bilibilicommon.domain.FollowingGroup;
import com.gduf.bilibilicommon.domain.JsonResponse;
import com.gduf.bilibilicommon.domain.UserFollowing;
import com.gduf.userservice.service.UserFollowingService;
import com.gduf.userservice.support.UserSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-service")
public class UserFollowingApi {
    @Autowired
    private UserFollowingService userFollowingService;

    @Autowired
    private UserSupport userSupport;

    /**
     * 新增关注记录
     *
     * @param userFollowing
     * @return
     */
    @PostMapping("/user-followings")
    public JsonResponse<String> addUserFollowings(@RequestBody UserFollowing userFollowing) {
        Long userId = userSupport.getCurrentUserId();
        userFollowing.setUserId(userId);
        userFollowingService.addUserFollowings(userFollowing);
        return JsonResponse.success();
    }

    /**
     * 查询关注列表
     *
     * @return
     */
    @GetMapping("/user-followings")
    public JsonResponse<List<FollowingGroup>> getUserFollowings() {
        Long userId = userSupport.getCurrentUserId();
        List<FollowingGroup> userFollowings = userFollowingService.getUserFollowings(userId);
        return JsonResponse.success(userFollowings);
    }

    @PutMapping("/user-followings")
    public JsonResponse<String> updateUserFollowings(@RequestBody UserFollowing userFollowing){
        Long userId = userSupport.getCurrentUserId();
        userFollowing.setUserId(userId);
        userFollowingService.updateUserFollowings(userFollowing);
        return JsonResponse.success();
    }
    //新增取消关注接口
    @DeleteMapping("/user-followings")
    public JsonResponse<String> deleteUserFollowing(@RequestParam Long followingId){
        Long userId = userSupport.getCurrentUserId();
        userFollowingService.deleteUserFollowing(userId, followingId);
        return JsonResponse.success();
    }

    /**
     * 获取粉丝列表
     *
     * @return
     */
    @GetMapping("/user-fans")
    public JsonResponse<List<UserFollowing>> getUserFans() {
        Long userId = userSupport.getCurrentUserId();
        List<UserFollowing> userFans = userFollowingService.getUserFans(userId);
        return JsonResponse.success(userFans);
    }

    /**
     * 添加关注分类分组
     * @param followingGroup
     * @return
     */
    @PostMapping("/user-following-groups")
    public JsonResponse<Long> addFollowingGroups(@RequestBody FollowingGroup followingGroup) {
        Long userId = userSupport.getCurrentUserId();
        followingGroup.setUserId(userId);
        Long groupId = userFollowingService.addUserFollowingGroups(followingGroup);
        return JsonResponse.success(groupId);
    }

    /**
     * 查询关注分类分组
     * @return
     */
    @GetMapping("/user-following-groups")
    public JsonResponse<List<FollowingGroup>> getFollowingGroups() {
        Long userId = userSupport.getCurrentUserId();
        List<FollowingGroup> list = userFollowingService.getUserFollowingGroups(userId);
        return JsonResponse.success(list);
    }
}
