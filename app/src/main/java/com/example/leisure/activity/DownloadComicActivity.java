package com.example.leisure.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.leisure.MainApplication;
import com.example.leisure.R;
import com.example.leisure.activity.adapter.BaseRecyclerViewAdapter;
import com.example.leisure.activity.adapter.DownloadComicAdapter;
import com.example.leisure.db.greendao.ComicBookBean;
import com.example.leisure.greenDao.gen.DaoSession;
import com.example.leisure.service.DownloadService;
import com.example.leisure.service.IDownloadBookCallback;
import com.example.leisure.util.Constant;
import com.example.leisure.util.ScreenInfoUtils;
import com.example.leisure.widget.CommonToolbar;
import com.example.leisure.widget.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.example.leisure.activity.adapter.DownloadComicAdapter.UPDATE_ITEM_FINISH;
import static com.example.leisure.activity.adapter.DownloadComicAdapter.UPDATE_ITEM_PROGRESS;
import static com.example.leisure.activity.adapter.DownloadComicAdapter.UPDATE_ITEM_STATE;

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
        , DownloadComicAdapter.onTaskListener, IDownloadBookCallback {
    private String TAG = "DownloadComicActivity";
    private static final int SPAN_COUNT_LAND = 5;
    private static final int SPAN_COUNT_PORT = 3;
    private static final int SPACING = 8;
    //初始化控件
    private RecyclerView mRvView;
    private CommonToolbar mCtbHeader;

    private DownloadComicAdapter mAdapter;
    private List<ComicBookBean> mLsData = new ArrayList<>();
    private int mSpanCount;

    private DaoSession mDaoSession;
    private DownloadService mService;
    private ServiceConnection mConn;
    private boolean isFront = true;
    private boolean hasStopService = false;


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

    //获取数据
    private void getData() {
        mDaoSession.clear();
        mLsData = mDaoSession.getComicBookBeanDao().queryBuilder().list();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "book----> onCreate: ");
        //获取本地数据库
        mDaoSession = MainApplication.getInstance().getDaoSession();
        mSpanCount = ScreenInfoUtils.isWindowOrientationLand(this) ? SPAN_COUNT_LAND : SPAN_COUNT_PORT;
        setContentView(R.layout.activity_download_comic);
        getData();

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
        //开启服务及绑定服务
        startService();
    }

    //初始化RecyclerView
    private void initRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, mSpanCount);
        mRvView.setLayoutManager(layoutManager);
        mRvView.addItemDecoration(new GridSpacingItemDecoration(mSpanCount, SPACING, false));
        mAdapter = new DownloadComicAdapter(this, mLsData);

        mRvView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mAdapter.addnTaskListener(this);
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
                mService.registerCallBack(DownloadComicActivity.this);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mService.unRegisterCallBack(DownloadComicActivity.this);
                mService = null;
            }
        };
        bindService(intent, mConn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getData();
        mAdapter.updateData(mLsData);
        Log.e(TAG, "book----> onStart: " + mLsData.get(0).getCacheState());
        if (mService != null)
            mService.registerCallBack(DownloadComicActivity.this);
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
        Log.e(TAG, "book----> onStop: ");
        if (!hasStopService)
            if (mService != null) {
                mService.unRegisterCallBack(this);
            }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "book----> onDestroy: ");
        if (!hasStopService)
            if (mService != null)
                unbindService(mConn);
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
        String sql = updateChapterStateStr(bean.get_id(), Constant.DownloadState.DOWNLOAD_CANCEL, Constant.DownloadState.DOWNLOADING);
        mDaoSession.getDatabase().execSQL(sql);

        bean.setCacheState(Constant.DownloadState.DOWNLOADING);
        mDaoSession.update(bean);
        //开始书本章节的任务下载
        if (mService != null)
            mService.addTask(bean.get_id());
    }

    @Override
    public void stopTask(ComicBookBean bean) {
        String sql = updateChapterStateStr(bean.get_id(), Constant.DownloadState.DOWNLOADING, Constant.DownloadState.DOWNLOAD_CANCEL);
        mDaoSession.getDatabase().execSQL(sql);

        bean.setCacheState(Constant.DownloadState.DOWNLOAD_CANCEL);
        mDaoSession.update(bean);
        //暂停书本章节的任务下载
        if (mService != null)
            mService.cancelChaptersTask(bean.get_id());
    }

    private String updateChapterStateStr(long bookId, int oldState, int newState) {
        StringBuffer sqlStr = new StringBuffer();
        sqlStr.append("update Comic_chapter_bean set cache_state = " + newState);
        sqlStr.append(" where book_id = " + bookId);
        sqlStr.append(" and cache_state = " + oldState);
        return sqlStr.toString();
    }

    @Override
    public void onNotConnWifi() {
        getData();
        mAdapter.updateData(mLsData);
        Toast.makeText(DownloadComicActivity.this,
                getResources().getText(R.string.net_not_connected_wifi),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFinishStopSelf() {
        hasStopService = true;
    }

    @Override
    public void onUpdateProgressBook(long bookId, float progress) {
        int position = mAdapter.getPosition(bookId);
        if (position == -1) return;
        mLsData.get(position).setProgress(progress);
        mAdapter.notifyItemChanged(position, UPDATE_ITEM_PROGRESS);
    }

    @Override
    public void onFinishBook(long bookId, float progress, int state) {
        int position = mAdapter.getPosition(bookId);
        if (position == -1) return;
        mLsData.get(position).setProgress(progress);
        mLsData.get(position).setCacheState(state);
        mAdapter.notifyItemChanged(position, UPDATE_ITEM_FINISH);
    }

    @Override
    public void onUpdateBookState(long bookId, int state) {
        int position = mAdapter.getPosition(bookId);
        if (position == -1) return;
        mLsData.get(position).setCacheState(state);
        mAdapter.notifyItemChanged(position, UPDATE_ITEM_STATE);
    }
}
