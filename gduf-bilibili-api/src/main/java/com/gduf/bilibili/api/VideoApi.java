package com.gduf.bilibili.api;

import com.gduf.bilibili.api.support.UserSupport;
import com.gduf.bilibili.domain.JsonResponse;
import com.gduf.bilibili.domain.PageResult;
import com.gduf.bilibili.domain.Video;
import com.gduf.bilibili.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class VideoApi {
    @Autowired
    private UserSupport userSupport;
    @Autowired
    private VideoService videoService;
    /**
     * 添加视频
     * @param video
     * @return
     */
    @PostMapping("/videos")
    public JsonResponse<String>addVideos(@RequestBody Video video){
        Long userId = userSupport.getCurrentUserId();
        video.setUserId(userId);
        videoService.addVideos(video);
        return JsonResponse.success();
    }

    /**
     * 视频分页查询
     * @param pageNo
     * @param pageSize
     * @param area
     * @return
     */
    @GetMapping("/videos")
    public JsonResponse<PageResult<Video>>pageListVideos(Integer pageNo,Integer pageSize,String area){
        PageResult<Video>result=videoService.pageListVideos(pageNo,pageSize,area);
        return JsonResponse.success(result);
    }
}
