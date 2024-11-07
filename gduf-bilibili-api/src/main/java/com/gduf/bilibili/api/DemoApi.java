package com.gduf.bilibili.api;

import com.gduf.bilibili.domain.JsonResponse;
import com.gduf.bilibili.domain.Video;
import com.gduf.bilibili.service.DemoService;
import com.gduf.bilibili.service.ElasticSearchService;
import com.gduf.bilibili.service.feign.MsDeclareService;
import com.gduf.bilibili.service.util.FastDFSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
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

    @Autowired
    private MsDeclareService msDeclareService;
    @GetMapping("/query")
    public Long getById(Long id){
       return demoService.query(id);
    }

    /**
     * 文件分片
     * @param file
     * @return
     * @throws
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
    @GetMapping("/demos")
    public Long msget(@RequestParam Long id){
        return id;
    }
    @PostMapping("/demos")
    public Map<String,Object>mspost(@RequestBody Map<String,Object>params){
        return params;
    }
}











