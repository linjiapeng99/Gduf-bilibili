package com.gduf.bilibili.dao;

import com.gduf.bilibili.domain.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Mapper
public interface VideoDao {

    Integer addVideos(Video video);

    Integer batchAddVideoTags(List<VideoTag> tagList);

    Integer pageCountVideo(Map<String, Object> params);

    List<Video> pageListVideos(Map<String, Object> params);

    List<VideoTag> getVideoTagsByVideoId(Long videoId);

    Video getVideoById(Long id);

    VideoLike getVideoLikeByVideoIdAndUserId(@Param("userId") Long userId, @Param("videoId") Long videoId);

    Integer addVideoLike(VideoLike videoLike);

    Integer deleteVideoLike(@Param("userId") Long userId, @Param("videoId") Long videoId);

    Long getVideoLikesByVideoId(Long videoId);

    Integer deleteVideoCollection(@Param("userId") Long userId, @Param("videoId") Long videoId);

    Integer addVideoCollection(VideoCollection videoCollection);

    Long getVideoCollectionsByVideoId(Long videoId);

    VideoCollection getVideoCollectionByVideoIdAndUserId(@Param("userId") Long userId, @Param("videoId") Long videoId);

    VideoCoin getVideoCoinByVideoIdAndUserId(@Param("userId") Long userId, @Param("videoId") Long videoId);

    Integer addVideoCoins(VideoCoin videoCoin);

    Integer updateVideoCoins(VideoCoin videoCoin);

    Long getVideoCoinAmount(Long videoId);

    Integer addVideoComments(VideoComment videoComment);

    Integer pageCountVideoComments(Map<String, Object> params);

    List<VideoComment> pageListVideoComments(Map<String, Object> params);

    List<VideoComment> batchGetVideoCommentsByRootIds(@Param("parentIds")List<Long> parentIds);

    VideoView getVideoView(Map<String, Object> params);

    void addVideoView(VideoView videoView);

    Integer getVideoCounts(Long videoId);

    List<UserPreference> getAllUserPreference();

    List<Video> batchVideosByIds(List<Long> idList);

    List<VideoDanmuCount> getVideoDanmuCountByVideoIds(Set<Long> videoIds);

    List<VideoViewCount> getVideoViewCountByVideoIds(Set<Long> videoIds);

    Integer batchAddBinaryPictures(List<VideoBinaryPicture> pictures);

    void updateVideoCollection(VideoCollection videoCollection);
}
