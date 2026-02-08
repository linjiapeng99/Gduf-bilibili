package com.gduf.bilibili.api;

import com.gduf.bilibili.domain.JsonResponse;
import com.gduf.bilibili.domain.Video;
import com.gduf.bilibili.service.DemoService;
import com.gduf.bilibili.service.ElasticSearchService;
//import com.gduf.bilibili.service.feign.MsDeclareService;
import com.gduf.bilibili.service.util.FastDFSUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
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

    /*@Autowired
    private MsDeclareService msDeclareService;*/
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
    /*@GetMapping("/demos")
    public Long msget(@RequestParam Long id){
        return msDeclareService.msget(id);
    }*/
    /*@PostMapping("/demos")
    public Map<String,Object>mspost(@RequestBody Map<String,Object>params){
        return msDeclareService.mspost(params);
    }

    @HystrixCommand(fallbackMethod = "error", commandProperties = {
            @HystrixProperty(
                    name="execution.isolation.thread.timeoutInMilliseconds",
                    value = "2000"
            )
    })
    @GetMapping("/timeout")
    public String circuitBreakerWithHystrix(@RequestParam Long time){
        return msDeclareService.timeout(time);
    }

    public String error(Long time){
        return "超时出错";
    }*/
}

/*
* 熔断降级在接口中编写两个方法，一个是主要接口，
* 另一个是当主接口出现错误的时候，采取熔断降级的方法调用下一个方法
* 这里是本服务去调用ms服务里的接口，ms服务的接口传入参数是一个时间，里面会根据时间进行睡眠
* 而本服务的熔断降级策略是当超过两秒即刻采取熔断降级策略，会调用error方法
*
*
*
*
* */
