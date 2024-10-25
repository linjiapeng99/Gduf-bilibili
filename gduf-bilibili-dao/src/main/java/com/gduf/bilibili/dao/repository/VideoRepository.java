package com.gduf.bilibili.dao.repository;
import com.gduf.bilibili.domain.Video;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface VideoRepository extends ElasticsearchRepository<Video,Long> {

    List<Video> findByTitleLike(String keyword);
}
