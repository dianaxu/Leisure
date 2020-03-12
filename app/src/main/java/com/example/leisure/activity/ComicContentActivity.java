package com.example.leisure.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leisure.MainApplication;
import com.example.leisure.R;
import com.example.leisure.activity.adapter.ComicContentGroupedListAdapter;
import com.example.leisure.activity.fragment.ChapterFragment;
import com.example.leisure.activity.presenter.ComicContentPresenter;
import com.example.leisure.activity.view.IComicContent;
import com.example.leisure.db.greendao.ComicBookBean;
import com.example.leisure.db.greendao.ComicChapterBean;
import com.example.leisure.service.DownloadService;
import com.example.leisure.util.ScreenInfoUtils;
import com.example.leisure.widget.CommonToolbar;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_DRAGGING;


/**
 * 漫画内容
 * <p>
 * 1.实现漫画内容展示  ok
 * 2.左侧显示章节  ok
 * 3.缓存之后的章节（包括当前的章节）
 * 4.全屏预览 ok
 */
public class ComicContentActivity extends BaseActivity implements View.OnClickListener, ChapterFragment.OnToolbarListener,
        ChapterFragment.OnChapterSelectedListener, CommonToolbar.OnLeftDrawableClickListener, IComicContent {
    public static final String BUNDLE_KEY_COMICBOOKBEAN = "key_comicbookbean";   //所有的章节

    private List<ComicChapterBean> mLsChapter; //所有章节 及  内容集

    private DrawerLayout mDrawerLayout;
    private RecyclerView mRvView; //漫画
    private TextView mTvChapter;  //当前章节
    private ImageView mIvChapter; //显示所有章节
    private TextView mTvCaching;  //缓存
    private TwinklingRefreshLayout mTrlView;
    private CommonToolbar mCtbHeader;
    private RelativeLayout mRlBottomBar;

    private ComicContentGroupedListAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private ChapterFragment menuFragment;
    private ComicContentPresenter mPresenter;
    private DownloadService mService;

    public static void startComicContentActivity(Context context, ComicBookBean bean) {
        Intent intent = new Intent(context, ComicContentActivity.class);
        intent.putExtra(BUNDLE_KEY_COMICBOOKBEAN, bean);
        context.startActivity(intent);

    }

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return TransitionMode.RIGHT;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_content);

        Intent intent = getIntent();
        //初始化界面数据
        ComicBookBean bean = (ComicBookBean) intent.getSerializableExtra(BUNDLE_KEY_COMICBOOKBEAN);
        mPresenter = new ComicContentPresenter(this, savedInstanceState, bean, this);

        initView();                   //初始化控件
        initMenu();                   //初始化章节
        initTwinklingRefreshLayout(); //初始化刷新控件
        initRecyclerView();

        mPresenter.getComicImages(mPresenter.getReadPosition(), true, true);  //获取漫画内容
        mService = MainApplication.getInstance().getDownloadService();
    }

    //初始化控件
    private void initView() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mTrlView = findViewById(R.id.trl_view);
        mRvView = findViewById(R.id.rv_view);
        mTvChapter = findViewById(R.id.tv_chapter);
        mIvChapter = findViewById(R.id.iv_chapter);
        mTvCaching = findViewById(R.id.tv_caching);
        mCtbHeader = findViewById(R.id.ctb_header);
        mRlBottomBar = findViewById(R.id.rl_bottom_bar);

        mIvChapter.setOnClickListener(this);
        mTvCaching.setOnClickListener(this);
        mCtbHeader.setLeftClickListener(this);
    }

    //初始化左侧 章节页
    private void initMenu() {
        if (menuFragment == null) {
            menuFragment = ChapterFragment.newInstance(mLsChapter);
            menuFragment.addChapterSelectedListener(this);
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_menu, menuFragment, "ChapterFragment")
                .commit();
    }

    //初始化刷新控件
    private void initTwinklingRefreshLayout() {
        mTrlView.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                int position = mPresenter.getReadPosition();
                if (position == 0) {
                    refreshLayout.finishRefreshing();
                    Toast.makeText(ComicContentActivity.this, "已到达第一章", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (hasNoData(position)) {
                    //todo 网络上获取当前章节的漫画链接集
                    mPresenter.getComicImages(position, false, false);
                } else {
                    updateGroupInserted(--position);
                    refreshLayout.finishRefreshing();
                }

            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                int position = mPresenter.getReadPosition();
                if (position != mAdapter.getItemCount() - 1) {
                    refreshLayout.finishLoadmore();
                    Toast.makeText(ComicContentActivity.this, "已到达最后一章", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (hasNoData(position)) {
                    //todo 网络上获取当前章节的漫画链接集
                    mPresenter.getComicImages(position, false, false);
                } else {
                    updateGroupInserted(++position);
                    refreshLayout.finishLoadmore();
                }
            }

        });
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        int windowWidth = ScreenInfoUtils.getWindowWidth(this);
        mLayoutManager = new LinearLayoutManager(this);
        mRvView.setLayoutManager(mLayoutManager);
        mAdapter = new ComicContentGroupedListAdapter(this, windowWidth, mLsChapter);
        mRvView.setAdapter(mAdapter);
        mRvView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (SCROLL_STATE_DRAGGING == newState && mRlBottomBar.getVisibility() == VISIBLE) {
                    setViewVisible();
                }
                if (newState == RecyclerView.SCROLL_STATE_IDLE) { //当前状态为停止滑动
                    int position = mPresenter.getReadPosition();
                    if (!mRvView.canScrollVertically(1)) { // 到达底部
                        if (position != mAdapter.getItemCount() - 1)
                            updateGroupInserted(++position);
                    } else if (!mRvView.canScrollVertically(-1)) { // 到达顶部
                        if (position != 0)
                            updateGroupInserted(--position);
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mLsChapter.get(mPresenter.getReadPosition()).isReading = false;

                int last = mLayoutManager.findLastVisibleItemPosition();
                int lastGroupposition = mAdapter.getGroupPositionForPosition(last);

                mPresenter.setReadPosition(lastGroupposition);
                mTvChapter.setText(mLsChapter.get(lastGroupposition).num);
            }
        });

        mAdapter.addOnBindChildClickListener(new ComicContentGroupedListAdapter.OnBindChildClickListener() {
            @Override
            public void onBindChildClick() {
                setViewVisible();
            }
        });
    }

    private void updateGroupInserted(int position) {
        if (mLsChapter.get(position).visible) return; //过滤已经显示着的
        if (hasNoData(position)) return; //过滤没有图片集

        mLsChapter.get(position).visible = true;
        mAdapter.notifyGroupInserted(position);
    }

    //设置顶部及底部控件的显示和隐藏
    private void setViewVisible() {
        if (GONE == mRlBottomBar.getVisibility()) {
            mRlBottomBar.setVisibility(VISIBLE);
            mCtbHeader.setVisibility(VISIBLE);
        } else if (VISIBLE == mRlBottomBar.getVisibility()) {
            mRlBottomBar.setVisibility(GONE);
            mCtbHeader.setVisibility(GONE);
        }
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
        if (R.id.iv_chapter == id) {
            //todo 显示所有的章节
            menuFragment.updateDate(mLsChapter);
            menuFragment.setReadPosition(mPresenter.getReadPosition());
            mDrawerLayout.openDrawer(GravityCompat.START);
        } else if (R.id.tv_caching == id) {
            //todo 缓存漫画
            SelectCacheDialog dialog = SelectCacheDialog.getInterest(this);
            dialog.addOnSelectCacheListener(new SelectCacheDialog.OnSelectCacheListener() {
                @Override
                public void onDownloadAll() {
                    cacheComic(true);
                }

                @Override
                public void onCurrentPosition() {
                    cacheComic(false);
                }

                @Override
                public void onCancel() {
                    dialog.dismiss();
                }
            });
            dialog.show();

        }
    }

    private void cacheComic(boolean isCacheAll) {
        Long bookId = mPresenter.getBookId();
        if (mPresenter.isAddBookShelf()) {
            mPresenter.updateState(isCacheAll);
            mService.addMoreTask(bookId);
        } else {
            mPresenter.addBookToDB(isCacheAll);
            bookId = mPresenter.getBookId();
            if (bookId != null)
                mService.addMoreTask(bookId);
        }
    }

    @Override
    public void onLeftDrawableClick() {
        finish();
    }

    @Override
    public void onBackClick() {
        mDrawerLayout.closeDrawers();
    }

    @Override
    public void onChapterSelected(int position) {
        if (position == mPresenter.getReadPosition()) {
            mDrawerLayout.closeDrawers();
            return;
        }

        //跳转到某个章节
        for (int i = 0; i < mLsChapter.size(); i++) {
            mLsChapter.get(i).visible = false;
        }

        if (hasNoData(position)) {
            mPresenter.getComicImages(position, true, true);
        } else {
            mPresenter.updateLsChapter(mLsChapter);
            mPresenter.setReadPosition(position);
            mLsChapter.get(position).visible = true;
            int scrollPosition = 0;
            if (position != 0 && !hasNoData(position - 1)) {
                mLsChapter.get(position - 1).visible = true;
                scrollPosition = mLsChapter.get(position - 1).getList().size();
            }

            mAdapter.updateData(mLsChapter);
            mLayoutManager.scrollToPositionWithOffset(scrollPosition, 0);
        }

        mTvChapter.setText(mLsChapter.get(position).num);
        mDrawerLayout.closeDrawers();
    }

    private boolean hasNoData(int position) {
        ComicChapterBean chapter = mLsChapter.get(position);
        if (mPresenter.isAddBookShelf()) {
            //针对数据库的数据
            return chapter.getList() == null || chapter.getList().size() == 0;
        } else {
            //针对网络数据
            return chapter.list == null || chapter.list.size() == 0;

        }
    }

    @Override
    public void updateUI(int groupPosition, List<ComicChapterBean> result, boolean isRefreshAll, boolean isJump) {
        mTrlView.finishRefreshing();
        mTrlView.finishLoadmore();

        mLsChapter = result;
        if (isRefreshAll) {
            mAdapter.updateData(result);
        } else {
            mAdapter.notifyGroupInserted(groupPosition);
        }
        if (isJump) {
            int scrollPosition = 0;
            if (groupPosition != 0 && mLsChapter.get(groupPosition - 1).visible)
                scrollPosition = mLsChapter.get(groupPosition - 1).list.size();
            mLayoutManager.scrollToPositionWithOffset(scrollPosition, 0);
        }
        mTvChapter.setText(mLsChapter.get(groupPosition).num);
    }

    @Override
    public void onFailure(String errorMsg) {
        mTrlView.finishRefreshing();
        mTrlView.finishLoadmore();

        Toast.makeText(getParent(), errorMsg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void addComicSuccessToDB(long bookId) {
        mService.addMoreTask(bookId);
    }
}
