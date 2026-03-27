package com.gduf.videoservice.api;

import com.gduf.bilibilicommon.domain.JsonResponse;
import com.gduf.bilibilicommon.domain.UserInfo;
import com.gduf.bilibilicommon.domain.Video;
import com.gduf.videoservice.service.DemoService;
import com.gduf.videoservice.service.ElasticSearchService;
import com.gduf.videoservice.util.FastDFSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/video-service")
public class DemoApi {
    @Autowired
    private DemoService demoService;

    @Autowired
    private FastDFSUtil fastDFSUtil;

    @Autowired
    private ElasticSearchService elasticSearchService;
    @GetMapping("/query")
    public Long getById(Long id) {
        return demoService.query(id);
    }

    /**
     * 文件分片
     *
     * @param file
     * @return
     * @throws
     */
    @GetMapping("/slices")
    public JsonResponse<String> slices(MultipartFile file) throws Exception {
        fastDFSUtil.convertFileToSlices(file);
        return JsonResponse.success();
    }

    /**
     * 使用es查询视频
     *
     * @param keyword
     * @return
     */
    @GetMapping("/es-videos")
    public JsonResponse<List<Video>> getVideos(@RequestParam String keyword) {
        List<Video> videoList = elasticSearchService.getVideos(keyword);
        return JsonResponse.success(videoList);
    }

    @PostMapping("/es-videos")
    public JsonResponse<String> addVideos(@RequestBody Video video) {
        elasticSearchService.addVideo(video);
        return JsonResponse.success();
    }

    @DeleteMapping("/es-videos")
    public JsonResponse<String> deleteVideos() {
        elasticSearchService.deleteAllVideos();
        return JsonResponse.success();
    }

    @PostMapping("/es-users")
    public JsonResponse<String> addUsers(@RequestBody UserInfo userInfo) {
        elasticSearchService.addUserInfo(userInfo);
        return JsonResponse.success();
    }
}

/*
 * 熔断降级在接口中编写两个方法，一个是主要接口，
 * 另一个是当主接口出现错误的时候，采取熔断降级的方法调用下一个方法
 * 这里是本服务去调用ms服务里的接口，ms服务的接口传入参数是一个时间，里面会根据时间进行睡眠
 * 而本服务的熔断降级策略是当超过两秒即刻采取熔断降级策略，会调用error方法
 * */
