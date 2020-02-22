package com.example.leisure.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.leisure.MainApplication;
import com.example.leisure.db.greendao.BookChapter;
import com.example.leisure.db.greendao.BookShelf;
import com.example.leisure.db.greendao.ChapterDetail;
import com.example.leisure.greenDao.gen.BookChapterDao;
import com.example.leisure.greenDao.gen.BookShelfDao;
import com.example.leisure.greenDao.gen.ChapterDetailDao;
import com.example.leisure.greenDao.gen.DaoSession;
import com.example.leisure.receiver.DownloadReceiver;
import com.example.leisure.util.Constant;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;

public class DownloadService extends Service {
    private static final String TAG1 = "DownloadTask";

    private DaoSession mDaoSession;
    private LinkedHashMap<Long, DownloadTask> mLsTask = new LinkedHashMap<>();

    private BookShelf mBook;
    private long mChapterId = -1;
    private long mBookChapterCount = 0;
    private long mLastUpdateTime = System.currentTimeMillis();             //最后更新的时间
    private long mUpdateTimeInterval = 830;  //UI更新时间的间隔

    private DownloadTask.onDownLoadInterface downLoadInterface = new DownloadTask.onDownLoadInterface() {
        @Override
        public void onStarting(long chapterId) {
            sendDownloadReceiver(Constant.DownloadReceiverState.PROMPT_START, chapterId, "开始下载");
        }

        @Override
        public void onProgressUpdate(BookChapter bean, ChapterDetail detail) {
            updateCacheProgress(detail, bean);

            //发送更新广播
            sendDownloadReceiver(bean, false);
        }

        @Override
        public void onFinish(BookChapter bean) {
            Log.e(TAG1, bean.get_id() + " onFinish: " + (bean.getMaxCount() == bean.getCacheCount()));
            //全部下载完
            updateChapterCacheProgress(bean);
            mBook = updateBookCacheProgress(bean.getBookId());
            //发送更新广播
            sendDownloadReceiver(bean, true);
            if (mLsTask != null && mLsTask.get(bean.get_id()) != null) {
                mLsTask.get(bean.get_id()).cancel(true);
                mLsTask.remove(bean.get_id());
            }
        }

        @Override
        public void onFail(long chapterId) {
            Log.e(TAG1, chapterId + "  onFail: ");
            sendDownloadReceiver(Constant.DownloadReceiverState.PROMPT_FAIL, chapterId, "下载失败");
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        mDaoSession = MainApplication.getInstance().getDaoSession();
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
        stopSelf();
        return super.onStartCommand(intent, flags, startId);
    }

    //开始任务
    public void startTask() {
        if (mLsTask.size() > 0) return;
        //获取需要下载的所有章节集
        List<BookChapter> lsChapter = mDaoSession.getBookChapterDao().queryRaw("where Cache_State = ? ",
                String.valueOf(Constant.DownloadState.DOWNLOADING));
        if (lsChapter.size() == 0) {
            stopSelf();
            return;
        }

        //按照章节开始启动线程来下载
        for (int i = 0; i < lsChapter.size(); i++) {
            BookChapter chapter = lsChapter.get(i);
            //情况1：章节的maxCount=cachecount
            if (chapter.getMaxCount() > 0 && chapter.getMaxCount() == chapter.getCacheState()) {
                if (mBook == null || mBook.get_id() != chapter.getBookId()) {
                    mBook = mDaoSession.getBookShelfDao().queryBuilder().where(BookShelfDao.Properties._id.eq(chapter.getBookId())).unique();
                }

                updateChapterCacheProgress(chapter);
                mBook = updateBookCacheProgress(chapter.getBookId());
                //发送更新广播
                sendDownloadReceiver(chapter, true);
                continue;
            } else if (chapter.getMaxCount() > 0 && chapter.getMaxCount() != chapter.getCacheState()) {
                //情况2：章节的maxCount!=cachecount 但是图片已经下载完了
                long count = mDaoSession.getChapterDetailDao().queryBuilder()
                        .where(ChapterDetailDao.Properties.ChapterId.eq(chapter.get_id()),
                                ChapterDetailDao.Properties.IsCaching.eq(true))
                        .count();
                if (count == chapter.getMaxCount()) {
                    chapter.setCacheCount((int) count);
                    updateChapterCacheProgress(chapter);
                    mBook = updateBookCacheProgress(chapter.getBookId());
                    //发送更新广播
                    sendDownloadReceiver(chapter, true);
                    continue;
                }
            }
            DownloadTask task = startDownloadTask(lsChapter.get(i));
            mLsTask.put(lsChapter.get(i).get_id(), task);
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG1, "onDestroy: ");

        cancelTask();
        mLsTask = null;
        cancelAllChapterCache();
        super.onDestroy();
    }

    public boolean hasPlayTask() {
        return mLsTask.size() > 0;
    }

    private void updateCacheProgress(ChapterDetail detail, BookChapter bean) {
        //更新 ChapterDetail表
        if (detail != null)
            mDaoSession.getChapterDetailDao().update(detail);

        //初始化漫画书
        if (mBook == null || mBook.get_id() != bean.getBookId()) {
            mBook = mDaoSession.getBookShelfDao().queryBuilder()
                    .where(BookShelfDao.Properties._id.eq(bean.getBookId()))
                    .unique();
            mBookChapterCount = mDaoSession.getBookChapterDao().queryBuilder()
                    .where(BookChapterDao.Properties.BookId.eq(bean.getBookId()))
                    .count();
        }


        Log.e(TAG1, "updateCacheProgress: " + bean.get_id() + ":" + bean.getCacheCount() + ":" + bean.getMaxCount());
        //完成一个图片下载 占这本书的进度
        float progress = (float) (1.0 / mBookChapterCount * 1.0 / bean.getMaxCount());
        mBook.setProgress(mBook.getProgress() + progress);

        //更新BookChapter表
        mDaoSession.getBookChapterDao().update(bean);
        //更新BookShelf表
        mDaoSession.getBookShelfDao().update(mBook);

    }

    /**
     * task任务完成，更新数据库
     *
     * @param bean
     */
    private void updateChapterCacheProgress(BookChapter bean) {
        //更新章节完成状态  默认取消状态，方便下次进来直接开启
        bean.setCacheState(Constant.DownloadState.DOWNLOAD_CANCEL);
        if (bean.getMaxCount() > 0 && bean.getMaxCount() == bean.getCacheCount()) {
            bean.setIsCaching(true);
            bean.setCacheState(Constant.DownloadState.DOWNLOADED);
        }
        mDaoSession.getBookChapterDao().update(bean);
    }

    private BookShelf updateBookCacheProgress(long bookId) {
        //更新漫画书的完成状态
        BookShelf book = mDaoSession.getBookShelfDao().queryBuilder()
                .where(BookShelfDao.Properties._id.eq(bookId))
                .unique();
        int cacheState = Constant.DownloadState.DOWNLOADING;
        if (book.getProgress() == 1) {
            cacheState = Constant.DownloadState.DOWNLOADED;
        } else {
            //正在下载的数量
            long downCount = mDaoSession.getBookChapterDao().queryBuilder()
                    .where(BookChapterDao.Properties.BookId.eq(bookId)
                            , BookChapterDao.Properties.CacheState.eq(
                                    Constant.DownloadState.DOWNLOADING)).count();
            //取消数量
            long cancelCount = mDaoSession.getBookChapterDao().queryBuilder()
                    .where(BookChapterDao.Properties.BookId.eq(bookId)
                            , BookChapterDao.Properties.CacheState.eq(
                                    Constant.DownloadState.DOWNLOAD_CANCEL)).count();
            //默认数量
            long defaultCount = mDaoSession.getBookChapterDao().queryBuilder()
                    .where(BookChapterDao.Properties.BookId.eq(bookId)
                            , BookChapterDao.Properties.CacheState.eq(
                                    Constant.DownloadState.DOWNLOAD_NOT)).count();
            if (downCount == 0 && cancelCount > 0) {
                cacheState = Constant.DownloadState.DOWNLOAD_CANCEL;
            } else if (downCount == 0 && cancelCount == 0 && defaultCount > 0) {
                cacheState = Constant.DownloadState.DOWNLOAD_NOT;
            }
            book.setCacheState(cacheState);
        }
        mDaoSession.update(book);
        return book;
    }

    private BookShelf updateBookShelf(long bookId, int state) {
        //更新书架表的缓存数据
        BookShelf book = mDaoSession.getBookShelfDao().queryBuilder()
                .where(BookShelfDao.Properties._id.eq(bookId))
                .unique();

        book.setCacheState(state);
        mDaoSession.getBookShelfDao().update(book);
        return book;
    }

    private String whereArgStr(long... ids) {
        StringBuffer strSelectionArg = new StringBuffer();
        for (int i = 0; i < ids.length; i++) {
            strSelectionArg.append(ids[i] + ",");
        }
        if (strSelectionArg.length() > 0)
            strSelectionArg.delete(strSelectionArg.length() - 1, strSelectionArg.length());
        return strSelectionArg.toString();
    }

    private void updateChapterCacheState(int state, long... chapterIds) {
        List<BookChapter> chapters = mDaoSession.getBookChapterDao()
                .queryRaw("where _id in (?)", whereArgStr(chapterIds));
        for (BookChapter chapter : chapters) {
            chapter.setCacheState(state);
        }
        mDaoSession.getBookChapterDao().updateInTx(chapters);
    }

    private List<Long> updateChapterCacheState(long... bookIds) {
        String argStr = whereArgStr();

        List<Long> chapterIds = new ArrayList<>();
        List<BookChapter> chapters = mDaoSession.getBookChapterDao()
                .queryRaw("where _id in (?) and Cache_State = ?",
                        new String[]{argStr, String.valueOf(Constant.DownloadState.DOWNLOADING)});
        if (chapters.size() == 0) return null;

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

    private void cancelAllChapterCache() {
        List<BookChapter> chapters = mDaoSession.getBookChapterDao().queryRaw("where Cache_State = ?"
                , new String[]{String.valueOf(Constant.DownloadState.DOWNLOADING)});
        if (chapters.size() > 0) {
            for (int i = 0; i < chapters.size(); i++) {
                chapters.get(i).setCacheState(Constant.DownloadState.DOWNLOAD_CANCEL);
            }
            mDaoSession.getBookChapterDao().updateInTx(chapters);
        }
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

    private void sendDownloadReceiver(int prompt, long mChapterId, String message) {
        DownloadReceiver.ReceiverPromptBean receiverBean = new DownloadReceiver.ReceiverPromptBean();
        receiverBean.chapterId = mChapterId;
        receiverBean.message = message;
        Intent intent = new Intent(Constant.ReceiverAction.ACTION_DOWNLOAD);
        intent.putExtra(DownloadReceiver.EXTRA_RECEIVER_PROMPT, receiverBean);
        intent.putExtra(DownloadReceiver.EXTRA_RECEIVER_STATE, prompt);
        sendBroadcast(intent);
    }

    //发送下载进度广播
    private void sendDownloadReceiver(BookChapter bean, boolean isFinish) {
        //设置间隔更新UI界面
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastUpdateTime < mUpdateTimeInterval && bean.getCacheCount() != bean.getMaxCount() &&
                mLsTask.size() > 0 && !isFinish) return;
        mLastUpdateTime = currentTime;

        if (mBook == null) {
            mBook = mDaoSession.getBookShelfDao().queryBuilder().where(BookShelfDao.Properties._id.eq(bean.getBookId())).unique();
        }

        //发送广播
        DownloadReceiver.ReceiverBean receiverBean = new DownloadReceiver.ReceiverBean();
        receiverBean.bookId = bean.getBookId();
        receiverBean.chapterId = bean.get_id();
        receiverBean.maxCount = bean.getMaxCount();
        receiverBean.bookProgress = mBook.getProgress();
        receiverBean.totalCount = bean.getCacheCount();
        receiverBean.chapterCacheState = bean.getCacheState();
        receiverBean.bookCacheState = mBook.getCacheState();

        Intent intent = new Intent(Constant.ReceiverAction.ACTION_DOWNLOAD);
        intent.putExtra(DownloadReceiver.EXTRA_RECEIVER_BEAN, receiverBean);
        intent.putExtra(DownloadReceiver.EXTRA_RECEIVER_STATE, isFinish ? Constant.DownloadReceiverState.FINISH : Constant.DownloadReceiverState.UPDATE);
        sendBroadcast(intent);
    }

    private void sendDownloadReceiver(BookShelf book) {
        DownloadReceiver.ReceiverCancelBean receiverBean = new DownloadReceiver.ReceiverCancelBean();
        receiverBean.bookId = book.get_id();
        receiverBean.bookProgress = book.getProgress();
        receiverBean.bookState = book.getCacheState();

        Intent intent = new Intent(Constant.ReceiverAction.ACTION_DOWNLOAD);
        intent.putExtra(DownloadReceiver.EXTRA_RECEIVER_CANCEL_BEAN, receiverBean);
        intent.putExtra(DownloadReceiver.EXTRA_RECEIVER_STATE, Constant.DownloadReceiverState.CANCEL);
        sendBroadcast(intent);
    }

    /**
     * 取消单个任务
     *
     * @param chapterId
     */
    public void cancelTask(long chapterId) {
        Log.e(TAG1, "cancelTask: " + chapterId);
        if (mLsTask.size() != 0) {
            if (mLsTask.containsValue(chapterId)) {
                Log.e(TAG1, "cancelTask:  ok" + chapterId);
                mLsTask.get(chapterId).cancel(true);
                mLsTask.remove(chapterId);
            }
        }
    }

    //取消单个任务
    public void cancelTask(BookChapter chapter, boolean hasPlayTask) {
        Log.e(TAG1, "cancelTask1: " + chapter.get_id());
        long chapterId = chapter.get_id();
        if (mLsTask.size() != 0) {
            if (mLsTask.containsKey(chapterId)) {
                Log.e(TAG1, "cancelTask1:  ok" + chapterId);
                mLsTask.get(chapterId).cancel(true);
                mLsTask.remove(chapterId);
            }
        }
        BookShelf book = null;
        //保存书架表的缓存数据
        if (hasPlayTask)
            book = updateBookShelf(chapter.getBookId(), Constant.DownloadState.DOWNLOADING);
        else book = updateBookShelf(chapter.getBookId(), Constant.DownloadState.DOWNLOAD_CANCEL);

        sendDownloadReceiver(book);
    }

    /**
     * 取消多个任务  一本书的所有运行的都取消
     */
    public void cancelTask(List<BookChapter> chapters) {
        if (mLsTask.size() == 0) return;
        if (chapters.size() == 0) return;
        Log.e(TAG1, "-----------------------cancelTask------>:  " + mLsTask.size());
        //取消多个任务
        for (BookChapter chapter : chapters) {
            long chapterId = chapter.get_id();
            Log.e(TAG1, "cancelTask------>:  " + chapterId);

            if (mLsTask.containsKey(chapterId)) {
                Log.e(TAG1, "cancelTask#######:  " + chapterId);
                mLsTask.get(chapterId).cancelTask();
                mLsTask.remove(chapterId);
            }
        }
        //更新book
        BookShelf book = updateBookShelf(chapters.get(0).getBookId(), Constant.DownloadState.DOWNLOAD_CANCEL);
        //发送广播
        sendDownloadReceiver(book);
    }

    /**
     * 取消漫画书的所有的任务
     */
    public void cancelBookTask(BookShelf book) {
        List<BookChapter> chapters = mDaoSession.getBookChapterDao()
                .queryRaw("where Book_Id = ? and Cache_State = ?", new String[]{
                        String.valueOf(book.get_id()), String.valueOf(Constant.DownloadState.DOWNLOADING)
                });

        //没有可以需要取消的任务
        if (chapters.size() == 0) return;

        //将这本书的相关任务取消掉
        for (BookChapter chapter : chapters) {
            long chapterId = chapter.get_id();
            chapter.setCacheState(Constant.DownloadState.DOWNLOAD_CANCEL);
            if (mLsTask.containsKey(chapterId)) {
                mLsTask.get(chapterId).cancelTask();
                mLsTask.remove(chapterId);
            }
        }

        mDaoSession.getBookChapterDao().updateInTx(chapters);
        book.setCacheState(Constant.DownloadState.DOWNLOAD_CANCEL);
        mDaoSession.getBookShelfDao().update(book);
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
    public void addTask(long chapterId, long bookId) {
        QueryBuilder<ChapterDetail> builder = mDaoSession.getChapterDetailDao().queryBuilder()
                .where(ChapterDetailDao.Properties.ChapterId.eq(chapterId));

        long maxCount = builder.count();
        long cacheCount = builder.where(ChapterDetailDao.Properties.IsCaching.eq(true)).count();
        //数据已经下载完了 界面未更新
        if (maxCount > 0 && maxCount == cacheCount) {
            BookChapter chapter = mDaoSession.getBookChapterDao().queryBuilder()
                    .where(BookChapterDao.Properties._id.eq(chapterId))
                    .unique();
            chapter.setCacheCount((int) cacheCount);
            updateChapterCacheProgress(chapter);
            mBook = updateBookCacheProgress(bookId);
            //发送更新广播
            sendDownloadReceiver(chapter, true);
            return;
        }

        if (mLsTask.size() <= 1) {
            mLsTask.put(chapterId, startDownloadTask(chapterId));
            mBook = updateBookCacheProgress(bookId);
            sendDownloadReceiver(mBook);
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

        mBook = updateBookCacheProgress(bookId);
        sendDownloadReceiver(mBook);
    }


    /**
     * 添加多个下载任务
     */
    public void addMoreTask() {
        Log.e(TAG1, "addMoreTask: ");
        //取消第一个任务的之外的任务
        if (mLsTask.size() > 1) {
            long chapterId = 0;
            DownloadTask task = null;
            int i = 0;
            for (Map.Entry<Long, DownloadTask> entity : mLsTask.entrySet()) {
                if (i == 0) {
                    Log.e(TAG1, "addMoreTask--->cancel " + entity.getKey());
                    chapterId = entity.getKey();
                    task = entity.getValue();

                } else {
                    entity.getValue().cancel(true);
                }
                i++;
            }
            mLsTask.clear();
            mLsTask.put(chapterId, task);
        }

        List<BookChapter> chapters = mDaoSession.getBookChapterDao().queryBuilder()
                .where(BookChapterDao.Properties.CacheState.eq(Constant.DownloadState.DOWNLOADING))
                .list();

        Log.e(TAG1, "addMoreTask: lsTask:size" + mLsTask.size());

        for (int j = 0; j < chapters.size(); j++) {
            long chapterId = chapters.get(j).get_id();
            Log.e(TAG1, "addMoreTask--->put new " + chapterId);
            if (mLsTask.containsKey(chapterId)) continue;
            mLsTask.put(chapterId, startDownloadTask(chapterId));
        }
    }

    public void addBookTask(long bookId) {
        //更新章节表的状态   取消--->正在下载
        List<BookChapter> chapters = mDaoSession.getBookChapterDao()
                .queryRaw("where Book_Id = ? and Cache_State = ? ",
                        new String[]{String.valueOf(bookId),
                                String.valueOf(Constant.DownloadState.DOWNLOAD_CANCEL)});
        for (int i = 0; i < chapters.size(); i++) {
            chapters.get(i).setCacheState(Constant.DownloadState.DOWNLOADING);
        }

        mDaoSession.getBookChapterDao().updateInTx(chapters);

        //更新漫画书表的状态  取消--->正在下载
        BookShelf book = mDaoSession.getBookShelfDao().queryBuilder()
                .where(BookShelfDao.Properties._id.eq(bookId))
                .unique();
        book.setCacheState(Constant.DownloadState.DOWNLOADING);
        mDaoSession.getBookShelfDao().update(book);

        //将取消状态的任务重新开启
        for (int i = 0; i < chapters.size(); i++) {
            DownloadTask task = startDownloadTask(chapters.get(i));
            mLsTask.put(chapters.get(i).get_id(), task);
        }

    }

    private void delTask(long chapterId) {
        if (mLsTask.containsKey(chapterId)) {
            mLsTask.remove(chapterId);
        }
        updateChapterCacheState(Constant.DownloadState.DOWNLOAD_NOT, chapterId);
    }

    public boolean hasTask() {
        return mLsTask.size() != 0 ? true : false;
    }


    public class DownloadBinder extends Binder {

        public DownloadService getService() {
            return DownloadService.this;
        }

    }


}
