package com.gduf.bilibili.api;

import com.gduf.bilibili.domain.JsonResponse;
import com.gduf.bilibili.domain.Video;
import com.gduf.bilibili.service.DemoService;
import com.gduf.bilibili.service.ElasticSearchService;
import com.gduf.bilibili.service.util.FastDFSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
public class DemoApi {
    @Autowired
    private DemoService demoService;

    @Autowired
    private FastDFSUtil fastDFSUtil;

    @Autowired
    private ElasticSearchService elasticSearchService;
    @GetMapping("/query")
    public Long getById(Long id){
       return demoService.query(id);
    }

    /**
     * 文件分片
     * @param file
     * @return
     * @throws Exception
     */
    @GetMapping("/slices")
    public JsonResponse<String>slices(MultipartFile file) throws Exception {
        fastDFSUtil.convertFileToSlices(file);
        return JsonResponse.success();
    }

    /**
     * 使用es查询视频
     * @param keyword
     * @return
     */
    @GetMapping("/es-videos")
    public JsonResponse<List<Video>>getVideos(@RequestParam String keyword){
        List<Video> videoList = elasticSearchService.getVideos(keyword);
        return JsonResponse.success(videoList);
    }
}











