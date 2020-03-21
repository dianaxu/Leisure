package com.example.leisure.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leisure.MainApplication;
import com.example.leisure.R;
import com.example.leisure.activity.adapter.BaseRecyclerViewAdapter;
import com.example.leisure.activity.adapter.DownloadChooseChapterAdapter;
import com.example.leisure.db.greendao.ComicChapterBean;
import com.example.leisure.eventbus.Event;
import com.example.leisure.eventbus.EventBusUtil;
import com.example.leisure.eventbus.EventCode;
import com.example.leisure.greenDao.gen.ComicChapterBeanDao;
import com.example.leisure.greenDao.gen.DaoSession;
import com.example.leisure.util.Constant;
import com.example.leisure.util.DensityUtil;
import com.example.leisure.widget.CommonToolbar;
import com.example.leisure.widget.GridSpacingItemDecoration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 选择下载章节
 * 1.界面 ok
 * 2.章节排序 ok
 * 3.显示一共几话 ok
 * 4.显示章节列表  标注已下载，选中想要下载的 ok
 * 5.显示已选择 多少话 ok
 * 6.可全选， 下载所选 ok
 */
public class DownloadChooseChapterActivity extends BaseActivity implements View.OnClickListener,
        BaseRecyclerViewAdapter.OnItemClickListener<ComicChapterBean>, DownloadChooseChapterAdapter.OnSelectListener {
    public static final String EXTRA_BOOK_ID = "extra_book_id";
    public static final String EXTRA_BOOK_NAME = "extra_book_name";

    private static final String BUNDLE_KEY_DATA = "key_data";
    private static final String BUNDLE_KEY_LsNotSelected = "key_lsnotselected";

    private long mBookId;
    private String mBookName;
    private List<ComicChapterBean> mLsData = new ArrayList<>();
    private List<Long> mLsNotSelected = new ArrayList<>();

    private CommonToolbar mCtbHeader;
    private TextView mTvMaxCount, mTvCount;
    private TextView mTvSort;
    private RecyclerView mRvView;
    private Button mBtnAll, mBtnDownload;

    private DownloadChooseChapterAdapter mAdapter;
    private DaoSession mDaoSession;


    public static void startDownloadChooseChapterActivity(Context context, long bookId, String bookName) {
        Intent intent = new Intent(context, DownloadChooseChapterActivity.class);
        intent.putExtra(EXTRA_BOOK_ID, bookId);
        intent.putExtra(EXTRA_BOOK_NAME, bookName);
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
        setContentView(R.layout.activity_download_choose_chapter);
        mDaoSession = MainApplication.getInstance().getDaoSession();

        mBookId = getIntent().getLongExtra(EXTRA_BOOK_ID, 0);
        mBookName = getIntent().getStringExtra(EXTRA_BOOK_NAME);
        if (savedInstanceState != null) {
            mLsData = (List<ComicChapterBean>) savedInstanceState.getSerializable(BUNDLE_KEY_DATA);
            mLsNotSelected = (List<Long>) savedInstanceState.getSerializable(BUNDLE_KEY_LsNotSelected);
        } else
            getData(mBookId);

        mCtbHeader = findViewById(R.id.ctb_header);
        mTvMaxCount = findViewById(R.id.tv_max_count);
        mTvCount = findViewById(R.id.tv_count);
        mRvView = findViewById(R.id.rv_view);
        mTvSort = findViewById(R.id.tv_sort);
        mBtnAll = findViewById(R.id.btn_all);
        mBtnDownload = findViewById(R.id.btn_download);

        mTvSort.setOnClickListener(this);
        mBtnAll.setOnClickListener(this);
        mBtnDownload.setOnClickListener(this);

        mCtbHeader.setLeftClickListener(new CommonToolbar.OnLeftDrawableClickListener() {
            @Override
            public void onLeftDrawableClick() {
                finish();
            }
        });
        mTvMaxCount.setText("共 " + mLsData.size() + " 话");

        initRecyclerView();
    }

    //初始化RecyclerView
    private void initRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        mRvView.setLayoutManager(layoutManager);
        mRvView.addItemDecoration(new GridSpacingItemDecoration(3, DensityUtil.dip2px(this, 8), true));

        mAdapter = new DownloadChooseChapterAdapter(this, mLsData, mLsNotSelected);

        mRvView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
        mAdapter.addOnSelectListener(this);
    }


    private void getData(long bookId) {
        mLsData = mDaoSession.getComicChapterBeanDao().queryBuilder()
                .where(ComicChapterBeanDao.Properties.BookId.eq(bookId)).list();
        for (int i = 0; i < mLsData.size(); i++) {
            if (mLsData.get(i).getCacheState() == Constant.DownloadState.DOWNLOAD_NOT) {
                mLsNotSelected.add(mLsData.get(i).get_id());
            }
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(BUNDLE_KEY_DATA, (Serializable) mLsData);
        outState.putSerializable(BUNDLE_KEY_LsNotSelected, (Serializable) mAdapter.getLsNotSelected());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.tv_sort == id) {
            sortChapter();
        } else if (R.id.btn_all == id) {
            mAdapter.setSelectAllOrNot();
        } else if (R.id.btn_download == id) {
            download();
        } else if (R.id.rv_view == id) {
            mAdapter.setSelectAllOrNot();
        }
    }

    private void download() {
        if (mAdapter.getSelectedChapters().size() == 0) {
            Toast.makeText(this, "请选择需要下载的章节！", Toast.LENGTH_SHORT).show();
            return;
        }

        //保存数据  跳转到详细下载页
        List<ComicChapterBean> selectChapter = mAdapter.getSelectChapter();
        updateChapterCacheState(Constant.DownloadState.DOWNLOADING, selectChapter);

        String sqlStr = "update comic_book_bean set cache_state = " + Constant.DownloadState.DOWNLOADING
                + " where _id = " + mBookId;
        mDaoSession.getDatabase().execSQL(sqlStr);

        EventBusUtil.sendEvent(new Event(EventCode.DOWNLOAD_ADD_MORE));
        DownloadChapterActivity.startDownloadChapterActivity(this, mBookId, mBookName, true);
        finish();
    }

    //排序
    private void sortChapter() {
        mTvSort.setSelected(!mTvSort.isSelected());
        mTvSort.setText(mTvSort.isSelected() ? "正序" : "倒序");
        mAdapter.sortData();
    }


    @Override
    public void onItemClick(View view, int position, ComicChapterBean bean) {
        mAdapter.setSelectItemOrNot(position);
        mTvCount.setText("已选择 " + mAdapter.selectCount() + " 话");
    }


    private void updateChapterCacheState(int state, List<ComicChapterBean> chapters) {
        for (ComicChapterBean chapter : chapters) {
            chapter.setCacheState(state);
        }
        mDaoSession.getComicChapterBeanDao().updateInTx(chapters);
    }

    @Override
    public void UpdateUI(String text, int selectCount) {
        mBtnAll.setText(text);
        mTvCount.setText("已选择 " + selectCount + " 话");
    }
}
