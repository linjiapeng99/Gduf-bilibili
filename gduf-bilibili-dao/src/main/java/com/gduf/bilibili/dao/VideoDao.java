package com.gduf.bilibili.dao;

import com.gduf.bilibili.domain.Video;
import com.gduf.bilibili.domain.VideoTag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface VideoDao {

    Integer addVideos(Video video);

    Integer batchAddVideoTags(List<VideoTag> tagList);

    Integer pageCountVideo(Map<String, Object> params);

    List<Video> pageListVideos(Map<String,Object> params);

    List<VideoTag> getVideoTagsByVideoId(Long videoId);
}
