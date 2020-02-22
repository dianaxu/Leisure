package com.example.leisure.activity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.example.leisure.MainApplication;
import com.example.leisure.R;
import com.example.leisure.adapter.BaseRecyclerViewAdapter;
import com.example.leisure.adapter.BaseViewHolder;
import com.example.leisure.adapter.DownloadComicAdapter;
import com.example.leisure.db.greendao.BookShelf;
import com.example.leisure.greenDao.gen.DaoSession;
import com.example.leisure.receiver.DownloadReceiver;
import com.example.leisure.service.DownloadService;
import com.example.leisure.util.Constant;
import com.example.leisure.widget.CommonToolbar;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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
public class DownloadComicActivity extends AppCompatActivity implements BaseRecyclerViewAdapter.OnRecyclerViewItemClickListener<BookShelf>
        , DownloadComicAdapter.onTaskListener, DownloadReceiver.onUpdateUIListener, DownloadReceiver.onCancelOrAddListener {

    //初始化控件
    private RecyclerView mRvView;
    private CommonToolbar mCtbHeader;

    private DownloadComicAdapter mAdapter;
    private List<BookShelf> mLsData = new ArrayList<>();
    private boolean mBound = false;

    private DaoSession mDaoSession;
    private DownloadReceiver mReceiver;
    private DownloadService mService;
    //    private ServiceConnection mServiceConn;
    private String TAG = "DownloadTask";


    public static void startDownloadComicActivity(Context context) {
        Intent intent = new Intent(context, DownloadComicActivity.class);
        context.startActivity(intent);
    }

    /**
     * 请求手机权限结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //创建文件夹
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        File file = new File(Environment.getExternalStorageDirectory() + "/leisure/");
                        if (!file.exists()) file.mkdirs();
                    }
                    break;
                }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_comic);
        //获取本地数据库
        mDaoSession = MainApplication.getInstance().getDaoSession();
        mService = MainApplication.getInstance().getDownloadService();
        getData();
        //请求sdk卡上的文件创建权限
        ActivityCompat.requestPermissions(this, new String[]{android
                .Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

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

        //绑定服务
//        bindService();
//        if (mService == null)
//            mService = MainApplication.getInstance().getDownloadService();


        if (mService != null)
            mService.startTask();
    }

    //初始化RecyclerView
    private void initRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mRvView.setLayoutManager(layoutManager);

        mAdapter = new DownloadComicAdapter(this, mLsData);

        mRvView.setAdapter(mAdapter);
        mAdapter.addOnRecyclerViewItemClickListener(this);
        mAdapter.addnTaskListener(this);
    }

    //获取数据
    private void getData() {
        mLsData = MainApplication.getInstance().getDaoSession().getBookShelfDao().queryBuilder().list();
    }

    //注册下载广播
    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_DOWNLOAD);
        mReceiver = new DownloadReceiver(this);
        mReceiver.addOnCancelListener(this);
        registerReceiver(mReceiver, intentFilter);
    }

    private void bindService() {
//        mServiceConn = new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName name, IBinder service) {
//                DownloadService.DownloadBinder mBinder = (DownloadService.DownloadBinder) service;
//                mService = mBinder.getService();
//                mBound = true;
//            }
//
//            @Override
//            public void onServiceDisconnected(ComponentName name) {
//                mBound = false;
//            }
//        };
//        Intent intent = new Intent(this, DownloadService.class);
//        bindService(intent, mServiceConn, BIND_AUTO_CREATE);
    }
    //绑定服务

    @Override
    protected void onStart() {
        super.onStart();
        if (mService != null) {
            boolean hasTask = mService.hasTask();
            if (!hasTask) mService.startTask();
        }
    }


    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy: DownloadComicActivity");
        super.onDestroy();
        //检测任务完成 ，则关闭服务
//        if (!mService.hasTask()) {
//            mService.stopSelf();
//        }
        if (mReceiver != null)
            unregisterReceiver(mReceiver);
    }

    @Override
    public void onRecyclerViewItemClick(View view, int position, BookShelf bean) {
        int count = mDaoSession.getBookChapterDao()
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
    public void startTask(BookShelf bean) {
        //开始书本章节的任务下载
        if (mService != null)
            mService.addBookTask(bean.get_id());
    }

    @Override
    public void stopTask(BookShelf bean) {
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
