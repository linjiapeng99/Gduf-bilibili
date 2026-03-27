package com.gduf.danmuservice.service;
import com.gduf.bilibilicommon.domain.UserInfo;
import com.gduf.bilibilicommon.domain.Video;
import com.gduf.bilibilicommon.domain.constant.SearchConstant;
import com.gduf.danmuservice.dao.repository.UserInfoRepository;
import com.gduf.danmuservice.dao.repository.VideoRepository;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
public class ElasticSearchService {
    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public void addUserInfo(UserInfo userInfo) {
        userInfoRepository.save(userInfo);
    }
    public void updateUserInfo(UserInfo userInfo) {
        userInfoRepository.save(userInfo);
    }
    public List<Map<String, Object>> getContents(String keyword, Integer pageNo, Integer pageSize) throws IOException {
        String[] indices = {"videos", "user-infos"};
        SearchRequest searchRequest = new SearchRequest(indices);
        //构建请求条件
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.from(pageNo - 1);
        searchSourceBuilder.size(pageSize);
        //构建二级多条件查询条件
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(keyword, "title", "nick", "description");
        searchSourceBuilder.query(multiMatchQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        //添加请求超时限制
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        //高亮显示
        String[] array = {"title", "nick", "description"};
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        for (String key : array) {
            highlightBuilder.fields().add(new HighlightBuilder.Field(key));
        }
        highlightBuilder.requireFieldMatch(false);//多个字段高亮的时候需要设置该字段为false
        highlightBuilder.preTags("<span style=\"color:red\">");
        highlightBuilder.postTags("</span>");
        searchSourceBuilder.highlighter(highlightBuilder);
        //发送请求
        SearchResponse response = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        //处理响应结果
        List<Map<String, Object>> arrayList = new ArrayList<>();
        for (SearchHit hit : response.getHits()) {
            //处理高亮字段
            Map<String, HighlightField> highLightBuilderFilds = hit.getHighlightFields();
            Map<String, Object> sourceMap = hit.getSourceAsMap();
            for (String key : array) {
                HighlightField highlightField = highLightBuilderFilds.get(key);
                if (highlightField != null) {
                    Text[] fragments = highlightField.fragments();
                    String str = Arrays.toString(fragments);
                    str = str.substring(1, str.length() - 1);
                    sourceMap.put(key, str);
                }
            }
            arrayList.add(sourceMap);
        }
        return arrayList;
    }

    public void addVideo(Video video) {
        videoRepository.save(video);
    }

    public List<Video> getVideos(String keyword) {
        return videoRepository.findByTitleLike(keyword);
    }

    public void deleteAllVideos() {
        videoRepository.deleteAll();
    }

    public long countVideoBySearchTxt(String searchTxt) {
        return this.videoRepository.countByTitleOrDescription(searchTxt, searchTxt);
    }

    public long countUserBySearchTxt(String searchTxt) {
        return this.userInfoRepository.countByNick(searchTxt);
    }

    public void updateVideoViewCount(Long videoId) {
        Optional<Video> videoOpt = videoRepository.findById(videoId);
        if (videoOpt.isPresent()) {
            Video video = videoOpt.get();
            int viewCount = video.getViewCount() == null ? 0 : video.getViewCount();
            video.setViewCount(viewCount + 1);
            videoRepository.save(video);
        }
    }
    public void updateVideoDanmuCount(Long videoId) {
        Optional<Video> videoOpt = videoRepository.findById(videoId);
        if(videoOpt.isPresent()){
            Video video = videoOpt.get();
            int danmuCount = video.getDanmuCount() == null? 0 : video.getDanmuCount();
            video.setDanmuCount(danmuCount+1);
            videoRepository.save(video);
        }
    }

    public Page<Video> pageListSearchVideos(String keyword, Integer pageSize,
                                            Integer pageNo, String searchType) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        if (SearchConstant.DEFAULT.equals(searchType)
                || SearchConstant.VIEW_COUNT.equals(searchType)) {
            return videoRepository.findByTitleOrDescriptionOrderByViewCountDesc(keyword, keyword, pageRequest);
        } else if (SearchConstant.CREATE_TIME.equals(searchType)) {
            return videoRepository.findByTitleOrDescriptionOrderByCreateTimeDesc(keyword, keyword, pageRequest);
        } else if (SearchConstant.DANMU_COUNT.equals(searchType)) {
            return videoRepository.findByTitleOrDescriptionOrderByDanmuCountDesc(keyword, keyword, pageRequest);
        } else {
            return videoRepository.findByTitleOrDescriptionOrderByViewCountDesc(keyword, keyword, pageRequest);
        }
    }

    public Page<UserInfo> pageListSearchUsers(String keyword, Integer pageSize,
                                              Integer pageNo, String searchType) {
        PageRequest pageRequest = PageRequest.of(pageNo, pageSize);
        if (SearchConstant.USER_DEFAULT.equals(searchType)
                || SearchConstant.USER_FAN_COUNT_DESC.equals(searchType)) {
            return userInfoRepository.findByNickOrderByFanCountDesc(keyword, pageRequest);
        } else if (SearchConstant.USER_FAN_COUNT_ASC.equals(searchType)) {
            return userInfoRepository.findByNickOrderByFanCountAsc(keyword, pageRequest);
        } else {
            return userInfoRepository.findByNickOrderByFanCountDesc(keyword, pageRequest);
        }
    }
}
