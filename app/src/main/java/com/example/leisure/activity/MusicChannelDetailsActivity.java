package com.example.leisure.activity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.danikula.videocache.HttpProxyCacheServer;
import com.example.leisure.MainApplication;
import com.example.leisure.R;
import com.example.leisure.adapter.BaseRecyclerViewAdapter;
import com.example.leisure.adapter.MusicChannelDetailsAdapter;
import com.example.leisure.bean.MusicBroadcastingDetailsBean;
import com.example.leisure.glide.ImageLoader;
import com.example.leisure.retrofit.MyObserver;
import com.example.leisure.retrofit.RetrofitUtils;
import com.example.leisure.retrofit.RxHelper;
import com.example.leisure.util.DensityUtil;
import com.example.leisure.widget.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 音乐电台详情页
 * 1.实现xml
 * 2.播放音乐
 * 3.停止音乐
 * 4.界面的展示
 * 5.需要实现下载音乐
 * 6.实现后台服务进行播放
 * 7.实现缓冲效果
 * <p>
 * 音乐电台详情接口： https://api.apiopen.top/musicBroadcastingDetails?channelname=public_tuijian_spring
 */
public class MusicChannelDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    public static String EXTRA_CHNAME = "extra_chname";
    public static String EXTRA_NAME = "extra_name";
    public static String EXTRA_THUMB = "extra_thumb";

    private RecyclerView mRvView;
    private ImageView mIvImage;
    private ImageView mIvBack;
    private TextView mTvTitle;
    private RelativeLayout mRlPlayAll;
    private ImageView mIvHead;
    private TextView mTvSong;
    private TextView mTvArtist;
    private ImageView mIvPlayStop;


    private String mChname;
    private String mThumb;
    private String mName;

    private MyObserver mObserver;
    private MusicChannelDetailsAdapter mAdapter;
    private List<MusicBroadcastingDetailsBean.SonglistBean> mLsData;

    public static void startMusicChannelDetailsActivity(Context context, String chname, String thumb, String name) {
        Intent intent = new Intent(context, MusicChannelDetailsActivity.class);
        intent.putExtra(EXTRA_CHNAME, chname);
        intent.putExtra(EXTRA_THUMB, thumb);
        intent.putExtra(EXTRA_NAME, name);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_channel_details);
        mLsData = new ArrayList<>();
        getExtraData();

        mRvView = findViewById(R.id.rv_view);
        mIvImage = findViewById(R.id.iv_image);
        mIvBack = findViewById(R.id.iv_back);
        mTvTitle = findViewById(R.id.tv_title);
        mRlPlayAll = findViewById(R.id.rl_play_all);
        mIvHead = findViewById(R.id.iv_head);
        mTvSong = findViewById(R.id.tv_song);
        mTvArtist = findViewById(R.id.tv_artist);
        mIvPlayStop = findViewById(R.id.iv_play_stop);


        initRecyclerView();
        initMedioPlay();

        mTvTitle.setText(mName);
        ImageLoader.with(this, mThumb, mIvImage);

        mIvBack.setOnClickListener(this);
        mRlPlayAll.setOnClickListener(this);
        mIvPlayStop.setOnClickListener(this);

        //获取页面数据
        getData();
    }

    private void initMedioPlay() {
        MainApplication myApplication;
        HttpProxyCacheServer proxy;
        String proxyUrl;

//        RenderersFactory renderersFactory = new DefaultRenderersFactory(this);
//        DefaultTrackSelector trackSelector = new DefaultTrackSelector();
//        LoadControl loadControl = new DefaultLoadControl();
//        mPlayer = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);

        myApplication = (MainApplication) getApplication();
        proxy = myApplication.getProxy(MusicChannelDetailsActivity.this);

    }

    private void initRecyclerView() {

        int spacing = DensityUtil.dip2px(MusicChannelDetailsActivity.this, 5);
        mRvView.addItemDecoration(new SpacesItemDecoration(spacing));

        LinearLayoutManager layoutManager = new LinearLayoutManager(MusicChannelDetailsActivity.this);
        mRvView.setLayoutManager(layoutManager);

        mAdapter = new MusicChannelDetailsAdapter(MusicChannelDetailsActivity.this, mLsData);
        mRvView.setAdapter(mAdapter);

        mAdapter.addOnRecyclerViewItemClickListener(new BaseRecyclerViewAdapter.OnRecyclerViewItemClickListener<MusicBroadcastingDetailsBean.SonglistBean>() {
            @Override
            public void onRecyclerViewItemClick(View view, int position, MusicBroadcastingDetailsBean.SonglistBean bean) {
                //启动后台播放，播放单个视频，跳转到详情页
                MusicDetailsActivity.startMusicDetailsActivity(MusicChannelDetailsActivity.this, bean.songid);
            }
        });
    }


    private void getExtraData() {
        Intent intent = getIntent();
        mChname = intent.getStringExtra(EXTRA_CHNAME);
        mThumb = intent.getStringExtra(EXTRA_THUMB);
        mName = intent.getStringExtra(EXTRA_NAME);
    }

    private void getData() {
        mObserver = new MyObserver<MusicBroadcastingDetailsBean>(this) {
            @Override
            public void onSuccess(MusicBroadcastingDetailsBean result) {
                if (result != null && result.songlist != null) {
                    mAdapter.refreshData(result.songlist);
                }
            }

            @Override
            public void onFailure(Throwable e, String errorMsg) {
                Toast.makeText(MusicChannelDetailsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
        RetrofitUtils.getApiUrl()
                .musicBroadcastingDetails(mChname)
                .compose(RxHelper.observableIO2Main(this))
                .subscribe(mObserver);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.iv_back == id) {
            finish();
        } else if (R.id.rl_play_all == id) {
            playAll();

        } else if (R.id.iv_play_stop == id) {

        }
    }

    private MediaPlayer mediaPlayer;

    private void playAll() {
        //todo 全部播放
        String url = "http://audio04.dmhmusic.com/71_53_T10041165821_128_4_1_0_sdk-cpm/cn/0208/M00/31/84/ChR461pkYjuALHeRAEEW2lcE6oY561.mp3?xcode=ceb3b04299ba922d59b2f1dc5142e03bd5e78d1";


    }
}
