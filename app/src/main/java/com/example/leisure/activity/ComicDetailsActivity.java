package com.example.leisure.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leisure.R;
import com.example.leisure.bean.ComicItemBean;
import com.example.leisure.glide.ImageLoader;
import com.example.leisure.retrofit.MyComicObserver;
import com.example.leisure.retrofit.RetrofitComicUtils;
import com.example.leisure.retrofit.RxHelper;
import com.example.leisure.widget.CommonToolbar;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 漫画详情
 */
public class ComicDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String BUNDLE_KEY_URL = "key_url";

    private String mhurl1;


    private CommonToolbar mCtbHeader;
    private ImageView mIvCover;
    private TextView mTvName, mTvAuthor, mTvTag, mTvZk, mTvIntroduce, mTvTime, mTvlatest, mTvInfo;
    private RelativeLayout mRlContent, mRlZj;
    private Button mBtnRead, mBtnAddOrRemove;


    private boolean mIsExpand = false;  //简介是否展开
    private ComicItemBean mData;        //漫画详情信息

    private MyComicObserver mObserver;


    public static void startComicDetailsActivity(Context context, String url) {
        Intent intent = new Intent(context, ComicDetailsActivity.class);
        intent.putExtra(BUNDLE_KEY_URL, url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_details);

        mCtbHeader = findViewById(R.id.ctb_header);
        mIvCover = findViewById(R.id.iv_cover);
        mTvName = findViewById(R.id.tv_name);
        mTvAuthor = findViewById(R.id.tv_author);
        mTvTag = findViewById(R.id.tv_tag);
        mTvZk = findViewById(R.id.tv_zk);
        mTvIntroduce = findViewById(R.id.tv_introduce);
        mTvTime = findViewById(R.id.tv_time);
        mTvlatest = findViewById(R.id.tv_latest);
        mTvInfo = findViewById(R.id.tv_info);
        mRlContent = findViewById(R.id.rl_content);
        mRlZj = findViewById(R.id.rl_zj);
        mBtnRead = findViewById(R.id.btn_read);
        mBtnAddOrRemove = findViewById(R.id.btn_add_or_remove);


        mCtbHeader.setLeftClickListener(new CommonToolbar.OnLeftDrawableClickListener() {
            @Override
            public void onLeftDrawableClick() {
                finish();
            }
        });
        mTvZk.setOnClickListener(this);
        mRlZj.setOnClickListener(this);
        mBtnRead.setOnClickListener(this);
        mBtnAddOrRemove.setOnClickListener(this);

        mhurl1 = getIntent().getStringExtra(BUNDLE_KEY_URL);

        getComic();
    }

    @Override
    protected void onDestroy() {
        if (mObserver != null) {
            mObserver.cancleRequest();
        }
        super.onDestroy();
    }

    /**
     * 获取漫画分类详情
     */
    private void getComic() {
        mObserver = new MyComicObserver<ComicItemBean>(this) {
            @Override
            public void onSuccess(ComicItemBean result) {
                if (result.code.contains("1")) {
                    mTvInfo.setText(result.message);
                    return;
                }
                mTvInfo.setVisibility(View.GONE);
                mRlContent.setVisibility(View.VISIBLE);
                initViewData(result);
            }

            @Override
            public void onFailure(Throwable e, String errorMsg) {
                Toast.makeText(ComicDetailsActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }

        };
        RetrofitComicUtils.getApiUrl()
                .getComicItem(mhurl1)
                .compose(RxHelper.observableIO2Main(this))
                .subscribe(mObserver);
    }

    private void initViewData(ComicItemBean bean) {
        mData = bean;

        mTvName.setText(mData.data.name);
        mTvAuthor.setText("作者：" + mData.data.author);
        mTvTag.setText("分类：" + mData.data.tag);
        mTvIntroduce.setText(mData.data.introduce);
        mTvTime.setText("最近更新：" + mData.data.time);
        mTvlatest.setText("更新至：" + mData.data.latest);

        ImageLoader.with(this, bean.data.cover, mIvCover);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.tv_zk == id) {
            expandOrHideIntroduce();
        } else if (R.id.rl_zj == id) {
            //todo 跳转到章节页
            ChapterActivity.startChapterActivity(this, mData.list, mData.data.name);
        } else if (R.id.btn_add_or_remove == id) {
            //todo 加入或者移除 漫画  在书架中 写入到数据库

        } else if (R.id.btn_read == id) {
            //todo 立即阅读 跳转到漫画详情页   后期需要进行对阅读之后的页面跳转
            ComicContentActivity.startComicContentActivity(this, mData.list, mData.list.get(0), mData.data.name);
        }
    }

    /**
     * 展开漫画简介 或者 隐藏漫画简介
     */
    private void expandOrHideIntroduce() {
        if (mIsExpand) {
            mIsExpand = false;
            mTvIntroduce.setMaxLines(2);// 收起
            mTvZk.setText("展开");
        } else {
            mIsExpand = true;
            mTvIntroduce.setMaxLines(Integer.MAX_VALUE);// 展开
            mTvZk.setText("隐藏");
        }
    }
}
