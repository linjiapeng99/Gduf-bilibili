package com.gduf.userservice.api;

import com.gduf.bilibilicommon.domain.JsonResponse;
import com.gduf.bilibilicommon.domain.PageResult;
import com.gduf.bilibilicommon.domain.UserMoments;
import com.gduf.bilibilicommon.domain.annotation.ApiLimitedRole;
import com.gduf.bilibilicommon.domain.annotation.DataLimitedRole;
import com.gduf.bilibilicommon.domain.constant.AuthRoleConstant;
import com.gduf.userservice.service.UserMomentsService;
import com.gduf.userservice.support.UserSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-service")
public class UserMomentsApi {
    @Autowired
    private UserMomentsService userMomentsService;
    @Autowired
    private UserSupport userSupport;

    /**
     * 添加用户动态
     *
     * @param userMoments
     * @return
     * @throws Exception
     */
    //limitedRoleCodeList = {AuthRoleConstant.ROLE_LV0}在这个列表中的角色不能访问这个接口
    //@ApiLimitedRole(limitedRoleCodeList = {AuthRoleConstant.ROLE_LV0})
    //@DataLimitedRole
    @PostMapping("/user-moments")
    public JsonResponse<String> addUserMoments(@RequestBody UserMoments userMoments) throws Exception {
        Long userId = userSupport.getCurrentUserId();
        userMoments.setUserId(userId);
        userMomentsService.addUserMoments(userMoments);
        return JsonResponse.success();
    }

    /**
     * 获取用户动态
     *
     * @return
     */
    @GetMapping("/user-subscribed-moments")
    public JsonResponse<List<UserMoments>> getUserSubscribedMoments() {
        Long userId = userSupport.getCurrentUserId();
        List<UserMoments> list = userMomentsService.getUserSubscribedMoments(userId);
        return JsonResponse.success(list);
    }

    @GetMapping("/moments")
    public JsonResponse<PageResult<UserMoments>> pageListMoments(@RequestParam("size") Integer size,
                                                                 @RequestParam("no") Integer no,
                                                                 String type){
        Long userId = userSupport.getCurrentUserId();
        PageResult<UserMoments> list = userMomentsService.pageListMoments(size, no,
                userId, type);
        return new JsonResponse<>(list);
    }
}
