package com.gduf.bilibili.api;

import com.gduf.bilibili.domain.JsonResponse;
import com.gduf.bilibili.service.ElasticSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class SystemApi {
    @Autowired
    private ElasticSearchService elasticSearchService;
    @GetMapping("/contents")
    public JsonResponse<List<Map<String, Object>>> getContent(@RequestParam String keyword,@RequestParam Integer pageNo,@RequestParam Integer pageSize) throws IOException {
        List<Map<String, Object>> list = elasticSearchService.getContents(keyword, pageNo, pageSize);
        return JsonResponse.success(list);
    }
}
