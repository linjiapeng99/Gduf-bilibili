package com.gduf.bilibili.dao;

import com.gduf.bilibili.domain.auth.AuthMenu;
import com.gduf.bilibili.domain.auth.AuthRoleMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Set;

@Mapper
public interface AuthRoleMenuDao {
    List<AuthRoleMenu> getRoleMenuByUserIds(Set<Long> roleIdSet);
}
