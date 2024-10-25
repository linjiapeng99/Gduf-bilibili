package com.gduf.bilibili.service;

import com.gduf.bilibili.dao.repository.UserInfoRepository;
import com.gduf.bilibili.dao.repository.VideoRepository;
import com.gduf.bilibili.domain.UserInfo;
import com.gduf.bilibili.domain.Video;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.directory.SearchResult;
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
    public void addUserInfo(UserInfo userInfo){
        userInfoRepository.save(userInfo);
    }

    public List<Map<String ,Object>>getContents(String keyword,Integer pageNo,Integer pageSize) throws IOException {
        String[] indices ={"videos","user-infos"};
        SearchRequest searchRequest=new SearchRequest(indices);
        //构建请求条件
        SearchSourceBuilder searchSourceBuilder=new SearchSourceBuilder();
        searchSourceBuilder.from(pageNo-1);
        searchSourceBuilder.size(pageSize);
        //构建二级多条件查询条件
        MultiMatchQueryBuilder multiMatchQueryBuilder= QueryBuilders.multiMatchQuery(keyword,"title","nick","description");
        searchSourceBuilder.query(multiMatchQueryBuilder);
        searchRequest.source(searchSourceBuilder);
        //添加请求超时限制
        searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        //高亮显示
        String[] array={"title","nick","description"};
        HighlightBuilder highlightBuilder=new HighlightBuilder();
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
        List<Map<String,Object>> arrayList=new ArrayList<>();
        for (SearchHit hit : response.getHits()) {
            //处理高亮字段
            Map<String, HighlightField> highLightBuilderFilds = hit.getHighlightFields();
            Map<String,Object>sourceMap=hit.getSourceAsMap();
            for (String key : array) {
                HighlightField highlightField = highLightBuilderFilds.get(key);
                if(highlightField!=null){
                    Text[] fragments =   highlightField.fragments();
                    String str= Arrays.toString(fragments);
                    str=str.substring(1,str.length()-1);
                    sourceMap.put(key,str);
                }
            }
            arrayList.add(sourceMap);
        }
        return arrayList;
    }

    public void addVideo(Video video){
        videoRepository.save(video);
    }

    public List<Video>  getVideos(String keyword){
       return  videoRepository.findByTitleLike(keyword);
    }
}
