package com.example.leisure.activity.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.leisure.R;
import com.example.leisure.activity.MusicChannelDetailsActivity;
import com.example.leisure.activity.adapter.BaseRecyclerViewAdapter;
import com.example.leisure.activity.adapter.BaseViewHolder;
import com.example.leisure.bean.ChannellistBean;
import com.example.leisure.bean.MusicBroadcastingBean;
import com.example.leisure.glide.ImageLoader;
import com.example.leisure.retrofit.MyObserver;
import com.example.leisure.retrofit.RetrofitUtils;
import com.example.leisure.retrofit.RxHelper;
import com.example.leisure.util.DensityUtil;
import com.example.leisure.widget.GridSpacingItemDecoration;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * 音乐页
 * 1.实现xml
 * 2.音乐排行榜 （防QQ排行榜）
 * 3.实现后台播放
 * 4.后期加上音乐电台界面
 * 5.跳转到音乐详情
 * 6.需要实现下载视频功能
 * 7.实现视频缓冲效果
 * <p>
 * 音乐电台接口： https://api.apiopen.top/musicBroadcasting
 * 音乐电台详情接口： https://api.apiopen.top/musicBroadcastingDetails?channelname=public_tuijian_spring
 * 音乐详情接口： https://api.apiopen.top/musicDetails?id=630071
 * 需要考虑后期加上音乐排行榜
 * 音乐排行榜接口：https://api.apiopen.top/musicRankings
 * 音乐排行榜详情接口：https://api.apiopen.top/musicRankingsDetails?type=1
 */
public class MusicFragment extends Fragment implements BaseRecyclerViewAdapter.OnItemClickListener<ChannellistBean> {
    private RecyclerView mRvView;

    private List<ChannellistBean> mLsData;
    private MyObserver mObserver;

    private BaseRecyclerViewAdapter mAdapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLsData = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music, container, false);
        mRvView = view.findViewById(R.id.rv_view);


        initRecyclerView();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getData();
    }

    private void initRecyclerView() {
        int spacing = DensityUtil.dip2px(getContext(), 5);
//        mRvView.addItemDecoration(new SpacesItemDecoration(spacing));

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL);
        mRvView.setLayoutManager(layoutManager);

        mAdapter = new BaseRecyclerViewAdapter<ChannellistBean>(getContext(), mLsData) {
            @Override
            public int getResourseId() {
                return R.layout.item_channel_details;
            }

            @Override
            public void onBindView(BaseViewHolder holder, int position) {
                ChannellistBean bean = mLsData.get(position);
                holder.setText(R.id.tv_text, bean.name);
                ImageView imageView = (ImageView) holder.getView(R.id.iv_image);
                ImageLoader.with(mContext, bean.thumb, imageView);
            }
        };

        mRvView.addItemDecoration(new GridSpacingItemDecoration(2, spacing, true));
        mRvView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
//        mAdapter.addOnRecyclerViewItemClickListener(new BaseRecyclerViewAdapter.OnRecyclerViewItemClickListener<ChannellistBean>() {
//
//            @Override
//            public void onRecyclerViewItemClick(View view, ChannellistBean bean) {
//                MusicChannelDetailsActivity.startMusicChannelDetailsActivity(getContext(), bean.ch_name, bean.thumb, bean.name);
//            }
//        });
    }

    private void getData() {
        mObserver = new MyObserver<List<MusicBroadcastingBean>>(getContext()) {
            @Override
            public void onSuccess(List<MusicBroadcastingBean> result) {
                if (result != null && result.size() > 0)
                    mAdapter.refreshData(result.get(0).channellist);
            }

            @Override
            public void onFailure(Throwable e, String errorMsg) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
        RetrofitUtils.getApiUrl()
                .musicBroadcasting()
                .compose(RxHelper.observableIO2Main(getContext()))
                .subscribe(mObserver);
    }

    @Override
    public void onItemClick(View view, int position, ChannellistBean bean) {
        MusicChannelDetailsActivity.startMusicChannelDetailsActivity(getContext(), bean.ch_name, bean.thumb, bean.name);
    }
}
