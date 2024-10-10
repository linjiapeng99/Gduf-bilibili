package com.gduf.bilibili.api;

import com.gduf.bilibili.domain.JsonResponse;
import com.gduf.bilibili.service.DemoService;
import com.gduf.bilibili.service.util.FastDFSUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
public class DemoApi {
    @Autowired
    private DemoService demoService;

    @Autowired
    private FastDFSUtil fastDFSUtil;
    @GetMapping("/query")
    public Long getById(Long id){
       return demoService.query(id);
    }

    @GetMapping("/slices")
    public JsonResponse<String>slices(MultipartFile file) throws Exception {
        fastDFSUtil.convertFileToSlices(file);
        return JsonResponse.success();
    }
}
