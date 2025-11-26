package com.gduf.bilibili.api;

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

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

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
     *
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
        if (result.getTotal() > 0) {
            List<UserInfo> checkFollowingUserInfoList = userFollowingService.checkFollowingStatus(result.getList(), userId);
            result.setList(checkFollowingUserInfoList);
        }
        return JsonResponse.success(result);
    }

    /**
     * 双令牌登录
     * @param user
     * @return
     */
    @PostMapping("/user-dts")
    public JsonResponse<Map<String, Object>> loginForDts(@RequestBody User user) throws Exception {
        Map<String, Object> map = userService.loginForDts(user);
        return JsonResponse.success(map);
    }

    /**
     * 用户登出，删除数据库中的刷新令牌
     * @param request
     * @return
     */
    @DeleteMapping("/refresh-tokens")
    public JsonResponse<String>logout(HttpServletRequest request){
        String refreshToken=request.getHeader("refreshToken");
        Long userId=userSupport.getCurrentUserId();
        userService.logout(refreshToken,userId);
        return JsonResponse.success();
    }

    /**
     * 刷新用户令牌
     * @param request
     * @return
     */
    @PostMapping("/access-tokens")
    public JsonResponse<String>refreshAccessToken(HttpServletRequest request) throws Exception {
        String refreshToken = request.getHeader("refreshToken");
        String accessToken=userService.refreshToken(refreshToken);
        return JsonResponse.success(accessToken);
    }
}
