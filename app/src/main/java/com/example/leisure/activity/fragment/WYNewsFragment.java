package com.example.leisure.activity.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.leisure.R;
import com.example.leisure.activity.WYNewsDetailsActivity;
import com.example.leisure.activity.adapter.BaseRecyclerViewAdapter;
import com.example.leisure.activity.adapter.WYNewsAdapter;
import com.example.leisure.bean.WangYiNewsBean;
import com.example.leisure.retrofit.MyObserver;
import com.example.leisure.retrofit.RetrofitUtils;
import com.example.leisure.retrofit.RxHelper;
import com.example.leisure.util.DensityUtil;
import com.example.leisure.widget.GridSpacingItemDecoration;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 网易新闻页
 * 1.实现xml
 * 2.新闻列表展示
 * 3.跳转到新闻详细页
 * 4.屏幕旋转测试
 * <p>
 * todo 问题1：列表显示高度有问题
 * 问题2：列表显示样式需要调整
 * 问题3：列表的间距有问题，时间没有显示出来
 * 问题4：实现滑动某个地方时自动加载数据
 * 问题5：可以考虑用StaggeredGridLayoutManager
 * 问题6：横竖屏切换
 * 问题7：加载更多需要再看下
 */
public class WYNewsFragment extends Fragment implements BaseRecyclerViewAdapter.OnItemClickListener<WangYiNewsBean> {
    private TwinklingRefreshLayout mTrlView;
    private RecyclerView mRvView;

    private MyObserver mObserver;
    private WYNewsAdapter mAdapter;

    private List<WangYiNewsBean> mLsData;
    private int mPage = 1;
    private int mCount = 10;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLsData = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wy_news, container, false);
        mTrlView = view.findViewById(R.id.trl_view);
        mRvView = view.findViewById(R.id.rv_view);
        initTwinklingRefreshLayout();
        initRecyclerView();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getData();
    }

    private void initRecyclerView() {
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);

        mRvView.setLayoutManager(layoutManager);
        int spanCount = 2;
        int spaning = DensityUtil.dip2px(getContext(), 5);
        mRvView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spaning, true));

        mAdapter = new WYNewsAdapter(getContext(), mLsData);
        mRvView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
    }

    private void initTwinklingRefreshLayout() {
        mTrlView.setEnableRefresh(false);
        mTrlView.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        refreshLayout.finishRefreshing();
//                    }
//                }, 2000);
//                mPage = 1;
//                getData();
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


    private void getData() {
        mObserver = new MyObserver<List<WangYiNewsBean>>(getContext()) {
            @Override
            public void onSuccess(List<WangYiNewsBean> result) {
                if (result != null) {
                    if (mPage == 1)
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
                .getWangYiNews(mPage, mCount)
                .compose(RxHelper.observableIO2Main(getContext()))
                .subscribe(mObserver);
    }

    @Override
    public void onItemClick(View view, int position, WangYiNewsBean bean) {
        WYNewsDetailsActivity.startActivity(getContext(), bean.path);
    }
}
