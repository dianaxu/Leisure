package com.example.leisure.db.greendao;

import com.example.leisure.greenDao.gen.ComicBookBeanDao;
import com.example.leisure.greenDao.gen.ComicChapterBeanDao;
import com.example.leisure.greenDao.gen.DaoSession;
import com.example.leisure.greenDao.gen.ReadPositionDao;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.ToOne;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import java.util.List;

@Entity
public class ComicBookBean implements Serializable {
    private static final long serialVersionUID = -3742666842616484836L;
    @Id(autoincrement = true)
    private Long _id;
    public String introduce;
    public String author;
    public String tag;
    public String name;
    public String url;
    public String cover;
    public String time;
    public String latest;
    private long lastTime;
    private boolean isTop;
    private String readToChapterUrl;
    private int cacheState;
    private float progress;
    private Long readPositionId;

    @ToOne(joinProperty = "readPositionId")
    private ReadPosition readPosition;

    @ToMany(referencedJoinProperty = "bookId")
    public List<ComicChapterBean> lsChapter;

    @Transient
    public String pages;
    @Transient
    public String dpages;


    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 2142591927)
    private transient ComicBookBeanDao myDao;

    @Generated(hash = 1917165801)
    public ComicBookBean(Long _id, String introduce, String author, String tag,
                         String name, String url, String cover, String time, String latest,
                         long lastTime, boolean isTop, String readToChapterUrl, int cacheState,
                         float progress, Long readPositionId) {
        this._id = _id;
        this.introduce = introduce;
        this.author = author;
        this.tag = tag;
        this.name = name;
        this.url = url;
        this.cover = cover;
        this.time = time;
        this.latest = latest;
        this.lastTime = lastTime;
        this.isTop = isTop;
        this.readToChapterUrl = readToChapterUrl;
        this.cacheState = cacheState;
        this.progress = progress;
        this.readPositionId = readPositionId;
    }

    @Generated(hash = 337421606)
    public ComicBookBean() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getIntroduce() {
        return this.introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getAuthor() {
        return this.author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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

    public Long getReadPositionId() {
        return this.readPositionId;
    }

    public void setReadPositionId(Long readPositionId) {
        this.readPositionId = readPositionId;
    }

    @Generated(hash = 170176140)
    private transient Long readPosition__resolvedKey;

    /**
     * To-one relationship, resolved on first access.
     */
    @Generated(hash = 802379499)
    public ReadPosition getReadPosition() {
        Long __key = this.readPositionId;
        if (readPosition__resolvedKey == null
                || !readPosition__resolvedKey.equals(__key)) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ReadPositionDao targetDao = daoSession.getReadPositionDao();
            ReadPosition readPositionNew = targetDao.load(__key);
            synchronized (this) {
                readPosition = readPositionNew;
                readPosition__resolvedKey = __key;
            }
        }
        return readPosition;
    }

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 723981865)
    public void setReadPosition(ReadPosition readPosition) {
        synchronized (this) {
            this.readPosition = readPosition;
            readPositionId = readPosition == null ? null : readPosition.get_id();
            readPosition__resolvedKey = readPositionId;
        }
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 114389568)
    public List<ComicChapterBean> getLsChapter() {
        if (lsChapter == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ComicChapterBeanDao targetDao = daoSession.getComicChapterBeanDao();
            List<ComicChapterBean> lsChapterNew = targetDao
                    ._queryComicBookBean_LsChapter(_id);
            synchronized (this) {
                if (lsChapter == null) {
                    lsChapter = lsChapterNew;
                }
            }
        }
        return lsChapter;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 1300997291)
    public synchronized void resetLsChapter() {
        lsChapter = null;
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

    /**
     * called by internal mechanisms, do not call yourself.
     */
    @Generated(hash = 1310582613)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getComicBookBeanDao() : null;
    }

}