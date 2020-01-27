package com.example.leisure.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.leisure.R;
import com.example.leisure.bean.MusicDetailsBean;
import com.example.leisure.retrofit.MyObserver;
import com.example.leisure.retrofit.RetrofitUtils;
import com.example.leisure.retrofit.RxHelper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 音乐详情页
 * 1.实现xml
 * 2.播放音乐
 * 3.停止音乐
 * 4.界面的展示
 * 5.需要实现下载音乐
 * 6.实现后台服务进行播放
 * 7.实现缓冲效果
 * <p>
 * 音乐电台详情接口：  https://api.apiopen.top/musicDetails?id=435225
 */
public class MusicDetailsActivity extends AppCompatActivity {
    public static final String EXTRA_SONGID = "extra_songid";

    private String mSongId;
    private MyObserver mObserver;

    public static void startMusicDetailsActivity(Context context, String songId) {
        Intent intent = new Intent(context, MusicDetailsActivity.class);
        intent.putExtra(EXTRA_SONGID, songId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_details);
        getExtraData();



    }


    private void getExtraData() {
        Intent intent = getIntent();
        mSongId = intent.getStringExtra(EXTRA_SONGID);
    }

    private void getData() {
        mObserver = new MyObserver<MusicDetailsBean>(this) {
            @Override
            public void onSuccess(MusicDetailsBean result) {
                if (result != null) {

                }
            }

            @Override
            public void onFailure(Throwable e, String errorMsg) {
                Toast.makeText(MusicDetailsActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
        RetrofitUtils.getApiUrl()
                .musicDetails(mSongId)
                .compose(RxHelper.observableIO2Main(this))
                .subscribe(mObserver);
    }

}
