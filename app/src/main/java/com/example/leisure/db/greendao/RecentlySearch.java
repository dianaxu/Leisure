package com.example.leisure.db.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class RecentlySearch {
    @Id(autoincrement = true)
    private Long _id;
    @Index(unique = true)
    private String text;
    private String dataTime;
    private String userName;
    @Generated(hash = 1283746672)
    public RecentlySearch(Long _id, String text, String dataTime, String userName) {
        this._id = _id;
        this.text = text;
        this.dataTime = dataTime;
        this.userName = userName;
    }
    @Generated(hash = 1916356413)
    public RecentlySearch() {
    }
    public Long get_id() {
        return this._id;
    }
    public void set_id(Long _id) {
        this._id = _id;
    }
    public String getText() {
        return this.text;
    }
    public void setText(String text) {
        this.text = text;
    }
    public String getDataTime() {
        return this.dataTime;
    }
    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }
    public String getUserName() {
        return this.userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

}
