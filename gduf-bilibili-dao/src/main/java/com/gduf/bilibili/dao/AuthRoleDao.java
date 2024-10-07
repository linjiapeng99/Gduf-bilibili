package com.gduf.bilibili.dao;

import com.gduf.bilibili.domain.auth.AuthRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthRoleDao {
    /**
     * 根据角色码获取角色id
     * @param code
     * @return
     */
    AuthRole getRoleByCode(String code);
}
