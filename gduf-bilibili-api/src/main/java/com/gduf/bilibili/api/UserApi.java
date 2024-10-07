package com.gduf.bilibili.api;

import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import com.alibaba.fastjson.JSONObject;
import com.gduf.bilibili.api.support.UserSupport;
import com.gduf.bilibili.domain.JsonResponse;
import com.gduf.bilibili.domain.PageResult;
import com.gduf.bilibili.domain.User;
import com.gduf.bilibili.domain.UserInfo;
import com.gduf.bilibili.service.UserFollowingService;
import com.gduf.bilibili.service.UserService;
import com.gduf.bilibili.service.util.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserApi {
    @Autowired
    private UserService userService;
    @Autowired
    private UserSupport userSupport;
    @Autowired
    private UserFollowingService userFollowingService;

    /**
     * 用来返回公钥的
     *
     * @return
     */
    @GetMapping("/rsa-pks")
    public JsonResponse<String> getRsaPublicKey() {
        String pk = RSAUtil.getPublicKeyStr();
        return JsonResponse.success(pk);
    }

    /**
     * 用户注册
     *
     * @param user
     * @return
     */
    @PostMapping("/users")
    public JsonResponse<String> addUser(@RequestBody User user) {
        userService.addUser(user);
        return JsonResponse.success();
    }

    /**
     * 用户登录
     *
     * @param user
     * @return
     * @throws Exception
     */
    @PostMapping("/user-tokens")
    public JsonResponse<String> login(@RequestBody User user) throws Exception {
        String token = userService.login(user);
        return JsonResponse.success(token);

    }

    /**
     * 获取用户信息
     *
     * @return
     */
    @GetMapping("/users")
    public JsonResponse<User> getUserInfo() {
        Long userId = userSupport.getCurrentUserId();
        User user = userService.getUserInfo(userId);
        return JsonResponse.success(user);
    }

    /**
     * 更新用户信息
     *
     * @param user
     * @return
     */
    @PutMapping("/users")
    public JsonResponse<String> updateUsers(@RequestBody User user) throws Exception {
        Long userId = userSupport.getCurrentUserId();
        user.setId(userId);
        userService.updateUsers(user);
        return JsonResponse.success();
    }

    /**
     * 更新用户账户信息
     *
     * @param userInfo
     * @return
     */
    @PutMapping("/user-infos")
    public JsonResponse<String> updateUserInfo(@RequestBody UserInfo userInfo) {
        Long userId = userSupport.getCurrentUserId();
        userInfo.setUserId(userId);
        userService.updateUserInfo(userInfo);
        return JsonResponse.success();
    }

    /**
     * 获取用户信息
     * @param pageNum
     * @param pageSize
     * @param nick
     * @return
     */
    @GetMapping("/user-infos")
    public JsonResponse<PageResult<UserInfo>> pageListUserInfos(@RequestParam Integer pageNum, @RequestParam Integer pageSize, String nick) {
        Long userId = userSupport.getCurrentUserId();
        JSONObject param = new JSONObject();
        param.put("userId", userId);
        param.put("pageNum", pageNum);
        param.put("pageSize", pageSize);
        param.put("nick", nick);
        PageResult<UserInfo> result = userService.pageListUserInfos(param);
        if(result.getTotal()>0){
           List<UserInfo>checkFollowingUserInfoList= userFollowingService.checkFollowingStatus(result.getList(),userId);
           result.setList(checkFollowingUserInfoList);
        }
        return JsonResponse.success(result);
    }
}
