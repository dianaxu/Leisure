package com.example.leisure.db.greendao;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class ChapterDetail extends Object {
    @Id(autoincrement = true)
    private Long _id;
    private Long chapterId;
    private Long bookId;  //为了方便删除数据
    private String img;
    private boolean isCaching;
    private String path;

    @Generated(hash = 402714361)
    public ChapterDetail(Long _id, Long chapterId, Long bookId, String img,
            boolean isCaching, String path) {
        this._id = _id;
        this.chapterId = chapterId;
        this.bookId = bookId;
        this.img = img;
        this.isCaching = isCaching;
        this.path = path;
    }

    @Generated(hash = 40392088)
    public ChapterDetail() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
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

}
