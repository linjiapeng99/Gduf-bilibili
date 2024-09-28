package com.gduf.bilibili.domain.auth;

import java.util.List;

public class UserAuthorities {
    private List<AuthRoleElementOperation> authElementOperationList;

    private List<AuthRoleMenu> authMenuList;

    public List<AuthRoleElementOperation> getAuthElementOperationList() {
        return authElementOperationList;
    }

    public void setAuthElementOperationList(List<AuthRoleElementOperation> authElementOperationList) {
        this.authElementOperationList = authElementOperationList;
    }

    public List<AuthRoleMenu> getAuthMenuList() {
        return authMenuList;
    }

    public void setAuthMenuList(List<AuthRoleMenu> authMenuList) {
        this.authMenuList = authMenuList;
    }
}