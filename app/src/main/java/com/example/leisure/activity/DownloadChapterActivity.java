package com.example.leisure.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.leisure.MainApplication;
import com.example.leisure.R;
import com.example.leisure.activity.adapter.DownloadChapterAdapter;
import com.example.leisure.db.greendao.ComicBookBean;
import com.example.leisure.db.greendao.ComicChapterBean;
import com.example.leisure.eventbus.Event;
import com.example.leisure.greenDao.gen.ComicChapterBeanDao;
import com.example.leisure.greenDao.gen.DaoSession;
import com.example.leisure.service.DownloadService;
import com.example.leisure.service.IDownloadChapterCallback;
import com.example.leisure.util.Constant;
import com.example.leisure.widget.CommonToolbar;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.leisure.activity.adapter.DownloadChapterAdapter.UPDATE_ITEM_FAIL;
import static com.example.leisure.activity.adapter.DownloadChapterAdapter.UPDATE_ITEM_PROGRESS;
import static com.example.leisure.eventbus.EventCode.DOWNLOAD_ADD_MORE;

/**
 * 显示漫画书章节下载页
 * 1.界面
 * 2.跳转到 选择下载章节
 * 3.列表的显示
 */
public class DownloadChapterActivity extends BaseActivity implements View.OnClickListener,
        DownloadChapterAdapter.OnTaskListener, IDownloadChapterCallback {
    public static final String EXTRA_DOWNLOAD_BOOK_ID = "download_book_id";
    public static final String EXTRA_DOWNLOAD_BOOK_NAME = "download_book_name";
    public static final String EXTRA_HAS_NEW_DATE = "extra_has_new_date";

    private long mBookId;
    private String mBookName;
    private boolean mIsNewDate;

    private List<ComicChapterBean> mLsData;

    private CommonToolbar mCtbHeader;
    private RelativeLayout mRlAddMore;
    private RecyclerView mRvView;
    private Button mBtnPlayPause;
    private DownloadChapterAdapter mAdapter;


    private DaoSession mDaoSession;
    private ComicBookBean mBook;
    private DownloadService mService;
    private ServiceConnection mConn;
    private String TAG = "DownloadTask";
    private boolean isFront;
    private boolean hasStopService = false;

    //开启Activity
    public static void startDownloadChapterActivity(Context context, long bookId, String bookName) {
        Intent intent = new Intent(context, DownloadChapterActivity.class);
        intent.putExtra(EXTRA_DOWNLOAD_BOOK_ID, bookId);
        intent.putExtra(EXTRA_DOWNLOAD_BOOK_NAME, bookName);
        context.startActivity(intent);
    }

    public static void startDownloadChapterActivity(Context context, long bookId, String bookName, boolean isNewDate) {
        Intent intent = new Intent(context, DownloadChapterActivity.class);
        intent.putExtra(EXTRA_DOWNLOAD_BOOK_ID, bookId);
        intent.putExtra(EXTRA_DOWNLOAD_BOOK_NAME, bookName);
        intent.putExtra(EXTRA_HAS_NEW_DATE, isNewDate);
        context.startActivity(intent);
    }

    //注册EventBus
    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected void receiveEvent(Event event) {
        Log.e(TAG, "receiveEvent: ");
        switch (event.getCode()) {
            case DOWNLOAD_ADD_MORE:
                Log.e(TAG, "receiveEvent---->Download_ADD_MORE");
                mIsNewDate = true;
                break;
        }
    }

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return TransitionMode.LEFT;
    }

    @Override
    protected boolean isHasStatusBar() {
        return true;
    }

    //获取数据
    private void getIntentData() {
        mBookId = getIntent().getLongExtra(EXTRA_DOWNLOAD_BOOK_ID, 0);
        mBookName = getIntent().getStringExtra(EXTRA_DOWNLOAD_BOOK_NAME);
        mIsNewDate = getIntent().getBooleanExtra(EXTRA_HAS_NEW_DATE, false);
    }

    //获取数据
    private void getData() {
        mDaoSession.clear();
        mLsData = mDaoSession.getComicChapterBeanDao().queryBuilder()
                .where(ComicChapterBeanDao.Properties.BookId.eq(mBookId),
                        ComicChapterBeanDao.Properties.CacheState.in(Constant.DownloadState.DOWNLOADING,
                                Constant.DownloadState.DOWNLOAD_CANCEL))
                .list();

        List<Long> lsPlay = new ArrayList<>();
        List<Long> lsPause = new ArrayList<>();
        for (int i = 0; i < mLsData.size(); i++) {
            if (mLsData.get(i).getCacheState() == Constant.DownloadState.DOWNLOADING)
                lsPlay.add(mLsData.get(i).get_id());
            else
                lsPause.add(mLsData.get(i).get_id());
        }
        mAdapter.setmLsPlay(lsPlay);
        mAdapter.setmLsPlause(lsPause);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "DownloadChapterActivity---->onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_chapter);
        mDaoSession = MainApplication.getInstance().getDaoSession();
        //从本地数据库获取数据 及Intent上的数据
        getIntentData();

        //初始化控件
        mCtbHeader = findViewById(R.id.ctb_header);
        mRlAddMore = findViewById(R.id.rl_add_more);
        mRvView = findViewById(R.id.rv_view);
        mBtnPlayPause = findViewById(R.id.btn_play_pause);
        initRecyclerView();
        //订阅事件

        mCtbHeader.setLeftClickListener(new CommonToolbar.OnLeftDrawableClickListener() {
            @Override
            public void onLeftDrawableClick() {
                DownloadComicActivity.startDownloadComicActivity(DownloadChapterActivity.this);
            }
        });
        mRlAddMore.setOnClickListener(this);
        mBtnPlayPause.setOnClickListener(this);

        mCtbHeader.setText(mBookName);
        //更新Button 上的文字
        updateButtonText(mAdapter.hasPauseTask());
        //开启服务及绑定服务器
        startService();
    }

    //还有章节需要下载
    private boolean hasMoreChapter() {
        int notCount = mDaoSession.getComicChapterBeanDao().queryRaw("where Cache_State = ? and book_id = ?",
                String.valueOf(Constant.DownloadState.DOWNLOAD_NOT), String.valueOf(mBookId)).size();
        return notCount > 0;
    }


    //初始化RecyclerView
    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRvView.setLayoutManager(layoutManager);

        mAdapter = new DownloadChapterAdapter(this, mLsData);
        mAdapter.addOnTaskListener(this);
        mRvView.setAdapter(mAdapter);
    }


    //开启服务及绑定服务
    private void startService() {
        Intent intent = new Intent(this, DownloadService.class);
        startService(intent);
        mConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                DownloadService.DownloadBinder binder = (DownloadService.DownloadBinder) service;
                mService = binder.getService();
                mService.registerCallBack(DownloadChapterActivity.this);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService.unRegisterCallBack(DownloadChapterActivity.this);
                mService = null;
            }
        };
        bindService(intent, mConn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mService != null)
            mService.registerCallBack(DownloadChapterActivity.this);

        //从选择章节下载页面 增加的任务数据  需要在此进行开启任务
        if (mIsNewDate) {
            mIsNewDate = false;

            //先执行任务，后重新获取数据
            if (mService != null)
                mService.addTask(mBookId);
        }

        //重新获取数据
        getData();
        mAdapter.updateData(mLsData);
        updateButtonText(mAdapter.hasPauseTask());

        //更多章节下载
        mRlAddMore.setVisibility(hasMoreChapter() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        isFront = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isFront = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!hasStopService)
            if (mService != null) {
                mService.unRegisterCallBack(this);
            }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!hasStopService)
            if (mService != null)
                unbindService(mConn);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.rl_add_more == id) {
            jumpToDownloadChooseChapterActivity();
        } else if (R.id.btn_play_pause == id) {
            mAdapter.playOrPauseAll();
        }
    }

    //跳转到选择章节下载页
    private void jumpToDownloadChooseChapterActivity() {
        Intent intent = new Intent(this, DownloadChooseChapterActivity.class);
        intent.putExtra(DownloadChooseChapterActivity.EXTRA_BOOK_ID, mBookId);
        startActivity(intent);
    }

    private void updateBookState(long bookId, int state) {
        String sql = "update comic_book_bean set cache_state = " + state + " where _id = " + bookId;
        mDaoSession.getDatabase().execSQL(sql);
    }

    //开启单个任务
    @Override
    public void startTask(ComicChapterBean chapter) {
        Log.e(TAG, "chapter-----> stopTask: " + chapter.getCacheState());
        mDaoSession.getComicChapterBeanDao().update(chapter);
        updateBookState(chapter.getBookId(), Constant.DownloadState.DOWNLOADING);
        mService.addTask(chapter.get_id(), chapter.getBookId());
    }

    //停止单个任务
    @Override
    public void stopTask(ComicChapterBean chapter) {
        Log.e(TAG, "chapter-----> stopTask: " + chapter.getCacheState());
        mDaoSession.getComicChapterBeanDao().update(chapter);
        if (!mAdapter.hasPlayTask()) {
            updateBookState(chapter.getBookId(), Constant.DownloadState.DOWNLOAD_CANCEL);
        }
        mService.cancelChapterTask(chapter.getBookId(), chapter.get_id());
    }

    //开启多个任务
    @Override
    public void startTask(List<ComicChapterBean> list) {
        mDaoSession.getComicChapterBeanDao().updateInTx(list);
        if (!mAdapter.hasPlayTask()) {
            updateBookState(mBookId, Constant.DownloadState.DOWNLOADING);
        }
        mService.addTask(mBookId);
    }

    //停止多个任务
    @Override
    public void stopTask(List<ComicChapterBean> list) {
        Log.e(TAG, "stopAllTask: " + list.size());
        mDaoSession.getComicChapterBeanDao().updateInTx(list);
        updateBookState(mBookId, Constant.DownloadState.DOWNLOAD_CANCEL);
        mService.cancelChaptersTask(mBookId, list);
    }

    //更新界面的Button的text值
    @Override
    public void updateButtonText(Boolean hasPauseTask) {
        mBtnPlayPause.setText(hasPauseTask ? "全部启动" : "全部停止");
    }

    @Override
    public void onNotConnWifi() {
        getData();
        mAdapter.updateData(mLsData);
        updateButtonText(mAdapter.hasPauseTask());
        Toast.makeText(DownloadChapterActivity.this,
                getResources().getText(R.string.net_not_connected_wifi),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFinishStopSelf() {
        hasStopService = true;
    }

    @Override
    public void onChapterProgress(long chapterId, int cacheCount, int maxCount) {
        int position = mAdapter.getPosition(chapterId);
        if (position == -1) return;
        mLsData.get(position).setCacheCount(cacheCount);
        mLsData.get(position).setMaxCount(maxCount);
        mAdapter.notifyItemChanged(position, UPDATE_ITEM_PROGRESS);
    }

    @Override
    public void onChapterFinish(long chapterId, int status) {
        int position = mAdapter.getPosition(chapterId);
        if (position == -1) return;
        if (status == Constant.DownloadState.DOWNLOADED) {
            Log.e(TAG, "onChapterFinish: status:" + status);
            mLsData.remove(position);
            mAdapter.notifyItemRemoved(position);
        } else {
            mAdapter.updateState(chapterId,position, status);
        }
        updateButtonText(mAdapter.hasPauseTask());
    }

    @Override
    public void onFailure(long chapterId, String msg) {
        int position = mAdapter.getPosition(chapterId);
        if (position == -1) return;
        mLsData.get(position).setCacheState(Constant.DownloadState.DOWNLOAD_CANCEL);
        mAdapter.notifyItemChanged(position, UPDATE_ITEM_FAIL);
    }

    @Override
    public void onSuccessCancel(long chapterId) {

    }
}
