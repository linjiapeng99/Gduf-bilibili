package com.gduf.videoservice.api;

import com.gduf.bilibilicommon.domain.Content;
import com.gduf.bilibilicommon.domain.JsonResponse;
import com.gduf.videoservice.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/video-service")
public class ContentApi {

    @Autowired
    private ContentService contentService;

    @PostMapping("/contents")
    public JsonResponse<Long> addContent(@RequestBody Content content){
        Long contentId = contentService.addContent(content);
        return new JsonResponse<>(contentId);
    }
}
