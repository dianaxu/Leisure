package com.example.leisure.service;

import java.io.Serializable;

public class DownloadProgressUpdateBean  implements Serializable {
    private int totalCacheCount;     //剩余未下载部分 累计成功下载数目
    private int maxCount;       //剩余未下载的总数
    private long chapterId;     //章节的_id
    private String imgUrl;      //图片url

    public DownloadProgressUpdateBean() {

    }

    public DownloadProgressUpdateBean(long chapterId) {
        this.chapterId = chapterId;
    }

    public long getChapterId() {
        return chapterId;
    }

    public void setChapterId(long chapterId) {
        this.chapterId = chapterId;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public int getTotalCacheCount() {
        return totalCacheCount;
    }

    public void setTotalCacheCount(int totalCacheCount) {
        this.totalCacheCount = totalCacheCount;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }
}
