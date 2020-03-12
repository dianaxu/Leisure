package com.example.leisure.db.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

@Entity
public class ReadPosition implements Serializable {
    private static final long serialVersionUID = 5329993276917890545L;
    @Id(autoincrement = true)
    private Long _id;
    private String bookUrl;
    private int position;
    @Generated(hash = 603023864)
    public ReadPosition(Long _id, String bookUrl, int position) {
        this._id = _id;
        this.bookUrl = bookUrl;
        this.position = position;
    }
    @Generated(hash = 1792853255)
    public ReadPosition() {
    }
    public Long get_id() {
        return this._id;
    }
    public void set_id(Long _id) {
        this._id = _id;
    }
    public String getBookUrl() {
        return this.bookUrl;
    }
    public void setBookUrl(String bookUrl) {
        this.bookUrl = bookUrl;
    }
    public int getPosition() {
        return this.position;
    }
    public void setPosition(int position) {
        this.position = position;
    }
}