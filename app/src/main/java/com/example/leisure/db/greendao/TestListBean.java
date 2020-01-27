package com.example.leisure.db.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class TestListBean {
    /**
     * name : 绝世古尊
     * url : mh123/comic/26620.html
     * cover : https://img.detatu.com/upload/vod/2019-09-17/15686950561.jpg
     * time : 2020-01-05
     * latest : 第47话大..大蒜！
     */
    @Id
    private Long id;
    private String name;
    private String url;
    private String cover;
    private String time;
    private String latest;
    private Long mhlbId;
    @Generated(hash = 1251804815)
    public TestListBean(Long id, String name, String url, String cover, String time,
            String latest, Long mhlbId) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.cover = cover;
        this.time = time;
        this.latest = latest;
        this.mhlbId = mhlbId;
    }
    @Generated(hash = 1989531662)
    public TestListBean() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public String getCover() {
        return this.cover;
    }
    public void setCover(String cover) {
        this.cover = cover;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getLatest() {
        return this.latest;
    }
    public void setLatest(String latest) {
        this.latest = latest;
    }
    public Long getMhlbId() {
        return this.mhlbId;
    }
    public void setMhlbId(Long mhlbId) {
        this.mhlbId = mhlbId;
    }
}
