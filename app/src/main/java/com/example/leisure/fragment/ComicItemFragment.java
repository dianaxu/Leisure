package com.example.leisure.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.leisure.R;
import com.example.leisure.activity.ComicDetailsActivity;
import com.example.leisure.adapter.BaseRecyclerViewAdapter;
import com.example.leisure.adapter.BaseViewHolder;
import com.example.leisure.bean.ComicListBean;
import com.example.leisure.glide.ImageLoader;
import com.example.leisure.retrofit.MyComicObserver;
import com.example.leisure.retrofit.RetrofitComicUtils;
import com.example.leisure.retrofit.RxHelper;

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
public class ComicItemFragment extends Fragment implements BaseRecyclerViewAdapter.OnRecyclerViewItemClickListener<ComicListBean.ListBean> {
    public static final String BUNDLE_KEY_MHLB = "key_mhlb";
    private String mMhlb; //列表名
    private List<ComicListBean.ListBean> mLsData = new ArrayList<>();
    private ComicListBean.ListBean mComicItem;

    private boolean mIsPrepare = false;        //视图还没准备好
    private boolean mIsVisible = false;        //不可见
    private boolean mIsFirstLoad = true;    //第一次加载

    private View rootView;
    private RecyclerView mRvView;
    private BaseRecyclerViewAdapter mAdapter;

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

        mComicItem = new ComicListBean.ListBean();
        mComicItem.pages = "0";
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_comic_item, container, false);
        }
        mRvView = rootView.findViewById(R.id.rv_view);

        initRecyclerView();
        return rootView;
    }

    private void initRecyclerView() {

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRvView.setLayoutManager(layoutManager);

        mAdapter = new BaseRecyclerViewAdapter<ComicListBean.ListBean>(getContext(), mLsData) {
            @Override
            public int getResourseId() {
                return R.layout.item_comic;
            }

            @Override
            public void onBindView(BaseViewHolder holder, int position) {
                ComicListBean.ListBean bean = mLsData.get(position);

                holder.setTextOfTextView(R.id.tv_name, bean.name);
                holder.setTextOfTextView(R.id.tv_latest, bean.latest);
                holder.setTextOfTextView(R.id.tv_time, bean.time);

                ImageLoader.with(getContext(), bean.cover, (ImageView) holder.getView(R.id.iv_cover));
            }
        };

        mRvView.setAdapter(mAdapter);
        mAdapter.addOnRecyclerViewItemClickListener(this);

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mIsPrepare = true;
        lazyLoad();
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
        //这里进行三个条件的判断，如果有一个不满足，都将不进行加载
        if (!mIsPrepare || !mIsVisible || !mIsFirstLoad) {
            return;
        }
        getComic();
        //数据加载完毕,恢复标记,防止重复加载
        mIsFirstLoad = false;
    }

    @Override
    public void onRecyclerViewItemClick(View view, ComicListBean.ListBean bean) {
        //todo 跳转到漫画详情页
        ComicDetailsActivity.startComicDetailsActivity(getContext(), bean.url);
    }

    /**
     * 获取漫画分类详情
     */
    private void getComic() {
        int currentPage = Integer.valueOf(mComicItem.pages);
//        int maxPage = Integer.valueOf(mComicItem.dpages);
//        //已经是最后一页了
//        if (currentPage == maxPage) {
//            return;
//        }
        mObserver = new MyComicObserver<ComicListBean>(getActivity()) {
            @Override
            public void onSuccess(ComicListBean result) {
                if (result == null) {
                    Toast.makeText(getActivity(), "无数据", Toast.LENGTH_LONG).show();
                    return;
                }

                //获取分类下的页数 总数 当前的页
                if (!TextUtils.isEmpty(result.list.get(0).pages)) {
                    mComicItem = result.list.get(0);
                    result.list.remove(0);
                }
                mLsData = result.list;
                mAdapter.addMoreData(mLsData);
            }


            @Override
            public void onFailure(Throwable e, String errorMsg) {
                Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
            }
        };

        RetrofitComicUtils.getApiUrl()
                .getComicList(mMhlb + "-" + (currentPage + 1))
                .compose(RxHelper.observableIO2Main(getActivity()))
                .subscribe(mObserver);
    }
}
