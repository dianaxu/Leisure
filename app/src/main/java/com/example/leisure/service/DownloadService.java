package com.example.leisure.service;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.leisure.MainApplication;
import com.example.leisure.R;
import com.example.leisure.db.greendao.ComicBookBean;
import com.example.leisure.db.greendao.ComicChapterBean;
import com.example.leisure.db.greendao.ComicImageBean;
import com.example.leisure.greenDao.gen.ComicBookBeanDao;
import com.example.leisure.greenDao.gen.ComicChapterBeanDao;
import com.example.leisure.greenDao.gen.ComicImageBeanDao;
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

    private ComicBookBean mBook;
    private long mChapterId = -1;
    private long mComicChapterBeanCount = 0;
    private long mLastUpdateTime = System.currentTimeMillis();             //最后更新的时间
    private long mUpdateTimeInterval = 830;  //UI更新时间的间隔

    private DownloadTask.onDownLoadInterface downLoadInterface = new DownloadTask.onDownLoadInterface() {
        @Override
        public void onStarting(long chapterId) {
            sendDownloadReceiver(Constant.DownloadReceiverState.PROMPT_START, chapterId, "开始下载");
        }

        @Override
        public void onProgressUpdate(ComicChapterBean bean, ComicImageBean detail) {
            updateCacheProgress(detail, bean);

            //发送更新广播
            sendDownloadReceiver(bean, false);
        }

        @Override
        public void onFinish(ComicChapterBean bean) {
            Log.e(TAG1, bean.get_id() + " onFinish: " + (bean.getMaxCount() == bean.getCacheCount()));
            //全部下载完
            updateChapterCacheProgress(bean);
            mBook = updateBookCacheProgress(bean.getBookId());
            //发送更新广播
            sendDownloadReceiver(bean, true);
            if (mLsTask != null && mLsTask.get(bean.get_id()) != null) {
                mLsTask.get(bean.get_id()).cancelTask();
                mLsTask.remove(bean.get_id());
            }
        }

        @Override
        public void onFail(long chapterId, boolean isConnWifi) {
            String msg = "下载失败";
            if (!isConnWifi) {
                cancelTask();
                msg = (String) getResources().getText(R.string.net_not_connected_wifi);
            }
            sendDownloadReceiver(Constant.DownloadReceiverState.PROMPT_FAIL, chapterId, msg);
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
        List<ComicChapterBean> lsChapter = mDaoSession.getComicChapterBeanDao().queryRaw("where Cache_State = ? ",
                String.valueOf(Constant.DownloadState.DOWNLOADING));
        if (lsChapter.size() == 0) {
            stopSelf();
            return;
        }

        //按照章节开始启动线程来下载
        for (int i = 0; i < lsChapter.size(); i++) {
            ComicChapterBean chapter = lsChapter.get(i);
            //情况1：章节的maxCount=cachecount
            if (chapter.getMaxCount() > 0 && chapter.getMaxCount() == chapter.getCacheState()) {
                if (mBook == null || mBook.get_id() != chapter.getBookId()) {
                    mBook = mDaoSession.getComicBookBeanDao().queryBuilder().where(ComicBookBeanDao.Properties._id.eq(chapter.getBookId())).unique();
                }

                updateChapterCacheProgress(chapter);
                mBook = updateBookCacheProgress(chapter.getBookId());
                //发送更新广播
                sendDownloadReceiver(chapter, true);
                continue;
            } else if (chapter.getMaxCount() > 0 && chapter.getMaxCount() != chapter.getCacheState()) {
                //情况2：章节的maxCount!=cachecount 但是图片已经下载完了
                long count = mDaoSession.getComicImageBeanDao().queryBuilder()
                        .where(ComicImageBeanDao.Properties.ChapterId.eq(chapter.get_id()),
                                ComicImageBeanDao.Properties.IsCaching.eq(true))
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

    private void updateCacheProgress(ComicImageBean detail, ComicChapterBean bean) {
        //更新 ComicImageBean表
        if (detail != null)
            mDaoSession.getComicImageBeanDao().update(detail);

        //初始化漫画书
        if (mBook == null || mBook.get_id() != bean.getBookId()) {
            mBook = mDaoSession.getComicBookBeanDao().queryBuilder()
                    .where(ComicBookBeanDao.Properties._id.eq(bean.getBookId()))
                    .unique();
            mComicChapterBeanCount = mDaoSession.getComicChapterBeanDao().queryBuilder()
                    .where(ComicChapterBeanDao.Properties.BookId.eq(bean.getBookId()))
                    .count();
        }

        //更新ComicChapterBean表
        mDaoSession.getComicChapterBeanDao().update(bean);

        Log.e(TAG1, "updateCacheProgress: " + bean.get_id() + ":" + bean.getCacheCount() + ":" + bean.getMaxCount());
        //完成一个图片下载 占这本书的进度
        float progress = (float) (1.0 / mComicChapterBeanCount * 1.0 / bean.getMaxCount());
        if (mBook.getProgress() == Float.POSITIVE_INFINITY || mBook.getProgress() == Float.NEGATIVE_INFINITY) {
            progress = caleBookProgress(mBook.get_id());
            mBook.setProgress(progress);
        } else
            mBook.setProgress(mBook.getProgress() + progress);


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
     * task任务完成，更新数据库
     *
     * @param bean
     */
    private void updateChapterCacheProgress(ComicChapterBean bean) {
        //更新章节完成状态  默认取消状态，方便下次进来直接开启
        bean.setCacheState(Constant.DownloadState.DOWNLOAD_CANCEL);
        if (bean.getMaxCount() > 0 && bean.getMaxCount() == bean.getCacheCount()) {
            bean.setIsCaching(true);
            bean.setCacheState(Constant.DownloadState.DOWNLOADED);
        }
        mDaoSession.getComicChapterBeanDao().update(bean);
    }

    private ComicBookBean updateBookCacheProgress(long bookId) {
        //更新漫画书的完成状态
        ComicBookBean book = mDaoSession.getComicBookBeanDao().queryBuilder()
                .where(ComicBookBeanDao.Properties._id.eq(bookId))
                .unique();
        int cacheState = Constant.DownloadState.DOWNLOADING;
        if (book.getProgress() == 1) {
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
            book.setCacheState(cacheState);
        }
        mDaoSession.update(book);
        return book;
    }

    private ComicBookBean updateComicBookBean(long bookId, int state) {
        //更新书架表的缓存数据
        ComicBookBean book = mDaoSession.getComicBookBeanDao().queryBuilder()
                .where(ComicBookBeanDao.Properties._id.eq(bookId))
                .unique();

        book.setCacheState(state);
        mDaoSession.getComicBookBeanDao().update(book);
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
        List<ComicChapterBean> chapters = mDaoSession.getComicChapterBeanDao()
                .queryRaw("where _id in (?)", whereArgStr(chapterIds));
        for (ComicChapterBean chapter : chapters) {
            chapter.setCacheState(state);
        }
        mDaoSession.getComicChapterBeanDao().updateInTx(chapters);
    }

    private List<Long> updateChapterCacheState(long... bookIds) {
        String argStr = whereArgStr();

        List<Long> chapterIds = new ArrayList<>();
        List<ComicChapterBean> chapters = mDaoSession.getComicChapterBeanDao()
                .queryRaw("where _id in (?) and Cache_State = ?",
                        new String[]{argStr, String.valueOf(Constant.DownloadState.DOWNLOADING)});
        if (chapters.size() == 0) return null;

        for (ComicChapterBean chapter : chapters) {
            chapterIds.add(chapter.get_id());
            chapter.setCacheState(Constant.DownloadState.DOWNLOAD_CANCEL);
        }
        mDaoSession.getComicChapterBeanDao().updateInTx(chapters);
        return chapterIds;
    }

    private void updateChapterCacheState() {
        List<ComicChapterBean> chapters = mDaoSession.getComicChapterBeanDao().queryBuilder()
                .where(ComicChapterBeanDao.Properties.CacheState.eq(Constant.DownloadState.DOWNLOADING))
                .list();

        for (ComicChapterBean chapter : chapters) {
            chapter.setCacheState(Constant.DownloadState.DOWNLOAD_CANCEL);
        }
        mDaoSession.getComicChapterBeanDao().updateInTx(chapters);
    }

    private void cancelAllChapterCache() {
        List<ComicChapterBean> chapters = mDaoSession.getComicChapterBeanDao().queryRaw("where Cache_State = ?"
                , new String[]{String.valueOf(Constant.DownloadState.DOWNLOADING)});
        if (chapters.size() > 0) {
            for (int i = 0; i < chapters.size(); i++) {
                chapters.get(i).setCacheState(Constant.DownloadState.DOWNLOAD_CANCEL);
            }
            mDaoSession.getComicChapterBeanDao().updateInTx(chapters);
        }
    }

    private DownloadTask startDownloadTask(long chapterId) {
        ComicChapterBean chapter = mDaoSession.getComicChapterBeanDao().queryBuilder()
                .where(ComicChapterBeanDao.Properties._id.eq(chapterId))
                .unique();
        return startDownloadTask(chapter);
    }

    //开启下载图片任务
    private DownloadTask startDownloadTask(ComicChapterBean chapter) {
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
    private void sendDownloadReceiver(ComicChapterBean bean, boolean isFinish) {
        //设置间隔更新UI界面
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastUpdateTime < mUpdateTimeInterval && bean.getCacheCount() != bean.getMaxCount() &&
                mLsTask.size() > 0 && !isFinish) return;
        mLastUpdateTime = currentTime;

        if (mBook == null) {
            mBook = mDaoSession.getComicBookBeanDao().queryBuilder().where(ComicBookBeanDao.Properties._id.eq(bean.getBookId())).unique();
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

    private void sendDownloadReceiver(ComicBookBean book) {
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
                mLsTask.get(chapterId).cancelTask();
                mLsTask.remove(chapterId);
            }
        }
    }

    //取消单个任务
    public void cancelTask(ComicChapterBean chapter, boolean hasPlayTask) {
        Log.e(TAG1, "cancelTask1: " + chapter.get_id());
        long chapterId = chapter.get_id();
        if (mLsTask.size() != 0) {
            if (mLsTask.containsKey(chapterId)) {
                Log.e(TAG1, "cancelTask1:  ok" + chapterId);
                mLsTask.get(chapterId).cancelTask();
                mLsTask.remove(chapterId);
            }
        }
        ComicBookBean book = null;
        //保存书架表的缓存数据
        if (hasPlayTask)
            book = updateComicBookBean(chapter.getBookId(), Constant.DownloadState.DOWNLOADING);
        else
            book = updateComicBookBean(chapter.getBookId(), Constant.DownloadState.DOWNLOAD_CANCEL);

        sendDownloadReceiver(book);
    }

    /**
     * 取消多个任务  一本书的所有运行的都取消
     */
    public void cancelTask(List<ComicChapterBean> chapters) {
        if (mLsTask.size() == 0) return;
        if (chapters.size() == 0) return;
        Log.e(TAG1, "-----------------------cancelTask------>:  " + mLsTask.size());
        //取消多个任务
        for (ComicChapterBean chapter : chapters) {
            long chapterId = chapter.get_id();
            Log.e(TAG1, "cancelTask------>:  " + chapterId);

            if (mLsTask.containsKey(chapterId)) {
                Log.e(TAG1, "cancelTask#######:  " + chapterId);
                mLsTask.get(chapterId).cancelTask();
                mLsTask.remove(chapterId);
            }
        }
        //更新book
        ComicBookBean book = updateComicBookBean(chapters.get(0).getBookId(), Constant.DownloadState.DOWNLOAD_CANCEL);
        //发送广播
        sendDownloadReceiver(book);
    }

    /**
     * 取消漫画书的所有的任务
     */
    public void cancelBookTask(ComicBookBean book) {
        List<ComicChapterBean> chapters = mDaoSession.getComicChapterBeanDao()
                .queryRaw("where Book_Id = ? and Cache_State = ?", new String[]{
                        String.valueOf(book.get_id()), String.valueOf(Constant.DownloadState.DOWNLOADING)
                });

        //没有可以需要取消的任务
        if (chapters.size() == 0) return;

        //将这本书的相关任务取消掉
        for (ComicChapterBean chapter : chapters) {
            long chapterId = chapter.get_id();
            chapter.setCacheState(Constant.DownloadState.DOWNLOAD_CANCEL);
            if (mLsTask.containsKey(chapterId)) {
                mLsTask.get(chapterId).cancelTask();
                mLsTask.remove(chapterId);
            }
        }

        mDaoSession.getComicChapterBeanDao().updateInTx(chapters);
        book.setCacheState(Constant.DownloadState.DOWNLOAD_CANCEL);
        mDaoSession.getComicBookBeanDao().update(book);
    }


    /**
     * 取消所有的任务
     */
    public void cancelTask() {
        if (mLsTask.size() != 0) {
            for (Map.Entry<Long, DownloadTask> entity : mLsTask.entrySet()) {
                entity.getValue().cancelTask();
            }
            mLsTask = new LinkedHashMap<>();
        }
        updateChapterCacheState();
        updateBookState2Cancel();
    }

    private void updateBookState2Cancel() {
        StringBuffer sql = new StringBuffer();
        sql.append("update  comic_book_bean set cache_state = %1$d  where _id in (");
        sql.append("select  A.book_id  from  (");
        sql.append("(select  book_id, count(*) as cancelCount  from comic_chapter_bean where cache_state = %2$d group by book_id ) as  A  ");
        sql.append(" left join ");
        sql.append("(select  book_id, count(*) as runCount  from comic_chapter_bean where cache_state = %3$d group by book_id  ) as B ");
        sql.append(" on A.book_id=B.book_id ) as C ");
        sql.append(" where cancelCount  > 0 and runCount is null );");

        String sqlStr = String.format(sql.toString(), Constant.DownloadState.DOWNLOAD_CANCEL, Constant.DownloadState.DOWNLOAD_CANCEL,
                Constant.DownloadState.DOWNLOADING);
        mDaoSession.getDatabase().execSQL(sqlStr);
    }

    /**
     * 添加下载任务
     *
     * @param chapterId
     */
    public void addTask(long chapterId, long bookId) {
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
            updateChapterCacheProgress(chapter);
            mBook = updateBookCacheProgress(bookId);
            //发送更新广播
            sendDownloadReceiver(chapter, true);
            return;
        }

        if (mLsTask.size() <= 1) {
            mLsTask.put(chapterId, startDownloadTask(chapterId));
            mBook = updateBookCacheProgress(bookId);
//            sendDownloadReceiver(mBook);
            return;
        }

        addMoreTask(bookId);
    }


    //终极下载任务
    public void addMoreTask(Long bookId) {
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
        ComicBookBean book = updateComicBookBean(bookId, Constant.DownloadState.DOWNLOADING);
        //发送广播
        sendDownloadReceiver(book);
    }

    //针对这本书中已取消的任务
    public void addMoreCanceledTask(long bookId) {
        //更新章节表的状态   取消--->正在下载
        List<ComicChapterBean> chapters = mDaoSession.getComicChapterBeanDao()
                .queryRaw("where Book_Id = ? and Cache_State = ? ",
                        new String[]{String.valueOf(bookId),
                                String.valueOf(Constant.DownloadState.DOWNLOAD_CANCEL)});
        for (int i = 0; i < chapters.size(); i++) {
            chapters.get(i).setCacheState(Constant.DownloadState.DOWNLOADING);
        }

        mDaoSession.getComicChapterBeanDao().updateInTx(chapters);

        //更新漫画书表的状态  取消--->正在下载
        ComicBookBean book = mDaoSession.getComicBookBeanDao().queryBuilder()
                .where(ComicBookBeanDao.Properties._id.eq(bookId))
                .unique();
        book.setCacheState(Constant.DownloadState.DOWNLOADING);
        mDaoSession.getComicBookBeanDao().update(book);

        addMoreTask(bookId);
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
