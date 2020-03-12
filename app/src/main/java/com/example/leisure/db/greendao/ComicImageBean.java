package com.example.leisure.db.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

@Entity
public class ComicImageBean implements Serializable {
    private static final long serialVersionUID = 2147959643194461037L;
    @Id(autoincrement = true)
    private Long _id;
    public String img;
    private boolean isCaching;
    private String path;
    private Long chapterId;
    private Long bookId;
    @Generated(hash = 713513738)
    public ComicImageBean(Long _id, String img, boolean isCaching, String path,
            Long chapterId, Long bookId) {
        this._id = _id;
        this.img = img;
        this.isCaching = isCaching;
        this.path = path;
        this.chapterId = chapterId;
        this.bookId = bookId;
    }
    @Generated(hash = 1764409786)
    public ComicImageBean() {
    }
    public Long get_id() {
        return this._id;
    }
    public void set_id(Long _id) {
        this._id = _id;
    }
    public String getImg() {
        return this.img;
    }
    public void setImg(String img) {
        this.img = img;
    }
    public boolean getIsCaching() {
        return this.isCaching;
    }
    public void setIsCaching(boolean isCaching) {
        this.isCaching = isCaching;
    }
    public String getPath() {
        return this.path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public Long getChapterId() {
        return this.chapterId;
    }
    public void setChapterId(Long chapterId) {
        this.chapterId = chapterId;
    }
    public Long getBookId() {
        return this.bookId;
    }
    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

}
