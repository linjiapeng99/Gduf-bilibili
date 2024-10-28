package com.gduf.bilibili.domain;

import java.util.Date;

public class VideoBinaryPicture {
    private Long id;

    private Long videoId;

    private Integer frameNo;

    private String url;

    private Long  videoTimeStamp;

    private Date createTime;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getFrameNo() {
        return frameNo;
    }

    public void setFrameNo(Integer frameNo) {
        this.frameNo = frameNo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public Long getVideoTimeStamp() {
        return videoTimeStamp;
    }

    public void setVideoTimeStamp(Long videoTimeStamp) {
        this.videoTimeStamp = videoTimeStamp;
    }
}
