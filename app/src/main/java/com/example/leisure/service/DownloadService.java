package com.example.leisure.service;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.leisure.MainApplication;
import com.example.leisure.db.greendao.ComicBookBean;
import com.example.leisure.db.greendao.ComicChapterBean;
import com.example.leisure.db.greendao.ComicImageBean;
import com.example.leisure.greenDao.gen.ComicBookBeanDao;
import com.example.leisure.greenDao.gen.ComicChapterBeanDao;
import com.example.leisure.greenDao.gen.ComicImageBeanDao;
import com.example.leisure.greenDao.gen.DaoSession;
import com.example.leisure.util.Constant;
import com.example.leisure.util.NetworkUtil;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;

public class DownloadService extends Service implements DownloadTask.onDownLoadInterface {
    private static final String TAG1 = "DownloadTask";

    private DaoSession mDaoSession;
    private LinkedHashMap<Long, DownloadTask> mLsTask = new LinkedHashMap<>();

    private ComicBookBean mBook;
    private long mChapterId = -1;
    private long mChapterCountInBook = 0;
    private long mLastUpdateTime;             //最后更新的时间
    private long mUpdateTimeInterval = 830;  //UI更新时间的间隔

    private List<IDownloadCallback> mICallbacks = new LinkedList<>();

    //注册接口
    public void registerCallBack(IDownloadCallback callBack) {
        if (mICallbacks != null) {
            mICallbacks.add(callBack);
        }
    }

    /**
     * 注销接口 false注销失败
     *
     * @param callBack
     * @return
     */
    public boolean unRegisterCallBack(IDownloadCallback callBack) {
        if (mICallbacks != null && mICallbacks.contains(callBack)) {
            Log.e(TAG1, "unRegisterCallBack: success");
            return mICallbacks.remove(callBack);
        }
        Log.e(TAG1, "unRegisterCallBack: fail");
        return false;
    }


    /**
     * ----------------------------Service原生方法-------------------
     */
    public class DownloadBinder extends Binder {

        public DownloadService getService() {
            return DownloadService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mLsTask == null || mLsTask.size() == 0) {
            mDaoSession = MainApplication.getInstance().getDaoSession();
            startTask();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mDaoSession = MainApplication.getInstance().getDaoSession();
        return new DownloadService.DownloadBinder();
    }

    @Override
    public void onDestroy() {
        Log.e(TAG1, "onDestroy: ");
        cancelAllTask();
        updateChaptersState(null, Constant.DownloadState.DOWNLOADING, Constant.DownloadState.DOWNLOAD_CANCEL);
        updateBooksState(null, Constant.DownloadState.DOWNLOADING, Constant.DownloadState.DOWNLOAD_CANCEL);
        mLsTask = null;
        super.onDestroy();
    }

    private boolean checkNetworkWifi() {
        if (!NetworkUtil.isConnectedByWifi(this)) {
            //将所有的任务全部停止
            return false;
        }
        return true;
    }

    /**
     * ----------------------------提供方法给绑定任务的组件-------------------
     */
    //开始任务
    public void startTask() {
        if (!checkNetworkWifi()) {
            onNotConnWifi();
            return;
        }
        if (mLsTask.size() > 0) return;
        //获取需要下载的所有章节集
        List<ComicChapterBean> lsChapter = mDaoSession.getComicChapterBeanDao().queryRaw("where Cache_State = ? ",
                String.valueOf(Constant.DownloadState.DOWNLOADING));
        if (lsChapter.size() == 0) {
            stopSelf();
            return;
        }

        //按照章节开始启动线程来下载
        for (int i = 0; i < lsChapter.size(); i++) {
            ComicChapterBean chapter = lsChapter.get(i);
            long chapterId = chapter.get_id();
            //情况1：章节的maxCount=cachecount
            if (chapter.getMaxCount() > 0 && chapter.getMaxCount() == chapter.getCacheState()) {
                updateChaptersState(chapterId, chapter.getCacheState(), Constant.DownloadState.DOWNLOADED);
                updateBookCacheProgress(chapter);
                //发送更新广播
                sendFinishChapter(chapterId, Constant.DownloadState.DOWNLOADED);
                sendUpdateProgressBook(chapter.getBookId(), mBook.getProgress());
                continue;
            } else if (chapter.getMaxCount() > 0 && chapter.getMaxCount() != chapter.getCacheState()) {
                //情况2：章节的maxCount!=cachecount 但是图片已经下载完了
                long count = mDaoSession.getComicImageBeanDao().queryBuilder()
                        .where(ComicImageBeanDao.Properties.ChapterId.eq(chapterId),
                                ComicImageBeanDao.Properties.IsCaching.eq(true))
                        .count();
                if (count == chapter.getMaxCount()) {
                    chapter.setCacheCount((int) count);
                    chapter.setIsCaching(true);
                    chapter.setCacheState(Constant.DownloadState.DOWNLOADED);
                    mDaoSession.update(chapter);
                    updateBookCacheProgress(chapter);
                    updateBookState(chapter.getBookId());
                    //发送更新广播
                    sendFinishChapter(chapterId, Constant.DownloadState.DOWNLOADED);
                    sendUpdateProgressBook(chapter.getBookId(), mBook.getProgress());
                    continue;
                }
            }
            DownloadTask task = startDownloadTask(lsChapter.get(i));
            mLsTask.put(lsChapter.get(i).get_id(), task);
        }
    }

    public void addTask(Long bookId) {
        if (!checkNetworkWifi()) {
            onNotConnWifi();
            return;
        }
        if (bookId == null) return;

        LinkedHashMap<Long, DownloadTask> lsBefore = new LinkedHashMap<>(); //这本书之前的任务，如果第一个任务就是这本书  那么保留第一个
        List<Long> lsAfter = new ArrayList<>();  //这本书之后的任务

        //针对有多个数据的任务 需要进行重新分段
        if (mLsTask.size() > 1) {
            String sql = "select max(_id) as max,min(_id) as min from Comic_Chapter_Bean where book_id = ?";
            Cursor cursor = mDaoSession.getDatabase().rawQuery(sql, new String[]{String.valueOf(bookId)});

            cursor.moveToFirst();
            long maxId = cursor.getLong(0);  //这本书最大ChapterId
            long minId = cursor.getLong(1);  //这本书最小ChapterId

            boolean isHandle = false;  //记录是否遇到了这本书的章节
            int x = 0;
            //循环整个任务集
            for (Map.Entry<Long, DownloadTask> entity : mLsTask.entrySet()) {
                long key = entity.getKey();
                DownloadTask value = entity.getValue();
                //这本书的章节 对其任务进行取消
                if (minId <= key && key <= maxId) {
                    isHandle = true;
                    if (x == 0) lsBefore.put(key, value);
                    else
                        value.cancelTask();
                } else {
                    //对这本书之后的章节进行取消  并且保存下来
                    if (isHandle) {
                        value.cancelTask();
                        lsAfter.add(key);
                    } else {
                        //保留未遇到这本书之前的任务
                        lsBefore.put(key, value);
                    }
                }
                x++;
            }

            mLsTask.clear();
        }
        //这本书全部的需要下载的章节  按照id进行下载排列
        List<ComicChapterBean> list = mDaoSession.getComicChapterBeanDao().queryBuilder()
                .where(ComicChapterBeanDao.Properties.BookId.eq(bookId),
                        ComicChapterBeanDao.Properties.CacheState.eq(Constant.DownloadState.DOWNLOADING))
                .list();

        //加入这本书的章节
        for (int i = 0; i < list.size(); i++) {
            if (!lsBefore.containsKey(list.get(i).get_id()))
                lsBefore.put(list.get(i).get_id(), startDownloadTask(list.get(i)));
        }
        //加入这本书之后的章节
        for (int i = 0; i < lsAfter.size(); i++) {
            lsBefore.put(lsAfter.get(i), startDownloadTask(lsAfter.get(i)));
        }

        mLsTask = lsBefore;

        //更新book
        updateBooksState(bookId, null, Constant.DownloadState.DOWNLOADING);
    }

    /*
     * 添加下载任务
     *
     * @param chapterId
     */
    public void addTask(long chapterId, long bookId) {
        if (!checkNetworkWifi()) {
            onNotConnWifi();
            return;
        }
        QueryBuilder<ComicImageBean> builder = mDaoSession.getComicImageBeanDao().queryBuilder()
                .where(ComicImageBeanDao.Properties.ChapterId.eq(chapterId));

        long maxCount = builder.count();
        long cacheCount = builder.where(ComicImageBeanDao.Properties.IsCaching.eq(true)).count();
        //数据已经下载完了 界面未更新
        if (maxCount > 0 && maxCount == cacheCount) {
            ComicChapterBean chapter = mDaoSession.getComicChapterBeanDao().queryBuilder()
                    .where(ComicChapterBeanDao.Properties._id.eq(chapterId))
                    .unique();
            chapter.setCacheCount((int) cacheCount);
            chapter.setCacheState(Constant.DownloadState.DOWNLOADED);
            chapter.setIsCaching(true);
            mDaoSession.update(chapter);

            updateBookCacheProgress(chapter);
            updateBookState(chapter.getBookId());

            //发送更新广播
            sendFinishChapter(chapterId, Constant.DownloadState.DOWNLOADED);
            sendFinishBook(chapter.getBookId(), mBook.getProgress(), mBook.getCacheState());
            return;
        }

        if (mLsTask.size() <= 1) {
            mLsTask.put(chapterId, startDownloadTask(chapterId));
//            updateBookCacheProgress(chapterId);
//            sendDownloadReceiver(mBook);
            return;
        }

        addTask(bookId);
    }


    //取消所有任务
    private void cancelAllTask() {
        if (mLsTask == null || mLsTask.size() == 0) {
            return;
        }

        for (Map.Entry<Long, DownloadTask> entity : mLsTask.entrySet()) {
            entity.getValue().cancelTask();
        }
        mLsTask.clear();
    }

    public void cancelChaptersTask(long bookId) {
        if (mLsTask == null || mLsTask.size() == 0) {
            return;
        }
        List<ComicChapterBean> list = mDaoSession.getComicChapterBeanDao().queryBuilder()
                .where(ComicChapterBeanDao.Properties.BookId.eq(bookId))
                .list();

        cancelChaptersTask(bookId, list);
    }

    //取消多个章节任务
    public void cancelChaptersTaskById(long bookId, List<Long> chapterIds) {
        if (mLsTask == null || mLsTask.size() == 0 ||
                chapterIds == null || chapterIds.size() == 0) {
            return;
        }
        for (Long bean : chapterIds) {
            if (mLsTask.get(bean) != null) {
                mLsTask.get(bean).cancelTask();
                mLsTask.remove(bean);
            }
        }

        updateBookState(bookId);
        sendUpdateBookState(bookId, mBook.getCacheState());
    }

    //取消多个章节任务
    public void cancelChaptersTask(long bookId, List<ComicChapterBean> chapters) {
        if (mLsTask == null || mLsTask.size() == 0 ||
                chapters == null || chapters.size() == 0) {
            return;
        }
        for (ComicChapterBean bean : chapters) {
            if (mLsTask.get(bean.get_id()) != null) {
                mLsTask.get(bean.get_id()).cancelTask();
                mLsTask.remove(bean.get_id());
            }
        }

        updateBookState(bookId);
        sendUpdateBookState(mBook.get_id(), mBook.getCacheState());
    }

    //取消单个章节任务
    public void cancelChapterTask(long bookId, long chapterId) {
        if (mLsTask == null || mLsTask.size() == 0) return;

        if (mLsTask.get(chapterId) != null) {
            mLsTask.get(chapterId).cancelTask();
            mLsTask.remove(chapterId);
        }

        updateBookState(bookId);
        sendUpdateBookState(mBook.get_id(), mBook.getCacheState());
    }

    /**
     * ----------------------------开启下载任务-------------------
     */

    //开启下载图片任务
    private DownloadTask startDownloadTask(long chapterId) {
        ComicChapterBean chapter = mDaoSession.getComicChapterBeanDao().queryBuilder()
                .where(ComicChapterBeanDao.Properties._id.eq(chapterId))
                .unique();
        return startDownloadTask(chapter);
    }

    //开启下载图片任务
    private DownloadTask startDownloadTask(ComicChapterBean chapter) {
        DownloadTask task = new DownloadTask(DownloadService.this, mDaoSession, chapter.getBookId(), this);
        task.execute(chapter);
        return task;
    }

    /**
     * ----------------------------DownloadTask回掉-------------------
     */

    @Override
    public void onStartingChapter(long chapterId) {
        mLastUpdateTime = System.currentTimeMillis();
    }

    @Override
    public void onSaveImageUrlsToDB(ComicChapterBean bean) {

    }

    @Override
    public void onUpdateProgressChapter(ComicChapterBean bean, ComicImageBean detail) {
//        long newTime = System.currentTimeMillis();
//        if (Math.abs(newTime - mLastUpdateTime) > mUpdateTimeInterval || bean.getCacheCount() == bean.getMaxCount()) {
        //更新进度
        //发送信息
        //更新images
        if (detail != null)
            mDaoSession.getComicImageBeanDao().update(detail);
        //更新chapter表
        updateChapterCacheProgress(bean);

        //更新book
        updateBookCacheProgress(bean);

        Log.e("finish", "onUpdateProgressChapter: state" + bean.getCacheState() + " : " + bean.getCacheCount());

        //发送信息
        sendUpdateProgressChapter(bean.get_id(), bean.getCacheCount(), bean.getMaxCount());
        sendUpdateProgressBook(bean.getBookId(), mBook.getProgress());
//    }

    }

    //完成章节下载
    @Override
    public void onFinishChapter(ComicChapterBean bean) {
        if (bean.getCacheCount() == bean.getMaxCount()) {
            Log.e("finish", "onFinishChapter: ----> 1");
            bean.setIsCaching(true);
            bean.setCacheState(Constant.DownloadState.DOWNLOADED);
        } else {
            Log.e("finish", "onFinishChapter: ----> cancel");
            bean.setCacheState(Constant.DownloadState.DOWNLOAD_CANCEL);
        }

        //发送信息
        //关闭任务
        //检测是否全部任务完成
        sendFinishChapter(bean.get_id(), bean.getCacheState());

        updateBookState(bean.getBookId());
        sendFinishBook(bean.getBookId(), mBook.getProgress(), mBook.getCacheState());

        cancelChapterTask(bean.getBookId(), bean.get_id());
        stopService();
    }

    //成功取消chapter
    @Override
    public void onSuccessCancelChapter(ComicChapterBean bean) {
        //1.todo 需不需要更新数据 状态改成取消
        //2.向组件发送信息
        cancelChapterTask(bean.getBookId(), bean.get_id());
        updateChaptersState(bean.get_id(), bean.getCacheState(),
                Constant.DownloadState.DOWNLOAD_CANCEL);
        updateBookState(bean.getBookId());
        sendSuccessCancelChapter(bean.get_id());
        stopService();
    }

    //下载失败
    @Override
    public void onFailChapter(ComicChapterBean bean) {
        cancelChapterTask(bean.getBookId(), bean.get_id());
        updateChaptersState(bean.get_id(), bean.getCacheState(),
                Constant.DownloadState.DOWNLOAD_CANCEL);
        updateBookState(bean.getBookId());
        sendChapterFailure(bean.get_id(), "下载失败");
        stopService();
    }

    //没有连接wifi
    @Override
    public void onNotConnWifi() {
        /**
         * 1.停止所有的下载任务
         * 2.更改漫画书状态   正在下载的状态 改成 取消状态
         * 3.发送消息
         */
        cancelAllTask();
        updateChaptersState(null, Constant.DownloadState.DOWNLOADING,
                Constant.DownloadState.DOWNLOAD_CANCEL);
        updateBooksState(null, Constant.DownloadState.DOWNLOADING,
                Constant.DownloadState.DOWNLOAD_CANCEL);

        sendNotConnWifi();
        stopService();
    }


    /**
     * ------------------连接数据库的方法-------------
     */

    /**
     * 更新漫画书状态
     *
     * @param bookId   null  所有的oldstate状态的漫画书
     * @param oldState 原先状态
     * @param newState 更改的状态
     */
    private void updateBooksState(@Nullable Long bookId, @Nullable Integer oldState, int newState) {
        StringBuffer sqlStr = new StringBuffer();
        sqlStr.append("update comic_book_bean set cache_state = " + newState);
        if (oldState != null)
            sqlStr.append(" where cache_state = " + oldState);
        if (oldState != null && bookId != null) {
            sqlStr.append(" and _id = " + bookId);
        } else if (oldState == null && bookId != null) {
            sqlStr.append(" where _id = " + bookId);
        }
        mDaoSession.getDatabase().execSQL(sqlStr.toString());
    }

    //更新漫画书状态 根据数据库数据进行分析得
    private ComicBookBean updateBookState(long bookId) {
        //更新漫画书的完成状态
        mBook = mDaoSession.getComicBookBeanDao().queryBuilder()
                .where(ComicBookBeanDao.Properties._id.eq(bookId))
                .unique();
        int cacheState = Constant.DownloadState.DOWNLOADING;
        if (mBook.getProgress() == 1) {
            cacheState = Constant.DownloadState.DOWNLOADED;
        } else {
            //正在下载的数量
            long downCount = mDaoSession.getComicChapterBeanDao().queryBuilder()
                    .where(ComicChapterBeanDao.Properties.BookId.eq(bookId)
                            , ComicChapterBeanDao.Properties.CacheState.eq(
                                    Constant.DownloadState.DOWNLOADING)).count();
            //取消数量
            long cancelCount = mDaoSession.getComicChapterBeanDao().queryBuilder()
                    .where(ComicChapterBeanDao.Properties.BookId.eq(bookId)
                            , ComicChapterBeanDao.Properties.CacheState.eq(
                                    Constant.DownloadState.DOWNLOAD_CANCEL)).count();
            //默认数量
            long defaultCount = mDaoSession.getComicChapterBeanDao().queryBuilder()
                    .where(ComicChapterBeanDao.Properties.BookId.eq(bookId)
                            , ComicChapterBeanDao.Properties.CacheState.eq(
                                    Constant.DownloadState.DOWNLOAD_NOT)).count();
            if (downCount == 0 && cancelCount > 0) {
                cacheState = Constant.DownloadState.DOWNLOAD_CANCEL;
            } else if (downCount == 0 && cancelCount == 0 && defaultCount > 0) {
                cacheState = Constant.DownloadState.DOWNLOAD_NOT;
            }
            mBook.setCacheState(cacheState);
        }
        mDaoSession.update(mBook);
        return mBook;
    }


    /**
     * 更新章节状态
     *
     * @param chapterId null  所有的满足oldstate状态下的章节
     * @param oldState  原先状态
     * @param newState  更改的状态
     */
    private void updateChaptersState(@Nullable Long chapterId, int oldState, int newState) {
        StringBuffer sqlStr = new StringBuffer();
        sqlStr.append("update comic_chapter_bean set cache_state = " + newState);
        sqlStr.append(" where cache_state = " + oldState);
        if (chapterId != null) {
            sqlStr.append(" and _id = " + chapterId);
        }
        mDaoSession.getDatabase().execSQL(sqlStr.toString());
    }

    /**
     * task任务完成，更新数据库
     *
     * @param bean
     */
    private void updateChapterCacheProgress(ComicChapterBean bean) {
        //更新章节完成状态  默认取消状态，方便下次进来直接开启
        //bean.setCacheState(Constant.DownloadState.DOWNLOAD_CANCEL);
        if (bean.getMaxCount() > 0 && bean.getMaxCount() == bean.getCacheCount()) {
            bean.setIsCaching(true);
            bean.setCacheState(Constant.DownloadState.DOWNLOADED);
        }
        mDaoSession.getComicChapterBeanDao().update(bean);
    }

    private void updateBookCacheProgress(ComicChapterBean bean) {
        //初始化漫画书
        if (mBook == null || mBook.get_id() != bean.getBookId()) {
            mBook = mDaoSession.getComicBookBeanDao().queryBuilder()
                    .where(ComicBookBeanDao.Properties._id.eq(bean.getBookId()))
                    .unique();
            mChapterCountInBook = mDaoSession.getComicChapterBeanDao().queryBuilder()
                    .where(ComicChapterBeanDao.Properties.BookId.eq(bean.getBookId()))
                    .count();
        }

        //完成一个图片下载 占这本书的进度
        float progress = caleBookProgress(mBook.get_id());
        mBook.setProgress(progress);
        if (bean.getCacheState() == Constant.DownloadState.DOWNLOADING)
            mBook.setCacheState(Constant.DownloadState.DOWNLOADING);
        else {
            updateBookState(mBook.get_id());
        }
        //更新ComicBookBean表
        mDaoSession.getComicBookBeanDao().update(mBook);
    }

    private float caleBookProgress(long bookId) {
        int chapterCount = (int) mDaoSession.getComicChapterBeanDao().queryBuilder().where(ComicChapterBeanDao.Properties.BookId.eq(bookId)).count();

        String sqlStr = "select sum(round(Cast(cache_count as float)/max_count/? ,5) )as sum " +
                " from comic_chapter_bean where book_id = ? and cache_count != 0;";
        Cursor cursor = mDaoSession.getDatabase().rawQuery(sqlStr, new String[]{String.valueOf(chapterCount), String.valueOf(bookId)});
        cursor.moveToFirst();
        float progress = cursor.getFloat(0);
        cursor.close();
        return progress;
    }


    /**
     * ----------------------------发送信息给Activity-------------------
     */

    //发送章节更新进度
    private void sendUpdateProgressChapter(long chapterId, int cacheCount, int maxCount) {
        Log.e("TestService", "sendChapterUpdateProgress: " + mICallbacks.size());
        if (mICallbacks != null && mICallbacks.size() > 0) {
            for (IDownloadCallback callBack : mICallbacks) {
                if (callBack instanceof IDownloadChapterCallback)
                    ((IDownloadChapterCallback) callBack).onChapterProgress(chapterId, cacheCount, maxCount);
            }
        }
    }

    //发送章节完成
    private void sendFinishChapter(long chapterId, int state) {
        Log.e("TestService", "sendChapterUpdateProgress: " + mICallbacks.size());
        if (mICallbacks != null && mICallbacks.size() > 0) {
            for (IDownloadCallback callBack : mICallbacks) {
                if (callBack instanceof IDownloadChapterCallback)
                    ((IDownloadChapterCallback) callBack).onChapterFinish(chapterId, state);
            }
        }
    }

    private void sendSuccessCancelChapter(long chapterId) {
        Log.e(TAG1, "sendSuccessCancelChapter: " + mICallbacks.size());
        if (mICallbacks != null && mICallbacks.size() > 0) {
            for (IDownloadCallback callBack : mICallbacks) {
                if (callBack instanceof IDownloadChapterCallback)
                    ((IDownloadChapterCallback) callBack).onSuccessCancel(chapterId);
            }
        }
    }

    //发送漫画书更新进度
    private void sendUpdateProgressBook(long bookId, float progress) {
        Log.e("TestService", "sendChapterUpdateProgress: " + mICallbacks.size());
        if (mICallbacks != null && mICallbacks.size() > 0) {
            for (IDownloadCallback callBack : mICallbacks) {
                if (callBack instanceof IDownloadBookCallback)
                    ((IDownloadBookCallback) callBack).onUpdateProgressBook(bookId, progress);
            }
        }
    }

    //发送漫画书已经完成勾选的下载
    private void sendFinishBook(long bookId, float progress, int state) {
        Log.e(TAG1, "sendFinishBook: " + mICallbacks.size());
        if (mICallbacks != null && mICallbacks.size() > 0) {
            for (IDownloadCallback callBack : mICallbacks) {
                if (callBack instanceof IDownloadBookCallback)
                    ((IDownloadBookCallback) callBack).onFinishBook(bookId, progress, state);
            }
        }
    }

    private void sendUpdateBookState(long bookId, int state) {
        Log.e(TAG1, "sendFinishBook: " + mICallbacks.size());
        if (mICallbacks != null && mICallbacks.size() > 0) {
            for (IDownloadCallback callBack : mICallbacks) {
                if (callBack instanceof IDownloadBookCallback)
                    ((IDownloadBookCallback) callBack).onUpdateBookState(bookId, state);
            }
        }
    }

    //发送未连接网络
    private void sendNotConnWifi() {
        Log.e(TAG1, "sendChapterUpdateProgress: " + mICallbacks.size());
        if (mICallbacks != null && mICallbacks.size() > 0) {
            for (IDownloadCallback callBack : mICallbacks) {
                callBack.onNotConnWifi();
            }
        }
    }

    //发送章节下载失败
    private void sendChapterFailure(long chapterId, String msg) {
        Log.e(TAG1, "sendChapterUpdateProgress: " + mICallbacks.size());
        if (mICallbacks != null && mICallbacks.size() > 0) {
            for (IDownloadCallback callBack : mICallbacks) {
                if (callBack instanceof IDownloadChapterCallback)
                    ((IDownloadChapterCallback) callBack).onFailure(chapterId, msg);
            }
        }
    }


    private void stopService() {
        if (mLsTask == null || mLsTask.size() == 0) {
            stopSelf();
        }
    }
}
