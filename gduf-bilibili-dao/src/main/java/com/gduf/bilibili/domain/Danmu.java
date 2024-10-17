package com.gduf.bilibili.domain;

import java.util.Date;

public class Danmu {
    private Long id;

    private Long userId;

    private Long videoId;

    private String content;

    private Date danmuTime;

    private Date createTime;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getDanmuTime() {
        return danmuTime;
    }

    public void setDanmuTime(Date danmuTime) {
        this.danmuTime = danmuTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }
}
