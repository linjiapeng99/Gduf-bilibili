package com.gduf.bilibili.api;

import com.gduf.bilibili.api.support.UserSupport;
import com.gduf.bilibili.domain.JsonResponse;
import com.gduf.bilibili.domain.UserMoments;
import com.gduf.bilibili.service.UserMomentsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserMomentsApi {
    @Autowired
    private UserMomentsService userMomentsService;
    @Autowired
    private UserSupport userSupport;
    public JsonResponse<String>addUserMoments(@RequestBody UserMoments userMoments) throws Exception {
        Long userId = userSupport.getCurrentUserId();
        userMoments.setUserId(userId);
        userMomentsService.addUserMoments(userMoments);
        return JsonResponse.success();
    }
}
