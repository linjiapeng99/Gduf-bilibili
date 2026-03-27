package com.gduf.userservice.dao;

import com.gduf.bilibilicommon.domain.auth.AuthRole;
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
