package com.example.leisure.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leisure.MainApplication;
import com.example.leisure.R;
import com.example.leisure.adapter.ComicContentGroupedListAdapter;
import com.example.leisure.bean.ComicContentBean;
import com.example.leisure.bean.ComicItemBean;
import com.example.leisure.db.greendao.BookChapter;
import com.example.leisure.db.greendao.BookShelf;
import com.example.leisure.db.greendao.ChapterDetail;
import com.example.leisure.fragment.ChapterFragment;
import com.example.leisure.greenDao.gen.BookChapterDao;
import com.example.leisure.greenDao.gen.BookShelfDao;
import com.example.leisure.greenDao.gen.DaoSession;
import com.example.leisure.retrofit.MyComicObserver;
import com.example.leisure.retrofit.RetrofitComicUtils;
import com.example.leisure.retrofit.RxHelper;
import com.example.leisure.widget.CommonToolbar;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
public class ComicContentActivity extends BaseActivity implements View.OnClickListener, ChapterFragment.OnToolbarListener, ChapterFragment.OnChapterSelectedListener, CommonToolbar.OnLeftDrawableClickListener {
    public static final String BUNDLE_KEY_CHAPTERS = "key_chapters";   //所有的章节
    public static final String BUNDLE_KEY_CURRENT_POS = "key_current_pos"; //当前的章节
    public static final String BUNDLE_KEY_HURL1 = "key_hurl1";         //书的链接

    private List<ComicItemBean.ChapterBean> mLsChapter = new ArrayList<>(); //所有章节 及  内容集
    private int mCurrentChapterPosition;  //当前的章节信息
    private String mTitle;  //漫画名称
    private String mUrl;

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

    private MyComicObserver mObserver;
    private BookShelf mBook;
    private boolean mIsAddBookShelf = false;
    private ChapterFragment menuFragment;
    private DaoSession mDaoSession;


    public static void startComicContentActivity(Context context, List<ComicItemBean.ChapterBean> list,
                                                 int position, String title, String url) {
        Intent intent = new Intent(context, ComicContentActivity.class);
        intent.putExtra(BUNDLE_KEY_CHAPTERS, (Serializable) list);
        intent.putExtra(BUNDLE_KEY_CURRENT_POS, position);
        intent.putExtra(ChapterFragment.BUNDLE_KEY_TITLE, title);
        intent.putExtra(BUNDLE_KEY_HURL1, url);
        context.startActivity(intent);

    }

    private void initViewDate() {
        Intent intent = getIntent();
        //初始化界面数据
        mLsChapter = (List<ComicItemBean.ChapterBean>) intent.getSerializableExtra(BUNDLE_KEY_CHAPTERS);
        mCurrentChapterPosition = intent.getIntExtra(BUNDLE_KEY_CURRENT_POS, 0);
        mTitle = intent.getStringExtra(ChapterFragment.BUNDLE_KEY_TITLE);
        mUrl = intent.getStringExtra(BUNDLE_KEY_HURL1);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_content);
        mDaoSession = MainApplication.getDaoSession();

        initViewDate();               //初始化界面数据
        initView();                   //初始化控件
        initTwinklingRefreshLayout(); //初始化刷新控件
        initMenu();                   //初始化章节
        initRecyclerView();
        initIsAddBookShelf();

        mTvChapter.setText(mLsChapter.get(mCurrentChapterPosition).num);
        getComicContent(mCurrentChapterPosition);  //获取漫画内容
    }

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

    //初始化刷新控件
    private void initTwinklingRefreshLayout() {
        mTrlView.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                int first = mLayoutManager.findFirstCompletelyVisibleItemPosition();
                mCurrentChapterPosition = mAdapter.getGroupPositionForPosition(first);
                if (first == -1) return;
                if (mCurrentChapterPosition == 0 && first == 0) {
                    Toast.makeText(ComicContentActivity.this, "已到达第一个", Toast.LENGTH_SHORT).show();
                    return;
                }
                mCurrentChapterPosition--;
                mLsChapter.get(mCurrentChapterPosition).visible = true;
                if (mLsChapter.get(mCurrentChapterPosition).list.size() == 0) {
                    //todo 网络上获取当前章节的漫画链接集
                    getComicContent(mCurrentChapterPosition);
                } else {
                    //数据存在  刷新数据 关闭刷新动画
                    mAdapter.notifyGroupChanged(mCurrentChapterPosition);
                    refreshLayout.finishRefreshing();
                }

            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                int last = mLayoutManager.findLastVisibleItemPosition();
                //到达最后的group
                mCurrentChapterPosition = mAdapter.getGroupPositionForPosition(last);
                if (last == -1) return;
                if (mCurrentChapterPosition + 1 == mLsChapter.size()) {
                    Toast.makeText(ComicContentActivity.this, "已到达最后一章", Toast.LENGTH_SHORT).show();
                    return;
                }
                mCurrentChapterPosition++;
                mLsChapter.get(mCurrentChapterPosition).visible = true;
                if (mLsChapter.get(mCurrentChapterPosition).list.size() == 0) {
                    //todo 网络上获取当前章节的漫画链接集
                    getComicContent(mCurrentChapterPosition);
                } else {
                    //数据存在  刷新数据 关闭刷新动画
                    mAdapter.notifyGroupChanged(mCurrentChapterPosition);
                    refreshLayout.finishRefreshing();
                }
            }
        });

    }

    //初始化左侧 章节页
    private void initMenu() {
        menuFragment = (ChapterFragment) getSupportFragmentManager().findFragmentByTag("ChapterFragment");
        mLsChapter.get(mCurrentChapterPosition).isReading = true;
        if (menuFragment == null) {
            menuFragment = ChapterFragment.newInstance(mLsChapter, mTitle, mUrl);
            menuFragment.addChapterSelectedListener(this);
        } else {
            menuFragment.setReading(mCurrentChapterPosition);
        }
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fl_menu, menuFragment, "ChapterFragment")
                .commit();
    }

    @Override
    public void onDestroy() {
        if (mObserver != null) {
            mObserver.cancleRequest();
        }
        super.onDestroy();
    }

    /**
     * 初始化RecyclerView
     */
    private void initRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        mRvView.setLayoutManager(mLayoutManager);
        mAdapter = new ComicContentGroupedListAdapter(this, mLsChapter);
        mRvView.setAdapter(mAdapter);
        mRvView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (SCROLL_STATE_DRAGGING == newState && mRlBottomBar.getVisibility() == VISIBLE) {
                    setViewVisible();
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                mLsChapter.get(mCurrentChapterPosition).isReading = false;
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int lastPosition = manager.findLastVisibleItemPosition();
                mCurrentChapterPosition = mAdapter.getGroupPositionForPosition(lastPosition);
                mTvChapter.setText(mLsChapter.get(mCurrentChapterPosition).num);
            }
        });

        mAdapter.addOnBindChildClickListener(new ComicContentGroupedListAdapter.OnBindChildClickListener() {
            @Override
            public void onBindChildClick() {
                setViewVisible();
            }
        });
    }

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
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.iv_chapter == id) {
            //todo 显示所有的章节
            menuFragment.setReading(mCurrentChapterPosition);
            mDrawerLayout.openDrawer(GravityCompat.START);
        } else if (R.id.tv_caching == id) {
            //todo 缓存漫画

        }
    }

    @Override
    public void onLeftDrawableClick() {
        finish();
    }

    /**
     * 获取当前章节的漫画内容
     */
    private void getComicContent(int groupPosition) {
        mObserver = new MyComicObserver<ComicContentBean>(this) {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(ComicContentBean result) {
                mTrlView.finishRefreshing();
                mTrlView.finishLoadmore();
                if (result == null) {
                    Toast.makeText(getParent(), "无数据", Toast.LENGTH_LONG).show();
                    return;
                }
                saveChapterDetailsToDB(result.list);
                updateBookChapter(mLsChapter.get(groupPosition).url, result.list.size());

                mLsChapter.get(groupPosition).visible = true;
                mLsChapter.get(groupPosition).list = result.list;
                mAdapter.notifyDataChanged();
            }

            @Override
            public void onFailure(Throwable e, String errorMsg) {
                mTrlView.finishRefreshing();
                mTrlView.finishLoadmore();
                Toast.makeText(ComicContentActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        };

        RetrofitComicUtils.getApiUrl()
                .getComicContent(mLsChapter.get(groupPosition).url)
                .compose(RxHelper.observableIO2Main(this))
                .subscribe(mObserver);

        updateReadToChapterUrl(mLsChapter.get(groupPosition).url);
    }

    @Override
    public void onBackClick() {
        mDrawerLayout.closeDrawers();
    }

    private void initIsAddBookShelf() {
        //获取在书架中符合条件的书
        mBook = mDaoSession.getBookShelfDao().queryBuilder()
                .where(BookShelfDao.Properties.Url.eq(mUrl))
                .unique();
        mIsAddBookShelf = mBook != null ? true : false;
    }

    /**
     * 保存 书架上的书的阅读位置  到本地数据库
     */
    private void updateReadToChapterUrl(String url) {
        if (!mIsAddBookShelf) return;

        //将阅读到的章节更新到数据库
        //阅读的位置
        mBook.setReadToChapterUrl(url);
        mDaoSession.getBookShelfDao().update(mBook);
    }

    //保存章节下的所有图片url数据到本地数据库
    private void saveChapterDetailsToDB(List<ComicContentBean.ListBean> list) {
        if (!mIsAddBookShelf) return;

        long chapterId = mDaoSession.getBookChapterDao().queryBuilder()
                .where(BookChapterDao.Properties.BookId.eq(mBook.get_id()),
                        BookChapterDao.Properties.Url.eq(mLsChapter.get(mCurrentChapterPosition).url))
                .unique()
                .get_id();
        long bookId = mBook.get_id();
        List<ChapterDetail> entities = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            entities.add(new ChapterDetail(null, chapterId, bookId, list.get(i).img, false, null));
        }
        mDaoSession.getChapterDetailDao().insertInTx(entities);

    }

    private void updateBookChapter(String url, int maxCount) {
        if (!mIsAddBookShelf) return;
        BookChapter chapter = mDaoSession.getBookChapterDao().queryBuilder()
                .where(BookChapterDao.Properties.Url.eq(url))
                .unique();
        chapter.setMaxCount(maxCount);
        mDaoSession.getBookChapterDao().update(chapter);
    }


    @Override
    public void onChapterSelected(int position) {
        //跳转到某个章节
        for (int i = 0; i < mLsChapter.size(); i++) {
            mLsChapter.get(i).visible = false;
        }

        if (mLsChapter.get(position).list.size() == 0) {
            getComicContent(position);
        } else {
            mLsChapter.get(position).visible = true;
            mAdapter.notifyItemInserted(position);
        }
        mCurrentChapterPosition = position;
        mTvChapter.setText(mLsChapter.get(mCurrentChapterPosition).num);
        mDrawerLayout.closeDrawers();
    }
}
