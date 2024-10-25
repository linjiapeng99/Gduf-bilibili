package com.gduf.bilibili.dao.repository;

import com.gduf.bilibili.domain.UserInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserInfoRepository extends ElasticsearchRepository<UserInfo,Long>{

}
