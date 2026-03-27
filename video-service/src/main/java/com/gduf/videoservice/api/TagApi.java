package com.gduf.videoservice.api;

import com.gduf.bilibilicommon.domain.JsonResponse;
import com.gduf.bilibilicommon.domain.Tag;
import com.gduf.videoservice.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/video-service")
public class TagApi {
    @Autowired
    private TagService tagService;

    /**
     * 添加标签
     * @param tag 表签
     * @return 默认
     */
    @PostMapping("tags")
    public JsonResponse<Long> addTags(@RequestBody Tag tag){
        Long tagId=tagService.addTags(tag);
        return JsonResponse.success(tagId);
    }

    /**
     * 查询表签
     * @param name 表签名
     * @return 标签列表
     */
    @GetMapping("tags")
    public JsonResponse<List<Tag>>getTags(String name){
        List<Tag>tagList=tagService.getTags(name);
        return JsonResponse.success(tagList);
    }

    @DeleteMapping("/tags")
    public JsonResponse<String>deleteTags(@RequestBody Tag tag){
        tagService.deleteTags(tag);
        return JsonResponse.success();
    }



}
