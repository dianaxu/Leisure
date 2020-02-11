package com.example.leisure.db.greendao;

import com.example.leisure.greenDao.gen.BookChapterDao;
import com.example.leisure.greenDao.gen.ChapterDetailDao;
import com.example.leisure.greenDao.gen.DaoSession;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

@Entity
public class BookChapter {
    @Id(autoincrement = true)
    private Long _id;
    private Long bookId;
    private String num;
    private String url;
    private boolean isCaching;
    //0  未下载
    //1  已下载
    //2  正在下载
    //3  取消下载
    private int cacheState;
    private int maxCount;
    private int cacheCount;

    @ToMany(referencedJoinProperty = "chapterId")
    private List<ChapterDetail> mLsChapterImage;


    /**
     * Used to resolve relations
     */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /**
     * Used for active entity operations.
     */
    @Generated(hash = 1400647315)
    private transient BookChapterDao myDao;

    @Generated(hash = 24068438)
    public BookChapter(Long _id, Long bookId, String num, String url, boolean isCaching,
                       int cacheState, int maxCount, int cacheCount) {
        this._id = _id;
        this.bookId = bookId;
        this.num = num;
        this.url = url;
        this.isCaching = isCaching;
        this.cacheState = cacheState;
        this.maxCount = maxCount;
        this.cacheCount = cacheCount;
    }

    @Generated(hash = 1481387400)
    public BookChapter() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public Long getBookId() {
        return this.bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
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

    public boolean getIsCaching() {
        return this.isCaching;
    }

    public void setIsCaching(boolean isCaching) {
        this.isCaching = isCaching;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 680716797)
    public List<ChapterDetail> getMLsChapterImage() {
        if (mLsChapterImage == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ChapterDetailDao targetDao = daoSession.getChapterDetailDao();
            List<ChapterDetail> mLsChapterImageNew = targetDao
                    ._queryBookChapter_MLsChapterImage(_id);
            synchronized (this) {
                if (mLsChapterImage == null) {
                    mLsChapterImage = mLsChapterImageNew;
                }
            }
        }
        return mLsChapterImage;
    }

    /**
     * Resets a to-many relationship, making the next get call to query for a fresh result.
     */
    @Generated(hash = 1700548208)
    public synchronized void resetMLsChapterImage() {
        mLsChapterImage = null;
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
    @Generated(hash = 980434935)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getBookChapterDao() : null;
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

}
