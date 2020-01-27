package com.example.leisure.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leisure.R;
import com.example.leisure.adapter.BaseRecyclerViewAdapter;
import com.example.leisure.adapter.BaseViewHolder;
import com.example.leisure.bean.ComicContentBean;
import com.example.leisure.bean.ComicItemBean;
import com.example.leisure.fragment.ChapterFragment;
import com.example.leisure.glide.ImageLoader;
import com.example.leisure.retrofit.MyComicObserver;
import com.example.leisure.retrofit.RetrofitComicUtils;
import com.example.leisure.retrofit.RxHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 漫画内容
 * <p>
 * 1.实现漫画内容展示  ok
 * 2.左侧显示章节
 * 3.缓存之后的章节（包括当前的章节）
 * 4.全屏预览 ok
 */
public class ComicContentActivity extends BaseActivity implements View.OnClickListener, ChapterFragment.OnToolbarListener {
    public static final String BUNDLE_KEY_CHAPTERS = "key_chapters";   //所有的章节
    public static final String BUNDLE_KEY_CURRENT_CHAPTER = "key_current_chapter"; //当前的章节

    private List<ComicItemBean.ChapterBean> mLsChapter = new ArrayList<>(); //所有章节
    private ComicItemBean.ChapterBean mCurrentChapter;  //当前的章节信息
    private List<ComicContentBean.ListBean> mLsData = new LinkedList<>(); //当前章节的具体内容集
    private String mTitle;  //漫画名称

    private DrawerLayout mDrawerLayout;
    private FrameLayout mFlMenu;
    private RecyclerView mRvView; //漫画
    private TextView mTvChapter;  //当前章节
    private ImageView mIvChapter; //所有章节
    private TextView mTvCaching;  //缓存

    private BaseRecyclerViewAdapter mAdapter;

    private MyComicObserver mObserver;

    public static void startComicContentActivity(Context context, List<ComicItemBean.ChapterBean> list,
                                                 ComicItemBean.ChapterBean chapter, String title) {
        Intent intent = new Intent(context, ComicContentActivity.class);
        intent.putExtra(BUNDLE_KEY_CHAPTERS, (Serializable) list);
        intent.putExtra(BUNDLE_KEY_CURRENT_CHAPTER, chapter);
        intent.putExtra(ChapterFragment.BUNDLE_KEY_TITLE, title);
        context.startActivity(intent);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_content);

        //初始化界面数据
        mLsChapter = (List<ComicItemBean.ChapterBean>) getIntent().getSerializableExtra(BUNDLE_KEY_CHAPTERS);
        mCurrentChapter = (ComicItemBean.ChapterBean) getIntent().getSerializableExtra(BUNDLE_KEY_CURRENT_CHAPTER);
        mTitle = getIntent().getStringExtra(ChapterFragment.BUNDLE_KEY_TITLE);

        //初始化控件
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mFlMenu = findViewById(R.id.fl_menu);
        mRvView = findViewById(R.id.rv_view);
        mTvChapter = findViewById(R.id.tv_chapter);
        mIvChapter = findViewById(R.id.iv_chapter);
        mTvCaching = findViewById(R.id.tv_caching);

        mIvChapter.setOnClickListener(this);
        mTvCaching.setOnClickListener(this);

        mTvChapter.setText(mCurrentChapter.num);
        initMenu();
        initDrawerLayout();
        initRecyclerView();

        //获取漫画内容
        getComicContent(mCurrentChapter.url);
    }

    //初始化左侧 章节页
    private void initMenu() {
        ChapterFragment menuFragment = (ChapterFragment) getSupportFragmentManager().findFragmentByTag("tag");
        if (menuFragment == null) {
            menuFragment = ChapterFragment.newInstance(mLsChapter, mTitle);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fl_menu, menuFragment, "tag")
                    .commit();
        }
    }

    private void initDrawerLayout() {

    }

    @Override
    protected void onDestroy() {
        if (mObserver != null) {
            mObserver.cancleRequest();
        }
        super.onDestroy();
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRvView.setLayoutManager(layoutManager);

        mAdapter = new BaseRecyclerViewAdapter<ComicContentBean.ListBean>(this, mLsData) {
            @Override
            public int getResourseId() {
                return R.layout.item_image;
            }

            @Override
            public void onBindView(BaseViewHolder holder, int position) {
                ComicContentBean.ListBean bean = mLsData.get(position);

                ImageLoader.with(mContext, bean.img, (ImageView) holder.getView(R.id.iv_cover));
            }
        };

        mRvView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.iv_chapter == id) {
            //todo 显示所有的章节
            mDrawerLayout.openDrawer(GravityCompat.START);
        } else if (R.id.tv_caching == id) {
            //todo 缓存漫画

        }
    }

    /**
     * 获取当前章节的漫画内容
     */
    private void getComicContent(String url) {
        mObserver = new MyComicObserver<ComicContentBean>(this) {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(ComicContentBean result) {
                if (result == null) {
                    Toast.makeText(getParent(), "无数据", Toast.LENGTH_LONG).show();
                    return;
                }
                initData(result.list);
                mAdapter.updateData(mLsData);
            }

            @Override
            public void onFailure(Throwable e, String errorMsg) {
                Toast.makeText(getParent(), errorMsg, Toast.LENGTH_LONG).show();
            }
        };

        RetrofitComicUtils.getApiUrl()
                .getComicContent(url)
                .compose(RxHelper.observableIO2Main(this))
                .subscribe(mObserver);
    }


    /**
     * 重新包装数据
     *
     * @param list 从网络中获取的数据
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void initData(List<ComicContentBean.ListBean> list) {
        mLsData = new ArrayList<>();
        //将数据加入到章节下
        for (ComicItemBean.ChapterBean a : mLsChapter) {
            if (a.num.contains(mCurrentChapter.num)) {
                a.list.addAll(list);
            }
        }

        //重新打包数据
        for (int i = 0; i < mLsChapter.size() - 1; i++) {
            mLsData.addAll(mLsChapter.get(i).list);
        }
    }

    @Override
    public void onBackClick() {
        mDrawerLayout .closeDrawers();
    }
}
