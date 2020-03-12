package com.example.leisure.db.greendao;

import com.example.leisure.greenDao.gen.ComicChapterBeanDao;
import com.example.leisure.greenDao.gen.ComicImageBeanDao;
import com.example.leisure.greenDao.gen.DaoSession;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import java.util.List;

@Entity
public class ComicChapterBean implements Serializable {
    private static final long serialVersionUID = 6538522983464695306L;
    @Id(autoincrement = true)
    private Long _id;
    public String num;
    public String url;
    private Long bookId;
    private boolean isCaching;
    private String path;
    private int cacheState;
    private int maxCount;
    private int cacheCount;

    @ToMany(referencedJoinProperty = "chapterId")
    public List<ComicImageBean> list;

    @Transient
    public boolean visible;
    @Transient
    public boolean isReading;

    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 354404488)
    private transient ComicChapterBeanDao myDao;

    @Generated(hash = 820093937)
    public ComicChapterBean(Long _id, String num, String url, Long bookId,
                            boolean isCaching, String path, int cacheState, int maxCount,
                            int cacheCount) {
        this._id = _id;
        this.num = num;
        this.url = url;
        this.bookId = bookId;
        this.isCaching = isCaching;
        this.path = path;
        this.cacheState = cacheState;
        this.maxCount = maxCount;
        this.cacheCount = cacheCount;
    }

    @Generated(hash = 190955710)
    public ComicChapterBean() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getNum() {
        return this.num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getBookId() {
        return this.bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
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

    public int getCacheState() {
        return this.cacheState;
    }

    public void setCacheState(int cacheState) {
        this.cacheState = cacheState;
    }

    public int getMaxCount() {
        return this.maxCount;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public int getCacheCount() {
        return this.cacheCount;
    }

    public void setCacheCount(int cacheCount) {
        this.cacheCount = cacheCount;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 801374422)
    public List<ComicImageBean> getList() {
        if (list == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ComicImageBeanDao targetDao = daoSession.getComicImageBeanDao();
            List<ComicImageBean> listNew = targetDao
                    ._queryComicChapterBean_List(_id);
            synchronized (this) {
                if (list == null) {
                    list = listNew;
                }
            }
        }
        return list;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 589833612)
    public synchronized void resetList() {
        list = null;
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
    @Generated(hash = 1466147602)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getComicChapterBeanDao() : null;
    }


}
