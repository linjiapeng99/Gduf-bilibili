package com.gduf.videoservice.dao;

import com.gduf.bilibilicommon.domain.auth.AuthRoleElementOperation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

@Mapper
public interface AuthRoleElementOperationDao {
    List<AuthRoleElementOperation> getRoleElementOperationByRoleIds(Set<Long> roleIdSet);
}
