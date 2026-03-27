package com.gduf.userservice.api;

import com.gduf.bilibilicommon.domain.JsonResponse;
import com.gduf.bilibilicommon.domain.PageResult;
import com.gduf.bilibilicommon.domain.UserVideoHistory;
import com.gduf.userservice.service.UserHistoryService;
import com.gduf.userservice.support.UserSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user-service")
public class UserHistoryApi {


    @Autowired
    private UserHistoryService userHistoryService;

    @Autowired
    private UserSupport userSupport;

    @GetMapping("/user-video-histories")
    public JsonResponse<PageResult<UserVideoHistory>> pagListUserVideoHistory(@RequestParam Integer size,
                                                                              @RequestParam Integer no){
        Long userId = userSupport.getCurrentUserId();
        PageResult<UserVideoHistory> result = userHistoryService.pagListUserVideoHistory(size,no,userId);
        return new JsonResponse<>(result);
    }

}
