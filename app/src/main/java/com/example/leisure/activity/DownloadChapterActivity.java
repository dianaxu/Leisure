package com.example.leisure.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.example.leisure.MainApplication;
import com.example.leisure.R;
import com.example.leisure.activity.adapter.BaseViewHolder;
import com.example.leisure.activity.adapter.DownloadChapterAdapter;
import com.example.leisure.db.greendao.ComicChapterBean;
import com.example.leisure.db.greendao.ComicBookBean;
import com.example.leisure.eventbus.Event;
import com.example.leisure.greenDao.gen.ComicChapterBeanDao;
import com.example.leisure.greenDao.gen.DaoSession;
import com.example.leisure.receiver.DownloadReceiver;
import com.example.leisure.service.DownloadService;
import com.example.leisure.util.Constant;
import com.example.leisure.widget.CommonToolbar;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.leisure.eventbus.EventCode.Download_ADD_MORE;

/**
 * 显示漫画书章节下载页
 * 1.界面
 * 2.跳转到 选择下载章节
 * 3.列表的显示
 */
public class DownloadChapterActivity extends BaseActivity implements View.OnClickListener, DownloadReceiver.onUpdateUIListener,
        DownloadChapterAdapter.OnTaskListener {
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
    private DownloadReceiver mReceiver;
    private DownloadService mService;
    //    private boolean mIsConn;
//    private ServiceConnection mServiceConn;
    private String TAG = "DownloadTask";

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
            case Download_ADD_MORE:
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "DownloadChapterActivity---->onCreate: ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_chapter);
        mDaoSession = MainApplication.getInstance().getDaoSession();
        mService = MainApplication.getInstance().getDownloadService();
        //从本地数据库获取数据 及Intent上的数据
        getIntentData();
        getData();
        //初始化控件
        mCtbHeader = findViewById(R.id.ctb_header);
        mRlAddMore = findViewById(R.id.rl_add_more);
        mRvView = findViewById(R.id.rv_view);
        mBtnPlayPause = findViewById(R.id.btn_play_pause);
        initRecyclerView();
        //订阅事件
        mCtbHeader.setText(mBookName);
        mCtbHeader.setLeftClickListener(new CommonToolbar.OnLeftDrawableClickListener() {
            @Override
            public void onLeftDrawableClick() {
                finish();
            }
        });
        mRlAddMore.setOnClickListener(this);
        mBtnPlayPause.setOnClickListener(this);

        //更新Button 上的文字
        updateButtonText(mAdapter.hasPauseTask());
        //绑定服务器
//        bindService();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //从选择章节下载页面 增加的任务数据  需要在此进行开启任务
        Log.e(TAG, "onStart: " + mIsNewDate);
        if (mIsNewDate) {
            mIsNewDate = false;
            Log.e(TAG, "onStart-->isNewDate: ");
            //注销广播，会导致正在下载的数据  未能正确实时的告诉告诉用户
            if (mReceiver != null)
                unregisterReceiver(mReceiver);
            //先执行任务，后重新获取数据
            if (mService != null)
                mService.addMoreTask(mBookId);
            else {
                DownloadService downloadService = MainApplication.getInstance().getDownloadService();
                if (downloadService != null)
                    downloadService.addMoreTask(mBookId);
            }
            //重新获取数据
            getData();
            mAdapter.updateData(mLsData);
        }
        updateButtonText(mAdapter.hasPauseTask());
        //注册广播
        mReceiver = new DownloadReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.ReceiverAction.ACTION_DOWNLOAD);
        registerReceiver(mReceiver, filter);

        //更多章节下载
        mRlAddMore.setVisibility(hasMoreChapter() ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //解除服务绑定
        unregisterReceiver(mReceiver);
    }

    //获取数据
    private void getIntentData() {
        mBookId = getIntent().getLongExtra(EXTRA_DOWNLOAD_BOOK_ID, 0);
        mBookName = getIntent().getStringExtra(EXTRA_DOWNLOAD_BOOK_NAME);
        mIsNewDate = getIntent().getBooleanExtra(EXTRA_HAS_NEW_DATE, false);
    }

    //获取数据
    private void getData() {
        mLsData = mDaoSession.getComicChapterBeanDao().queryBuilder()
                .where(ComicChapterBeanDao.Properties.BookId.eq(mBookId),
                        ComicChapterBeanDao.Properties.CacheState.in(Constant.DownloadState.DOWNLOAD_CANCEL, Constant.DownloadState.DOWNLOADING))
                .list();


    }

    //还有章节需要下载
    private boolean hasMoreChapter() {
        int notCount = mDaoSession.getComicChapterBeanDao().queryRaw("where Cache_State = ?", String.valueOf(Constant.DownloadState.DOWNLOAD_NOT)).size();
        return notCount > 0;
    }


    //初始化RecyclerView
    private void initRecyclerView() {
        List<Long> lsPlay = new ArrayList<>();
        List<Long> lsPause = new ArrayList<>();
        for (int i = 0; i < mLsData.size(); i++) {
            if (mLsData.get(i).getCacheState() == Constant.DownloadState.DOWNLOADING)
                lsPlay.add(mLsData.get(i).get_id());
            else
                lsPause.add(mLsData.get(i).get_id());
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRvView.setLayoutManager(layoutManager);

        mAdapter = new DownloadChapterAdapter(this, mLsData);
        mAdapter.setmLsPlay(lsPlay);
        mAdapter.setmLsPlause(lsPause);
        mAdapter.addOnTaskListener(this);

        mRvView.setAdapter(mAdapter);
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

    //DownloadReceiver 更新界面的进度
    @Override
    public void updateUI(DownloadReceiver.ReceiverBean bean) {
        int position = mAdapter.getPosition(bean.chapterId);
        BaseViewHolder viewHolder = (BaseViewHolder) mRvView.findViewHolderForAdapterPosition(position);
        mAdapter.updateProgress(viewHolder, position, bean.chapterId, bean.totalCount, bean.maxCount, false);
    }

    //提示开始  因为时间太短界面显示不出来
    @Override
    public void promptStart(DownloadReceiver.ReceiverPromptBean bean) {
    }

    //提示失败  暂时未想到界面方案
    @Override
    public void promptFail(DownloadReceiver.ReceiverPromptBean bean) {
    }

    //完成章节下载
    @Override
    public void finishDown(DownloadReceiver.ReceiverBean bean) {
        Log.e(TAG, "finishDown: " + bean.chapterId);
        int position = mAdapter.getPosition(bean.chapterId);
        BaseViewHolder viewHolder = (BaseViewHolder) mRvView.findViewHolderForAdapterPosition(position);
        mAdapter.updateProgress(viewHolder, position, bean.chapterId, bean.totalCount, bean.maxCount, true);
    }

    //开启单个任务
    @Override
    public void startTask(ComicChapterBean chapter) {
        mDaoSession.getComicChapterBeanDao().update(chapter);
        mService.addTask(chapter.get_id(), chapter.getBookId());
    }

    //停止单个任务
    @Override
    public void stopTask(ComicChapterBean chapter) {
        Log.e(TAG, "stopTask: " + chapter.get_id());
        mDaoSession.getComicChapterBeanDao().update(chapter);
        mService.cancelTask(chapter, mAdapter.hasPlayTask());
    }

    //开启多个任务
    @Override
    public void startTask(List<ComicChapterBean> list) {
        mDaoSession.getComicChapterBeanDao().updateInTx(list);
        mService.addMoreTask(mBookId);
    }

    //停止多个任务
    @Override
    public void stopTask(List<ComicChapterBean> list) {
        Log.e(TAG, "stopAllTask: " + list.size());
        mDaoSession.getComicChapterBeanDao().updateInTx(list);
        mService.cancelTask(list);
    }

    //更新界面的Button的text值
    @Override
    public void updateButtonText(Boolean hasPauseTask) {
        mBtnPlayPause.setText(hasPauseTask ? "全部启动" : "全部停止");
    }

}
