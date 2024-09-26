package com.gduf.bilibili.domain.auth;

import java.util.List;

public class UserAuthorities {
    private List<AuthElementOperation> authElementOperationList;

    private List<AuthMenu> authMenuList;

    public List<AuthElementOperation> getAuthElementOperationList() {
        return authElementOperationList;
    }

    public void setAuthElementOperationList(List<AuthElementOperation> authElementOperationList) {
        this.authElementOperationList = authElementOperationList;
    }

    public List<AuthMenu> getAuthMenuList() {
        return authMenuList;
    }

    public void setAuthMenuList(List<AuthMenu> authMenuList) {
        this.authMenuList = authMenuList;
    }
}