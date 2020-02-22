package com.example.leisure.db.greendao;

import com.example.leisure.greenDao.gen.BookChapterDao;
import com.example.leisure.greenDao.gen.BookShelfDao;
import com.example.leisure.greenDao.gen.DaoSession;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.OrderBy;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

@Entity
public class BookShelf {
    /**
     * name : 绝世古尊
     * url : mh123/comic/26620.html
     * cover : https://img.detatu.com/upload/vod/2019-09-17/15686950561.jpg
     * time : 2020-01-05
     * latest : 第47话大..大蒜！
     */
    @Id(autoincrement = true)
    private Long _id;
    private String name;
    private String url;
    private String cover;
    private String time;
    private String latest;
    @OrderBy("lastTime desc")
    private long lastTime;  //记录最后看的时间
    private boolean isTop;    //是否置顶
    private Long readToChapterId;   //记录阅读到的章节
    private String readToChapterUrl;   //记录阅读到章节url
    //0  未下载
    //1  已下载
    //2  正在下载
    //3  取消下载
    private int cacheState;
    private float progress;

    @ToMany(referencedJoinProperty = "bookId")
    private List<BookChapter> mLsChapter;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1689124318)
    private transient BookShelfDao myDao;

    @Generated(hash = 1381356201)
    public BookShelf(Long _id, String name, String url, String cover, String time,
            String latest, long lastTime, boolean isTop, Long readToChapterId,
            String readToChapterUrl, int cacheState, float progress) {
        this._id = _id;
        this.name = name;
        this.url = url;
        this.cover = cover;
        this.time = time;
        this.latest = latest;
        this.lastTime = lastTime;
        this.isTop = isTop;
        this.readToChapterId = readToChapterId;
        this.readToChapterUrl = readToChapterUrl;
        this.cacheState = cacheState;
        this.progress = progress;
    }

    @Generated(hash = 547688644)
    public BookShelf() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
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

    public long getLastTime() {
        return this.lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public boolean getIsTop() {
        return this.isTop;
    }

    public void setIsTop(boolean isTop) {
        this.isTop = isTop;
    }

    public Long getReadToChapterId() {
        return this.readToChapterId;
    }

    public void setReadToChapterId(Long readToChapterId) {
        this.readToChapterId = readToChapterId;
    }

    public String getReadToChapterUrl() {
        return this.readToChapterUrl;
    }

    public void setReadToChapterUrl(String readToChapterUrl) {
        this.readToChapterUrl = readToChapterUrl;
    }

    public int getCacheState() {
        return this.cacheState;
    }

    public void setCacheState(int cacheState) {
        this.cacheState = cacheState;
    }

    public float getProgress() {
        return this.progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 333653895)
    public List<BookChapter> getMLsChapter() {
        if (mLsChapter == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            BookChapterDao targetDao = daoSession.getBookChapterDao();
            List<BookChapter> mLsChapterNew = targetDao
                    ._queryBookShelf_MLsChapter(_id);
            synchronized (this) {
                if (mLsChapter == null) {
                    mLsChapter = mLsChapterNew;
                }
            }
        }
        return mLsChapter;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 983337844)
    public synchronized void resetMLsChapter() {
        mLsChapter = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 231179132)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getBookShelfDao() : null;
    }

}
