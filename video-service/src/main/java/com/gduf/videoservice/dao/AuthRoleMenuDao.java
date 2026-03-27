package com.gduf.videoservice.dao;

import com.gduf.bilibilicommon.domain.auth.AuthRoleMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

@Mapper
public interface AuthRoleMenuDao {
    List<AuthRoleMenu> getRoleMenuByUserIds(Set<Long> roleIdSet);
}
