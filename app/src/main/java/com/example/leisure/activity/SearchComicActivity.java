package com.example.leisure.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leisure.MainApplication;
import com.example.leisure.R;
import com.example.leisure.adapter.BaseRecyclerViewAdapter;
import com.example.leisure.adapter.BaseViewHolder;
import com.example.leisure.bean.ComicListBean;
import com.example.leisure.db.greendao.RecentlySearch;
import com.example.leisure.glide.ImageLoader;
import com.example.leisure.greenDao.gen.RecentlySearchDao;
import com.example.leisure.retrofit.MyComicObserver;
import com.example.leisure.retrofit.RetrofitComicUtils;
import com.example.leisure.retrofit.RxHelper;
import com.example.leisure.util.Constant;
import com.example.leisure.util.Util;
import com.example.leisure.widget.CommonToolbar;
import com.example.leisure.widget.textWatcher.MySearchTextWatcher;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 查询漫画  通过网络
 */
public class SearchComicActivity extends BaseActivity implements MySearchTextWatcher.OnInputCompleteListener {
    private CommonToolbar mCtbHeader;
    private RecyclerView mRvOldSearch; //最近搜索 文字流布局
    private RecyclerView mRvResult;    //搜索结果
    private EditText mEtSearch;
    private BaseRecyclerViewAdapter mResultAdapter;
    private BaseRecyclerViewAdapter mOldSearchAdapter;


    private MyComicObserver mObserver;
    private List<ComicListBean.ListBean> mLsResultData = new ArrayList<>();
    private List<RecentlySearch> mLsSearchData = new ArrayList<>();
//    private ComicListBean.ListBean mComicItem;

    public static void startSearchComicActivity(Context context) {
        Intent intent = new Intent(context, SearchComicActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_comic);

        mCtbHeader = findViewById(R.id.ctb_header);
        mRvOldSearch = findViewById(R.id.rv_old_search);
        mRvResult = findViewById(R.id.rv_result);
        mEtSearch = findViewById(R.id.et_search);

        mCtbHeader.setLeftClickListener(new CommonToolbar.OnLeftDrawableClickListener() {
            @Override
            public void onLeftDrawableClick() {
                finish();
            }
        });


        initEditText();
        initRvOldSearch();
        initRvResult();

    }

    private void initEditText() {
        MySearchTextWatcher textWatcher = new MySearchTextWatcher();
        textWatcher.addOnEmptyListener(new MySearchTextWatcher.OnEmptyListener() {
            @Override
            public void onEmpty() {
                //输入框为空  删除
                mRvOldSearch.setVisibility(View.VISIBLE);
                mRvResult.setVisibility(View.GONE);

            }
        });
        mEtSearch.addTextChangedListener(textWatcher);

        mEtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String value = mEtSearch.getText().toString().trim();

                    //输入完成 通过网络进行查询
                    getData(value);

                    //向数据库 插入 最近查询的漫画书名称
                    RecentlySearch entity = new RecentlySearch();
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    entity.setDataTime(df.format(new Date()));
                    entity.setText(value);
                    entity.setUserName(MainApplication.getInstance().getInfo(Constant.SharedPref.BASE_DATA_USER_NAME));
                    MainApplication.getDaoSession().getRecentlySearchDao().insertOrReplace(entity);
                    return true;
                }
                return false;
            }
        });
    }

    //初始化最近搜索的词
    private void initRvOldSearch() {
        mLsSearchData = MainApplication.getDaoSession().getRecentlySearchDao().queryBuilder()
                .orderDesc(RecentlySearchDao.Properties.DataTime)
                .limit(10)
                .list();

        FlexboxLayoutManager layoutManager = new FlexboxLayoutManager(this, FlexDirection.ROW, FlexWrap.WRAP) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        mRvOldSearch.setLayoutManager(layoutManager);
        mOldSearchAdapter = new BaseRecyclerViewAdapter(this, mLsSearchData) {
            @Override
            public int getResourseId() {
                return R.layout.item_search;
            }

            @Override
            public void onBindView(BaseViewHolder holder, int position) {
                holder.setTextOfTextView(R.id.tv_text, mLsSearchData.get(position).getText());
            }
        };
        mRvOldSearch.setAdapter(mOldSearchAdapter);
        mOldSearchAdapter.addOnRecyclerViewItemClickListener(new BaseRecyclerViewAdapter.OnRecyclerViewItemClickListener<RecentlySearch>() {
            @Override
            public void onRecyclerViewItemClick(View view, RecentlySearch bean) {
                String text = bean.getText();
                mEtSearch.setText(text);
                mEtSearch.setSelection(text.length());
                getData(text);
            }
        });
    }

    //初始化搜索结果
    private void initRvResult() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRvResult.setLayoutManager(layoutManager);

        mResultAdapter = new BaseRecyclerViewAdapter<ComicListBean.ListBean>(this, mLsResultData) {
            @Override
            public int getResourseId() {
                return R.layout.item_book_shelf;
            }

            @Override
            public void onBindView(BaseViewHolder holder, int position) {
                ComicListBean.ListBean bean = mLsData.get(position);

                holder.setTextOfTextView(R.id.tv_name, bean.name);
                holder.setTextOfTextView(R.id.tv_latest, bean.latest);
                holder.setTextOfTextView(R.id.tv_time, bean.time);

                ImageLoader.with(SearchComicActivity.this, bean.cover, (ImageView) holder.getView(R.id.iv_cover));
            }
        };

        mRvResult.setAdapter(mResultAdapter);
        mResultAdapter.addOnRecyclerViewItemClickListener(new BaseRecyclerViewAdapter.OnRecyclerViewItemClickListener<ComicListBean.ListBean>() {
            @Override
            public void onRecyclerViewItemClick(View view, ComicListBean.ListBean bean) {
                //跳转到漫画详情
                ComicDetailsActivity.startComicDetailsActivity(SearchComicActivity.this, bean.url);
            }
        });

    }


    @Override
    public void onInputComplete(String value) {
//        //输入完成 通过网络进行查询
//        getData(value);
//        //向数据库 插入 最近查询的漫画书名称
//        RecentlySearch entity = new RecentlySearch();
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        entity.setDataTime(df.format(new Date()));
//        entity.setText(value);
//        entity.setUserName(MainApplication.getInstance().getInfo(Constant.SharedPref.BASE_DATA_USER_NAME));
//        MainApplication.getDaoSession().getRecentlySearchDao().insertOrReplace(entity);
    }

    /**
     * 查询漫画
     *
     * @param value 漫画名称
     */
    private void getData(String value) {
        //关闭键盘
        Util.hideInput(SearchComicActivity.this, getWindow());

        mObserver = new MyComicObserver<ComicListBean>(this) {
            @Override
            public void onSuccess(ComicListBean result) {
                if (result == null) {
                    Toast.makeText(SearchComicActivity.this, "无数据", Toast.LENGTH_LONG).show();
                    return;
                }

                mLsResultData = result.list;
                mResultAdapter.updateData(mLsResultData);
                mRvOldSearch.setVisibility(View.GONE);
                mRvResult.setVisibility(View.VISIBLE);
            }


            @Override
            public void onFailure(Throwable e, String errorMsg) {
                Toast.makeText(SearchComicActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        };

        RetrofitComicUtils.getApiUrl()
                .searchComic(value)
                .compose(RxHelper.observableIO2Main(this))
                .subscribe(mObserver);
    }


    /**
     * 显示键盘
     *
     * @param et 输入焦点
     */
    public void showInput(final EditText et) {
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * 隐藏键盘
     */
    protected void hideInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View v = getWindow().peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }


}
