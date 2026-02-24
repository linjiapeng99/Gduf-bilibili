package com.gduf.bilibili.api;

import com.gduf.bilibili.api.support.UserSupport;
import com.gduf.bilibili.domain.Danmu;
import com.gduf.bilibili.domain.JsonResponse;
import com.gduf.bilibili.service.DanmuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class DanmuApi {
    @Autowired
    private DanmuService danmuService;
    @Autowired
    private UserSupport userSupport;

    /**
     * 获取视频弹幕
     * @param videoId 视频id
     * @param startTime  弹幕创建时间
     * @param endTime    弹幕截止时间
     * @return
     */
    @GetMapping("/danmus")
    public JsonResponse<List<Danmu>>getDanmus(@RequestParam Long videoId, String startTime, String endTime) throws Exception {
        List<Danmu>list;
        try{
            //判断当前是游客模式还是用户模式，报错走catch逻辑则是游客模式
            userSupport.getCurrentUserId();
            list=danmuService.getDanmus(videoId,startTime,endTime);
        }catch (Exception e){
            list=danmuService.getDanmus(videoId,null,null);
        }
        return JsonResponse.success(list);
    }
}
