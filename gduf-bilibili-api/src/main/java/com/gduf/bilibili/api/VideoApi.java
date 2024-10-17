package com.gduf.bilibili.api;

import com.gduf.bilibili.api.support.UserSupport;
import com.gduf.bilibili.dao.VideoDao;
import com.gduf.bilibili.domain.*;
import com.gduf.bilibili.service.VideoService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
public class VideoApi {
    @Autowired
    private UserSupport userSupport;
    @Autowired
    private VideoService videoService;


    /**
     * 添加视频
     *
     * @param video
     * @return
     */
    @PostMapping("/videos")
    public JsonResponse<String> addVideos(@RequestBody Video video) {
        Long userId = userSupport.getCurrentUserId();
        video.setUserId(userId);
        videoService.addVideos(video);
        return JsonResponse.success();
    }

    /**
     * 视频分页查询
     *
     * @param pageNo
     * @param pageSize
     * @param area
     * @return
     */
    @GetMapping("/videos")
    public JsonResponse<PageResult<Video>> pageListVideos(Integer pageNo, Integer pageSize, String area) {
        PageResult<Video> result = videoService.pageListVideos(pageNo, pageSize, area);
        return JsonResponse.success(result);
    }

    /**
     * 视频的下载
     *
     * @param request  前端请求
     * @param response 给前端的响应
     * @param path     前端传来的文件相对路径
     * @throws Exception
     */
    @GetMapping("/video-slices")
    public void viewVideoOnlineBySlices(HttpServletRequest request,
                                        HttpServletResponse response,
                                        String path) throws Exception {
        videoService.viewVideoOnlineBySlices(request, response, path);
    }

    /**
     * 添加视频点赞记录
     *
     * @param videoId
     * @return
     */
    @PostMapping("/video-likes")
    public JsonResponse<String> addVideoLike(@RequestParam Long videoId) {
        Long userId = userSupport.getCurrentUserId();
        videoService.addVideoLike(userId, videoId);
        return JsonResponse.success();
    }

    /**
     * 删除视频点赞
     * @param videoId
     * @return
     */
    @DeleteMapping("/video-likes")
    public JsonResponse<String> deleteVideoLike(@RequestParam Long videoId) {
        Long userId = userSupport.getCurrentUserId();
        videoService.deleteVideoLike(userId, videoId);
        return JsonResponse.success();
    }

    /**
     * 获取视频点赞数量
     *
     * @param videoId
     * @return
     */
    @GetMapping("/video-likes")
    public JsonResponse<Map<String, Object>> getVideoLikes(@RequestParam Long videoId) {
        Long userId = null;
        try {
            userId = userSupport.getCurrentUserId();
        } catch (Exception ignored) {
        }
        Map<String, Object> result = videoService.getVideoLikes(userId, videoId);
        return JsonResponse.success(result);
    }

    /**
     * 添加视频收藏
     *
     * @param videoCollection
     * @return
     */
    @PostMapping("/video-collections")
    public JsonResponse<String> addVideoCollection(@RequestBody VideoCollection videoCollection) {
        Long userId = userSupport.getCurrentUserId();
        videoService.addVideoCollection(userId, videoCollection);
        return JsonResponse.success();
    }

    /**
     * 取消视频收藏
     *
     * @param videoId
     * @return
     */
    @DeleteMapping("video-collections")
    public JsonResponse<String> deleteVideoCollection(@RequestParam Long videoId) {
        Long userId = userSupport.getCurrentUserId();
        videoService.deleteVideoCollection(userId, videoId);
        return JsonResponse.success();
    }

    /**
     * 获取视频收藏记录
     *
     * @param videoId
     * @return
     */
    @GetMapping("video-collections")
    public JsonResponse<Map<String, Object>> getVideoCollections(@RequestParam Long videoId) {
        Long userId = null;
        try {
            userId = userSupport.getCurrentUserId();
        } catch (Exception ignored) {
        }
        Map<String, Object> result = videoService.getVideoCollections(userId, videoId);
        return JsonResponse.success(result);
    }

    /**
     * 添加收藏分组
     *
     * @param collectionGroup
     * @return
     */
    @PostMapping("/video-collecion-groups")
    public JsonResponse<Long> addCollectionGroup(@RequestBody CollectionGroup collectionGroup) {
        Long userId = userSupport.getCurrentUserId();
        collectionGroup.setUserId(userId);
        Long groupId = videoService.addCollectionGroup(collectionGroup);
        return JsonResponse.success(groupId);
    }

    /**
     * 查询收藏分组
     *
     * @return
     */
    @GetMapping("/video-collecion-groups")
    public JsonResponse<List<CollectionGroup>> getFollowingGroups() {
        Long userId = userSupport.getCurrentUserId();
        List<CollectionGroup> list = videoService.getCollectionGroups(userId);
        return JsonResponse.success(list);
    }

    /**
     * 视频投币
     *
     * @param videoCoin
     * @return
     */
    @PostMapping("/video-coins")
    public JsonResponse<String> addVideoCoins(@RequestBody VideoCoin videoCoin) {
        Long userId = userSupport.getCurrentUserId();
        videoCoin.setUserId(userId);
        videoService.addVideoCoins(videoCoin);
        return JsonResponse.success();
    }

    /**
     * 获取视频投币数量
     *
     * @param videoId
     * @return
     */
    @GetMapping("/video-coins")
    public JsonResponse<Map<String, Object>> getVideoCoins(@RequestParam Long videoId) {
        Long userId = null;
        try {
            userId = userSupport.getCurrentUserId();
        } catch (Exception ignored) {
        }
        Map<String, Object> result = videoService.getVideoCoins(userId, videoId);
        return JsonResponse.success(result);
    }

    /**
     * 添加视频评论
     *
     * @param videoComment
     * @return
     */
    @PostMapping("/video-comments")
    public JsonResponse<String> addVideoComments(@RequestBody VideoComment videoComment) {
        Long userId = userSupport.getCurrentUserId();
        videoService.addVideoComments(videoComment, userId);
        return JsonResponse.success();
    }

    /**
     * 分页查询视频评论
     * @param pageNo
     * @param pageSize
     * @param videoId
     * @return
     */
    @GetMapping("/video-comments")
    public JsonResponse<PageResult<VideoComment>> pageListVideoComments(
            @RequestParam Integer pageNo,
            @RequestParam Integer pageSize,
            @RequestParam Long videoId) {
        PageResult<VideoComment> result = videoService.pageListVideoComments(pageNo, pageSize, videoId);
        return JsonResponse.success(result);
    }

    /**
     * 获取视频详情
     * @param videoId
     * @return
     */
    @GetMapping("/video-details")
    public JsonResponse<Map<String,Object>>getVideoDetail(@RequestParam Long videoId){
        Map<String,Object>result=videoService.getVideoDetail(videoId);
        return JsonResponse.success(result);
    }
}
