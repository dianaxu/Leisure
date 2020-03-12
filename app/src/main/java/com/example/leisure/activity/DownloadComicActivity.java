package com.example.leisure.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;

import com.example.leisure.MainApplication;
import com.example.leisure.R;
import com.example.leisure.activity.adapter.BaseRecyclerViewAdapter;
import com.example.leisure.activity.adapter.BaseViewHolder;
import com.example.leisure.activity.adapter.DownloadComicAdapter;
import com.example.leisure.db.greendao.ComicBookBean;
import com.example.leisure.greenDao.gen.DaoSession;
import com.example.leisure.receiver.DownloadReceiver;
import com.example.leisure.service.DownloadService;
import com.example.leisure.util.Constant;
import com.example.leisure.util.ScreenInfoUtils;
import com.example.leisure.widget.CommonToolbar;
import com.example.leisure.widget.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.leisure.util.Constant.ReceiverAction.ACTION_DOWNLOAD;

/**
 * 缓存管理
 * 1:界面  ok
 * 2.列表显示 ok
 * 3.图片上水波显示进度 ok
 * 4.可以进行 暂停漫画任务下载
 * 5.可以进行 开启漫画任务下载
 * 6.实现DownloadService的binder 进行章节下载后通知界面显示水波的更新
 */
public class DownloadComicActivity extends BaseActivity implements BaseRecyclerViewAdapter.OnItemClickListener<ComicBookBean>
        , DownloadComicAdapter.onTaskListener, DownloadReceiver.onUpdateUIListener, DownloadReceiver.onCancelOrAddListener {
    private static final int SPAN_COUNT_LAND = 6;
    private static final int SPAN_COUNT_PORT = 3;
    private static final int SPACING = 16;
    //初始化控件
    private RecyclerView mRvView;
    private CommonToolbar mCtbHeader;

    private DownloadComicAdapter mAdapter;
    private List<ComicBookBean> mLsData = new ArrayList<>();
    private int mSpanCount;

    private DaoSession mDaoSession;
    private DownloadReceiver mReceiver;
    private DownloadService mService;


    public static void startDownloadComicActivity(Context context) {
        Intent intent = new Intent(context, DownloadComicActivity.class);
        context.startActivity(intent);
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_comic);
        //获取本地数据库
        mDaoSession = MainApplication.getInstance().getDaoSession();
        mService = MainApplication.getInstance().getDownloadService();
        getData();
        mSpanCount = ScreenInfoUtils.isWindowOrientationLand(this) ? SPAN_COUNT_LAND : SPAN_COUNT_PORT;

        //初始化控件
        mRvView = findViewById(R.id.rv_view);
        mCtbHeader = findViewById(R.id.ctb_header);
        initRecyclerView();


        //订阅事件
        mCtbHeader.setLeftClickListener(new CommonToolbar.OnLeftDrawableClickListener() {
            @Override
            public void onLeftDrawableClick() {
                finish();
            }
        });

        //注册广播
        registerReceiver();

        if (mService != null)
            mService.startTask();
    }

    //初始化RecyclerView
    private void initRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, mSpanCount);
        mRvView.setLayoutManager(layoutManager);
        mRvView.addItemDecoration(new GridSpacingItemDecoration(mSpanCount, SPACING, true));
        mAdapter = new DownloadComicAdapter(this, mLsData);

        mRvView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mAdapter.addnTaskListener(this);
    }

    //获取数据
    private void getData() {
        mLsData = MainApplication.getInstance().getDaoSession().getComicBookBeanDao().queryBuilder().list();
    }

    //注册下载广播
    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_DOWNLOAD);
        mReceiver = new DownloadReceiver(this);
        mReceiver.addOnCancelListener(this);
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mService != null) {
            boolean hasTask = mService.hasTask();
            if (!hasTask) mService.startTask();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mReceiver != null)
            unregisterReceiver(mReceiver);
    }


    @Override
    public void onItemClick(View view, int position, ComicBookBean bean) {
        int count = mDaoSession.getComicChapterBeanDao()
                .queryRaw("where Book_Id = ? and Cache_State in (?,?)",
                        new String[]{bean.get_id().toString(),
                                String.valueOf(Constant.DownloadState.DOWNLOAD_CANCEL),
                                String.valueOf(Constant.DownloadState.DOWNLOADING)})
                .size();
        if (count == 0)
            DownloadChooseChapterActivity.startDownloadChooseChapterActivity(this, bean.get_id(), bean.getName());
        else
            DownloadChapterActivity.startDownloadChapterActivity(this, bean.get_id(), bean.getName());
    }

    @Override
    public void startTask(ComicBookBean bean) {
        //开始书本章节的任务下载
        if (mService != null)
            mService.addMoreCanceledTask(bean.get_id());
    }

    @Override
    public void stopTask(ComicBookBean bean) {
        //暂停书本章节的任务下载
        if (mService != null)
            mService.cancelBookTask(bean);
    }

    @Override
    public void updateUI(DownloadReceiver.ReceiverBean bean) {
        int position = mAdapter.getPosition(bean.bookId);
        BaseViewHolder viewHolder = (BaseViewHolder) mRvView.findViewHolderForAdapterPosition(position);
        mAdapter.updateProgress(viewHolder, position, bean.bookId, bean.bookProgress, bean.bookCacheState, false);
    }

    @Override
    public void promptStart(DownloadReceiver.ReceiverPromptBean bean) {

    }

    @Override
    public void promptFail(DownloadReceiver.ReceiverPromptBean bean) {

    }

    @Override
    public void finishDown(DownloadReceiver.ReceiverBean bean) {
        int position = mAdapter.getPosition(bean.bookId);
        BaseViewHolder viewHolder = (BaseViewHolder) mRvView.findViewHolderForAdapterPosition(position);
        mAdapter.updateProgress(viewHolder, position, bean.bookId, bean.bookProgress, bean.bookCacheState, true);
    }


    @Override
    public void cancelOrAddTask(DownloadReceiver.ReceiverCancelBean bean) {
        int position = mAdapter.getPosition(bean.bookId);
        BaseViewHolder viewHolder = (BaseViewHolder) mRvView.findViewHolderForAdapterPosition(position);
        mAdapter.updateState(viewHolder, position, bean.bookState);
    }
}
