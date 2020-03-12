package com.example.leisure.activity.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leisure.MainApplication;
import com.example.leisure.R;
import com.example.leisure.activity.ComicDetailsActivity;
import com.example.leisure.activity.adapter.BaseRecyclerViewAdapter;
import com.example.leisure.activity.adapter.BaseViewHolder;
import com.example.leisure.bean.ComicListBean;
import com.example.leisure.db.greendao.ComicBookBean;
import com.example.leisure.glide.ImageLoader;
import com.example.leisure.retrofit.MyComicObserver;
import com.example.leisure.retrofit.RetrofitComicUtils;
import com.example.leisure.retrofit.RxHelper;
import com.example.leisure.util.Constant;
import com.example.leisure.util.DensityUtil;
import com.example.leisure.util.ScreenInfoUtils;
import com.example.leisure.widget.GridSpacingItemDecoration;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 漫画分类详情
 * <p>
 * 显示分类下的漫画列表
 */
public class ComicItemFragment extends Fragment implements BaseRecyclerViewAdapter.OnItemClickListener<ComicBookBean>, View.OnClickListener {
    public static final String BUNDLE_KEY_MHLB = "key_mhlb";

    private static final String BUNDLE_KEY_DATA = "key_data";
    private static final String BUNDLE_KEY_COMIC_ITEM = "key_comic_item";
    private static final int SPAN_COUNT_LAND = 5;
    private static final int SPAN_COUNT_PORT = 3;
    private static final int SPACING_LAND = 8;
    private static final int SPACING_PORT = 8;

    private String mMhlb; //列表名
    private List<ComicBookBean> mLsData;
    private ComicBookBean mComicItem;

    private boolean mIsPrepare = false;        //视图还没准备好
    private boolean mIsVisible = false;        //不可见
    private boolean mIsFirstLoad = true;    //第一次加载
    private int mSpanCount;
    private int mSpacing;

    private View rootView;
    private TwinklingRefreshLayout mTrlView;
    private RecyclerView mRvView;
    private BaseRecyclerViewAdapter mAdapter;
    private RelativeLayout mRlError;
    private TextView mTvInfo, mTvRetry;

    private MyComicObserver mObserver;

    public static ComicItemFragment newInstance(String mhlb) {
        Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_KEY_MHLB, mhlb);
        ComicItemFragment fragment = new ComicItemFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            mMhlb = bundle.getString(BUNDLE_KEY_MHLB, "");
        }

        if (savedInstanceState != null) {
            mLsData = (List<ComicBookBean>) savedInstanceState.getSerializable(BUNDLE_KEY_DATA);
            mComicItem = (ComicBookBean) savedInstanceState.getSerializable(BUNDLE_KEY_COMIC_ITEM);
            mMhlb = savedInstanceState.getString(BUNDLE_KEY_MHLB);
        }

        if (mComicItem == null) {
            mComicItem = new ComicBookBean();
            mComicItem.pages = "1";
        }

        mSpanCount = ScreenInfoUtils.isWindowOrientationLand(getContext()) ? SPAN_COUNT_LAND : SPAN_COUNT_PORT;
        mSpacing = ScreenInfoUtils.isWindowOrientationLand(getContext()) ? SPACING_LAND : SPACING_PORT;
        mSpacing = DensityUtil.dip2px(getContext(), mSpacing);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_comic_item, container, false);
        }
        mRvView = rootView.findViewById(R.id.rv_view);
        mTrlView = rootView.findViewById(R.id.trl_view);
        mRlError = rootView.findViewById(R.id.rl_error);
        mTvInfo = rootView.findViewById(R.id.tv_info);
        mTvRetry = rootView.findViewById(R.id.tv_retry);

        initTwinklingRefreshLayout();
        initRecyclerView();

        mTvRetry.setOnClickListener(this);
        return rootView;
    }

    //初始化刷新控件
    private void initTwinklingRefreshLayout() {
        mTrlView.setEnableRefresh(false);
        mTrlView.setOnRefreshListener(new RefreshListenerAdapter() {

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                if (Integer.parseInt(mComicItem.dpages) == Integer.parseInt(mComicItem.pages)) {
                    Toast.makeText(getContext(), "已到达最后一页", Toast.LENGTH_SHORT).show();
                    return;
                }
                getComic(Integer.parseInt(mComicItem.pages) + 1, false);
            }
        });
    }

    private void initRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), mSpanCount);
        mRvView.setLayoutManager(layoutManager);
        mRvView.addItemDecoration(new GridSpacingItemDecoration(mSpanCount, mSpacing, false));


        mAdapter = new BaseRecyclerViewAdapter<ComicBookBean>(getContext(), mLsData) {
            @Override
            public int getResourseId() {
                return R.layout.item_comic;
            }

            @Override
            public void onBindView(BaseViewHolder holder, int position) {
                ComicBookBean bean = mLsData.get(position);

                holder.setText(R.id.tv_name, bean.getName());
                ImageLoader.with(getContext(), bean.getCover(), (ImageView) holder.getView(R.id.iv_cover));
            }
        };

        mRvView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mIsPrepare = true;
        lazyLoad();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putSerializable(BUNDLE_KEY_DATA, (Serializable) mLsData);
        outState.putSerializable(BUNDLE_KEY_COMIC_ITEM, mComicItem);
        outState.putString(BUNDLE_KEY_MHLB, mMhlb);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //isVisibleToUser这个boolean值表示:该Fragment的UI 用户是否可见
        if (isVisibleToUser) {
            mIsVisible = true;
            lazyLoad();
        } else {
            mIsVisible = false;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mIsFirstLoad = true;
        mIsPrepare = false;
        mIsVisible = false;
        if (rootView != null) {
            ((ViewGroup) rootView.getParent()).removeView(rootView);
        }
    }

    @Override
    public void onDestroy() {
        if (mObserver != null) {
            mObserver.cancleRequest();
        }
        super.onDestroy();
    }

    private void lazyLoad() {
        if (mLsData != null) {
            mIsFirstLoad = false;
            return;
        }

        //这里进行三个条件的判断，如果有一个不满足，都将不进行加载
        if (!mIsPrepare || !mIsVisible || !mIsFirstLoad) {
            return;
        }
        mLsData = new ArrayList<>();
        getComic(Integer.parseInt(mComicItem.pages), true);
        //数据加载完毕,恢复标记,防止重复加载
        mIsFirstLoad = false;
    }

    @Override
    public void onItemClick(View view, int position, ComicBookBean bean) {
        //todo 跳转到漫画详情页
        MainApplication.getInstance().saveInfo(Constant.ComicBaseBundle.BUNDLE_COVER, bean.getCover());
        MainApplication.getInstance().saveInfo(Constant.ComicBaseBundle.BUNDLE_NAME, bean.getName());
        MainApplication.getInstance().saveInfo(Constant.ComicBaseBundle.BUNDLE_HURL1, bean.getUrl());
        ComicDetailsActivity.startComicDetailsActivity(getContext());
    }

    /**
     * 获取漫画分类详情
     */
    private void getComic(int page, boolean isUpdate) {
        mObserver = new MyComicObserver<ComicListBean>(getActivity()) {
            @Override
            public void onSuccess(ComicListBean result) {
                mTrlView.finishRefreshing();
                mTrlView.finishLoadmore();

                if (result == null || result.list == null || result.list.size() == 0) {
                    if (mLsData != null)
                        Toast.makeText(getActivity(), "Sorry,没有数据啦", Toast.LENGTH_LONG).show();
                    else
                        updateView(false, "服务开小差了，没有数据");
                    return;
                }

                //获取分类下的页数 总数 当前的页
                if (!TextUtils.isEmpty(result.list.get(0).pages)) {
                    mComicItem = result.list.get(0);
                    result.list.remove(0);
                }
                mLsData = result.list;
                if (isUpdate)
                    mAdapter.updateData(mLsData);
                else
                    mAdapter.addMoreData(mLsData);
                updateView(true, null);
            }


            @Override
            public void onFailure(Throwable e, String errorMsg) {
                if (mLsData != null && mLsData.size() != 0) {
                    mTrlView.finishRefreshing();
                    mTrlView.finishLoadmore();
                    Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
                } else {
                    updateView(false, errorMsg);
                }

            }
        };

        RetrofitComicUtils.getApiUrl()
                .getComicList(mMhlb + "-" + page)
                .compose(RxHelper.observableIO2Main(getActivity()))
                .subscribe(mObserver);
    }

    private void updateView(boolean isSuccess, String msg) {
        if (isSuccess) {
            mRlError.setVisibility(View.GONE);
        } else if (mLsData == null || mLsData.size() == 0) {
            mRlError.setVisibility(View.VISIBLE);
            mTvInfo.setText(msg);
        }
    }

    @Override
    public void onClick(View v) {
        if (R.id.tv_retry == v.getId()) {
            getComic(Integer.parseInt(mComicItem.pages), true);
            mTrlView.setVisibility(View.GONE);
        }
    }
}
