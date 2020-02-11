package com.example.leisure.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.leisure.MainApplication;
import com.example.leisure.db.greendao.BookChapter;
import com.example.leisure.db.greendao.ChapterDetail;
import com.example.leisure.greenDao.gen.BookChapterDao;
import com.example.leisure.greenDao.gen.DaoSession;
import com.example.leisure.receiver.DownloadReceiver;
import com.example.leisure.util.Constant;

import org.greenrobot.greendao.query.Query;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;

public class DownloadService extends Service {
    private static final String TAG1 = "DownloadTask";

    private DaoSession mDaoSession;
    private LinkedHashMap<Long, DownloadTask> mLsTask = new LinkedHashMap<>();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG1, "onBind: ");
        return new DownloadBinder();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG1, "onCreate: ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG1, "onStartCommand: ");
        mDaoSession = MainApplication.getDaoSession();
        //获取需要下载的所有章节集
        Query<BookChapter> chapterQuery = mDaoSession.getBookChapterDao().queryBuilder()
                .where(BookChapterDao.Properties.CacheState.eq(Constant.DownloadState.DOWNLOADING))
                .orderAsc(BookChapterDao.Properties._id)
                .build();

        List<BookChapter> lsChapter = chapterQuery.list();
        //按照章节开始启动线程来下载
        for (int i = 0; i < lsChapter.size(); i++) {
            BookChapter chapter = lsChapter.get(i);
            if (chapter.getMaxCount() > 0 && chapter.getMaxCount() == chapter.getCacheState()) {
                updateChapterCacheState(chapter);
                continue;
            }
            DownloadTask task = startDownloadTask(lsChapter.get(i));
            mLsTask.put(lsChapter.get(i).get_id(), task);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        cancelTask();
        mLsTask = null;
    }

    private DownloadTask.onDownLoadInterface downLoadInterface = new DownloadTask.onDownLoadInterface() {
        @Override
        public void onStarting(long chapterId, int imgCount) {

        }

        @Override
        public void onProgressUpdate(BookChapter bean, ChapterDetail detail) {
            //更新图片表的缓存状态
            mDaoSession.getChapterDetailDao().update(detail);
            //更新BookChapter表的下载数据 本地数据库
            bean.setCacheCount(bean.getCacheCount() + 1);
            mDaoSession.getBookChapterDao().update(bean);

            Log.e(TAG1, "onProgressUpdate: -------" + bean.getCacheCount()
                    + " --" + bean.getMaxCount() + "  :" + bean.get_id());
            //发送更新广播
            sendDownloadReceiver(Constant.DownloadReceiverState.UPDATE, bean);
        }

        @Override
        public void onFinish(BookChapter bean) {
            Log.e(TAG1, "onFinish: " + (bean.getMaxCount() == bean.getCacheCount()));
            //全部下载完
            updateChapterCacheState(bean);
            mLsTask.remove(bean.get_id());
        }
    };

    /**
     * 更新某个章节的缓存状态
     *
     * @param bean
     */
    private void updateChapterCacheState(BookChapter bean) {
        int receiverState = Constant.DownloadReceiverState.NO_FINISH_ALL;
        if (bean.getMaxCount() == bean.getCacheCount()) {
            receiverState = Constant.DownloadReceiverState.FINISH;
            //更新章节表的状态
            bean.setIsCaching(true);
            bean.setCacheState(Constant.DownloadState.DOWNLOADED);
            mDaoSession.getBookChapterDao().update(bean);
        }
        //发送更新广播
        sendDownloadReceiver(receiverState, bean);
    }

    private void updateChapterCacheState(int state, long... chapterIds) {
        List<BookChapter> chapters = mDaoSession.getBookChapterDao().queryBuilder()
                .where(BookChapterDao.Properties._id.in(chapterIds))
                .list();
        for (BookChapter chapter : chapters) {
            chapter.setCacheState(state);
        }
        mDaoSession.getBookChapterDao().updateInTx(chapters);
    }

    private List<Long> updateChapterCacheState(long... bookIds) {
        List<Long> chapterIds = new ArrayList<>();
        List<BookChapter> chapters = mDaoSession.getBookChapterDao().queryBuilder()
                .where(BookChapterDao.Properties._id.in(bookIds),
                        BookChapterDao.Properties.CacheState.eq(Constant.DownloadState.DOWNLOADING))
                .list();

        for (BookChapter chapter : chapters) {
            chapterIds.add(chapter.get_id());
            chapter.setCacheState(Constant.DownloadState.DOWNLOAD_CANCEL);
        }
        mDaoSession.getBookChapterDao().updateInTx(chapters);
        return chapterIds;
    }

    private void updateChapterCacheState() {
        List<BookChapter> chapters = mDaoSession.getBookChapterDao().queryBuilder()
                .where(BookChapterDao.Properties.CacheState.eq(Constant.DownloadState.DOWNLOADING))
                .list();

        for (BookChapter chapter : chapters) {
            chapter.setCacheState(Constant.DownloadState.DOWNLOAD_CANCEL);
        }
        mDaoSession.getBookChapterDao().updateInTx(chapters);
    }

    private DownloadTask startDownloadTask(long chapterId) {
        BookChapter chapter = mDaoSession.getBookChapterDao().queryBuilder()
                .where(BookChapterDao.Properties._id.eq(chapterId))
                .unique();
        return startDownloadTask(chapter);
    }

    //开启下载图片任务
    private DownloadTask startDownloadTask(BookChapter chapter) {
        DownloadTask task = new DownloadTask(DownloadService.this, mDaoSession, downLoadInterface);
        task.execute(chapter);
        return task;
    }

    //发送下载进度广播
    private void sendDownloadReceiver(int receiverState, BookChapter bean) {
        Intent intent = new Intent("com.example.leisure.DownloadReceiver");
        intent.putExtra(DownloadReceiver.EXTRA_CHAPTER_ID, bean.get_id());
        intent.putExtra(DownloadReceiver.EXTRA_MAX_COUNT, bean.getMaxCount());
        intent.putExtra(DownloadReceiver.EXTRA_TOTAL_COUNT, bean.getCacheCount());
        intent.putExtra(DownloadReceiver.EXTRA_RECEIVER_STATE, receiverState);
        sendBroadcast(intent);
    }

    /**
     * 取消单个任务
     *
     * @param chapterId
     */
    public void cancelTask(long chapterId) {
        if (mLsTask.size() != 0) {
            if (mLsTask.containsValue(chapterId)) {
                mLsTask.get(chapterId).cancel(true);
                mLsTask.remove(chapterId);
            }
        }
        //更新本地数据库
        updateChapterCacheState(Constant.DownloadState.DOWNLOAD_CANCEL, chapterId);
    }

    /**
     * 取消多个任务
     */
    public void cancelTask(long[] chapterIds) {
        if (mLsTask.size() != 0) {
            //取消多个任务
            for (int i = 0; i < chapterIds.length; i++) {
                long chapterId = chapterIds[i];
                if (mLsTask.containsKey(chapterId)) {
                    mLsTask.get(chapterId).cancel(true);
                    mLsTask.remove(chapterId);
                }
            }
        }
        updateChapterCacheState(Constant.DownloadState.DOWNLOAD_CANCEL, chapterIds);
    }

    /**
     * 取消漫画书的所有的任务
     */
    public void cancelBookTask(long... bookId) {
        //更新数据库的状态
        List<Long> chapterIds = updateChapterCacheState(bookId);
        //将任务中的 相关这本书的所有章节的下载任务取消
        if (mLsTask.size() != 0) {
            for (long chapterId : chapterIds) {
                if (mLsTask.containsKey(chapterId)) {
                    mLsTask.get(chapterId).cancel(true);
                    mLsTask.remove(chapterId);
                }
            }
        }
    }

    /**
     * 取消所有的任务
     */
    public void cancelTask() {
        if (mLsTask.size() != 0) {
            for (Map.Entry<Long, DownloadTask> entity : mLsTask.entrySet()) {
                entity.getValue().cancel(true);
            }
            mLsTask = new LinkedHashMap<>();
        }
        updateChapterCacheState();
    }

    /**
     * 添加下载任务
     *
     * @param chapterId
     */
    public void addTask(long chapterId) {
        updateChapterCacheState(Constant.DownloadState.DOWNLOADING, chapterId);

        if (mLsTask.size() == 0) {
            mLsTask.put(chapterId, startDownloadTask(chapterId));
            return;
        }

        //任务已经存在
        if (mLsTask.containsValue(chapterId)) {
            DownloadTask task = mLsTask.get(chapterId);
            if (task != null) {
                task.execute(task.getBookChapter());
            }
        }
        //任务不存在 需要插入到任务中
        else {
            Object[] objects = mLsTask.keySet().toArray();
            List<Long> values = new ArrayList<>();  //记录在原始任务中 插入b任务之后的任务集
            long startKey = -1;  //原始数据 a c d f两个任务，插入 b任务   startKey指的是a的位置
            for (int i = 0; i < objects.length; i++) {
                //加入到最后一个
                if (i == objects.length - 1) {
                    mLsTask.put(chapterId, startDownloadTask(chapterId));
                    return;
                }
                long currentKey = (long) objects[i];
                //已经记录了要插入的数据
                if (startKey > 0) {
                    //任务没有被取消
                    if (!mLsTask.get(currentKey).isCancelled()) {
                        //先将任务取消
                        mLsTask.get(currentKey).cancel(true);
                        values.add(currentKey);

                        mLsTask.remove(currentKey);
                    }
                    continue;
                }
                //找到需要插入的地方
                if (currentKey < chapterId && chapterId < (long) objects[i + 1]) {
                    startKey = currentKey;
                }
            }

            //在重新开启 插入b任务之后的未取消的任务
            mLsTask.put(chapterId, startDownloadTask(chapterId));
            for (Long value : values) {
                mLsTask.put(value, startDownloadTask(value));
            }
        }
    }

    /**
     * 添加多个下载任务
     *
     * @param chapterIds
     */
    public void addTask(long... chapterIds) {
        //取消第一个任务的之外的任务
        int i = 0;
        for (Map.Entry<Long, DownloadTask> entity : mLsTask.entrySet()) {
            if (i > 0) {
                entity.getValue().cancel(true);
                mLsTask.remove(entity.getKey());
            }
            i++;
        }

        updateChapterCacheState(Constant.DownloadState.DOWNLOADING, chapterIds);

        List<BookChapter> chapters = mDaoSession.getBookChapterDao().queryBuilder()
                .where(BookChapterDao.Properties.CacheState.eq(Constant.DownloadState.DOWNLOADING))
                .list();

        for (int j = 0; j < chapters.size(); j++) {
            long chapterId = chapters.get(j).get_id();
            if (mLsTask.containsKey(chapterId)) continue;
            mLsTask.put(chapterId, startDownloadTask(chapters.get(i)));
        }
    }

    public void addTask(long bookId, long startId) {

    }

    private void delTask(long chapterId) {
        if (mLsTask.containsKey(chapterId)) {
            mLsTask.remove(chapterId);
        }
        updateChapterCacheState(Constant.DownloadState.DOWNLOAD_NOT, chapterId);
    }


    public class DownloadBinder extends Binder {

        public void cancelTask() {
            DownloadService.this.cancelTask();
        }

        public void cancelTask(long chapterId) {
            DownloadService.this.cancelTask(chapterId);
        }

        public void cancelTask(long[] chapterIds) {
            DownloadService.this.cancelTask(chapterIds);
        }
    }
}
