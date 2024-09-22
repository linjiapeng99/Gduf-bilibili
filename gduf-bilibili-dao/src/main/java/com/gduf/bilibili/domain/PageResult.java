package com.gduf.bilibili.domain;

import java.util.List;

public class PageResult <T>{
    public Integer total;

    public List<T>list;

    public PageResult(Integer total,List<T> list) {
        this.list = list;
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
