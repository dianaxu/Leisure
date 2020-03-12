package com.example.leisure.activity.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.leisure.R;
import com.example.leisure.activity.adapter.JokeAdapter;
import com.example.leisure.bean.JokeBean;
import com.example.leisure.retrofit.MyObserver;
import com.example.leisure.retrofit.RetrofitUtils;
import com.example.leisure.retrofit.RxHelper;
import com.example.leisure.util.DensityUtil;
import com.example.leisure.widget.SpacesItemDecoration;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 推荐页
 * 1.实现图片和视频和文字为一体的展示页 （目前展示图片页）
 * 2.实现两列的展示，样式的调整
 * 3.图片的高度问题，视频及文字后期处理
 * 4.调转到详情页看大图
 * 5.界面的调整样式
 * 6.优化 自动加载更多
 * 7.屏幕旋转不进行重新加载数据
 */
public class RecommendFragment extends Fragment {
    private TwinklingRefreshLayout mTrlView;
    private RecyclerView mRvView;

    private MyObserver mObserver;
    private JokeAdapter mAdapter;
    private List<JokeBean> mLsData;

    int mPage = 0;
    int mCount = 8;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLsData = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);
        mTrlView = view.findViewById(R.id.trl_view);
        mRvView = view.findViewById(R.id.rv_view);

        initTwinklingRefreshLayout();
        initRecyclerView();
        return view;
    }

    private void initRecyclerView() {
        //设置间距
        //        int spanCount = 2; // 3 columns
        int spacing = DensityUtil.dip2px(getContext(), 5);
//        boolean includeEdge = false;
//        mRvView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
        mRvView.addItemDecoration(new SpacesItemDecoration(spacing));

        // StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRvView.setLayoutManager(layoutManager);

        mAdapter = new JokeAdapter(getContext(), mLsData);
        mRvView.setAdapter(mAdapter);
    }

    private void initTwinklingRefreshLayout() {
        mTrlView.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.finishRefreshing();
                    }
                }, 2000);
                mPage = 0;
                getData();
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.finishLoadmore();
                    }
                }, 2000);
                mPage++;
                getData();
            }
        });

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getData();
    }

    private void getData() {
        mObserver = new MyObserver<List<JokeBean>>(getContext()) {
            @Override
            public void onSuccess(List<JokeBean> result) {
                if (result != null) {
                    if (mPage == 0)
                        mAdapter.refreshData(result);
                    else
                        mAdapter.addMoreData(result);
                }
            }

            @Override
            public void onFailure(Throwable e, String errorMsg) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
        RetrofitUtils.getApiUrl()
                .getJoke(mPage, mCount, "image")
                .compose(RxHelper.observableIO2Main(getContext()))
                .subscribe(mObserver);
    }


}
