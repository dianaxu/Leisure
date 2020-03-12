package com.example.leisure.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.leisure.R;
import com.example.leisure.activity.presenter.ComicDetailPresenter;
import com.example.leisure.activity.view.IComicDetail;
import com.example.leisure.db.greendao.ComicBookBean;
import com.example.leisure.glide.ImageLoader;
import com.example.leisure.util.ScreenInfoUtils;
import com.example.leisure.widget.CommonToolbar;

/**
 * 漫画详情
 */
public class ComicDetailsActivity extends BaseActivity implements View.OnClickListener, IComicDetail {
    private CommonToolbar mCtbHeader;
    private ImageView mIvCover;
    private TextView mTvName, mTvAuthor, mTvTag, mTvZk, mTvIntroduce, mTvTime, mTvlatest, mTvInfo;
    private RelativeLayout mRlContent, mRlZj;
    private RelativeLayout mRlError;
    private TextView mTvRetry;
    private Button mBtnRead, mBtnAddOrRemove;


    private boolean mIsExpand = false;  //简介是否展开

    private ComicDetailPresenter mPresenter;

    public static void startComicDetailsActivity(Context context) {
        Intent intent = new Intent(context, ComicDetailsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return TransitionMode.RIGHT;
    }

    @Override
    protected boolean isHasStatusBar() {
        return true;
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
        mTvIntroduce = findViewById(R.id.tv_introduce);
        mTvTime = findViewById(R.id.tv_time);
        mTvlatest = findViewById(R.id.tv_latest);
        mTvInfo = findViewById(R.id.tv_info);
        mRlContent = findViewById(R.id.rl_content);
        mRlZj = findViewById(R.id.rl_zj);
        mBtnRead = findViewById(R.id.btn_read);
        mBtnAddOrRemove = findViewById(R.id.btn_add_or_remove);
        mRlError = findViewById(R.id.rl_error);
        mTvRetry = findViewById(R.id.tv_retry);

        mCtbHeader.setLeftClickListener(new CommonToolbar.OnLeftDrawableClickListener() {
            @Override
            public void onLeftDrawableClick() {
                finish();
            }
        });
        mRlZj.setOnClickListener(this);
        mBtnRead.setOnClickListener(this);
        mBtnAddOrRemove.setOnClickListener(this);
        mTvRetry.setOnClickListener(this);

        //竖屏 初始化控件
        if (!ScreenInfoUtils.isWindowOrientationLand(this)) {
            initWindowPort();
        }

        mPresenter = new ComicDetailPresenter(this, savedInstanceState, this);
        //查询书是否已经加入到书架中
        mBtnAddOrRemove.setText(mPresenter.isAddBookShelf() ? "移除书架" : "加入书架");
        mPresenter.getComicDetail();
    }

    private void initWindowPort() {
        mTvZk = findViewById(R.id.tv_zk);
        mTvZk.setOnClickListener(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mPresenter.savedInstanceState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        mPresenter.cancleRequest();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.tv_zk == id) {
            expandOrHideIntroduce();
        } else if (R.id.rl_zj == id) {
            //跳转到章节页
            ComicBookBean bean = mPresenter.getComicBook();
            ChapterActivity.startChapterActivity(this, bean);
        } else if (R.id.btn_add_or_remove == id) {
            //已存在书架中  需移除
            if (mPresenter.isAddBookShelf()) {
                mPresenter.removeComicToDB();
            } else { //没有在书架中  需要加入
                mPresenter.addComicToDB();
            }
        } else if (R.id.btn_read == id) {
            //todo 立即阅读 跳转到漫画详情页   后期需要进行对阅读之后的页面跳转
            ComicBookBean bean = mPresenter.getComicBook();
            ComicContentActivity.startComicContentActivity(this, bean);
        } else if (R.id.tv_retry == id) {
            mPresenter.getComicDetail();
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

    @Override
    public void updateUI(String bookName, String bookCover) {
        ImageLoader.with(this, bookCover, mIvCover);
        mTvName.setText(bookName);
    }

    @Override
    public void updateUI(ComicBookBean bean) {
        mRlError.setVisibility(View.GONE);
        mRlContent.setVisibility(View.VISIBLE);

        mTvName.setText(bean.name);
        mTvAuthor.setText("作者：" + bean.author);
        mTvTag.setText("分类：" + bean.tag);
        mTvIntroduce.setText(bean.introduce);
        mTvTime.setText("最近更新：" + bean.time);
        mTvlatest.setText("更新至：" + bean.latest);

        ImageLoader.with(this, bean.cover, mIvCover);
    }

    @Override
    public void onFailure(String errorMsg) {
        mRlError.setVisibility(View.VISIBLE);
        mTvInfo.setText(errorMsg);
    }

    @Override
    public void onAddComicToDBSuccess() {
        mBtnAddOrRemove.setText("移除书架");
    }

    @Override
    public void onRemoveComicToDBSuccess() {
        mBtnAddOrRemove.setText("加入书架");
    }
}
