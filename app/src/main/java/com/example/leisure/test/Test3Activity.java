package com.example.leisure.test;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.donkingliang.groupedadapter.adapter.GroupedRecyclerViewAdapter;
import com.donkingliang.groupedadapter.holder.BaseViewHolder;
import com.example.leisure.R;
import com.example.leisure.glide.ImageLoader;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class Test3Activity extends Activity implements View.OnClickListener {

    private RecyclerView mRvView;
    private Button mBtnAdd;
    private TwinklingRefreshLayout trl_view;


    private List<Group> mLsData = new ArrayList<>();

    private String[] img = {
            "https://m-bnmanhua-com.mipcdn.com/i/img.detatu.com/upload/files/8918/810406/15437245221.jpg",
            "https://m-bnmanhua-com.mipcdn.com/i/img.detatu.com/upload/files/8918/810406/15437245221.jpg",
            "https://m-bnmanhua-com.mipcdn.com/i/img.detatu.com/upload/files/8918/810406/15437245221.jpg",
            "https://m-bnmanhua-com.mipcdn.com/i/img.detatu.com/upload/files/8918/810406/15437245221.jpg",
            "https://m-bnmanhua-com.mipcdn.com/i/img.detatu.com/upload/files/8918/810406/15437245221.jpg",
            "https://m-bnmanhua-com.mipcdn.com/i/img.detatu.com/upload/files/8918/810406/15437245222.jpg"
    };
    private String[] img1 = {
            "https://m-bnmanhua-com.mipcdn.com/i/img.detatu.com/upload/files/8918/810394/15437243160.jpg",
            "https://m-bnmanhua-com.mipcdn.com/i/img.detatu.com/upload/files/8918/810394/15437243160.jpg",
            "http://img.manhua.weibo.com/comic/91/72591/371970/005_2463776bc48430cc163953d0b760d2ab_big.webp",
            "https://m-bnmanhua-com.mipcdn.com/i/img.detatu.com/upload/files/8918/810406/154372453646.jpg"
    };

    private int mCurrentGroup = 20;
    private GroupedListAdapter mAdapter;
    private LinearLayoutManager layout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test3);

        mRvView = findViewById(R.id.rv_view);
        mBtnAdd = findViewById(R.id.btn_add);
        trl_view = findViewById(R.id.trl_view);

        findViewById(R.id.btn_update).setOnClickListener(this);
        findViewById(R.id.btn_qh).setOnClickListener(this);


        trl_view.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                LinearLayoutManager manager = (LinearLayoutManager) mRvView.getLayoutManager();
                //到达第一个group
                int first = manager.findFirstVisibleItemPosition();
                int groupPositionForPosition1 = mAdapter.getGroupPositionForPosition(first);
                if (groupPositionForPosition1 == 0 && first == 0) {
                    Toast.makeText(Test3Activity.this, "已到达第一个", Toast.LENGTH_SHORT).show();
                    trl_view.finishRefreshing();
                    return;
                }
                //到达group的第一个
                if (first == 0) {
                    groupPositionForPosition1--;
                    mLsData.get(groupPositionForPosition1).visible = true;
                    if (mLsData.get(groupPositionForPosition1).list.size() == 0) {
                        for (int j = 0; j < img1.length; j++) {
                            Bean bean = new Bean(img1[j]);
                            mLsData.get(groupPositionForPosition1).list.add(bean);
                        }
                    }
                    mAdapter.notifyGroupInserted(groupPositionForPosition1);
                }
                trl_view.finishRefreshing();
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                LinearLayoutManager manager = (LinearLayoutManager) mRvView.getLayoutManager();
                int last = manager.findLastVisibleItemPosition();
                //到达最后的group
                int groupPositionForPosition = mAdapter.getGroupPositionForPosition(last);
                if (groupPositionForPosition + 1 == mLsData.size() && last + 1 == mAdapter.getItemCount()) {
                    Toast.makeText(Test3Activity.this, "已到达最后", Toast.LENGTH_SHORT).show();
                    trl_view.finishLoadmore();
                    return;
                }
                //到达group的最后一个
                if (last + 1 == mAdapter.getItemCount()) {
                    groupPositionForPosition++;
                    mLsData.get(groupPositionForPosition).visible = true;
                    if (mLsData.get(groupPositionForPosition).list.size() == 0) {
                        for (int j = 0; j < img1.length; j++) {
                            Bean bean = new Bean(img1[j]);
                            mLsData.get(groupPositionForPosition).list.add(bean);
                        }
                    }
                    mAdapter.notifyGroupInserted(groupPositionForPosition);
                }
                trl_view.finishLoadmore();
            }
        });


        mBtnAdd.setOnClickListener(this);
        for (int i = 0; i < 40; i++) {
            List<Bean> list = new ArrayList<>();
            if (i == 0) {
                for (int j = 0; j < img.length; j++) {
                    Bean bean = new Bean(img[j]);
                    list.add(bean);
                }
            }

            if (i == 20) {
                for (int j = 0; j < img.length; j++) {
                    Bean bean = new Bean(img[j]);
                    list.add(bean);
                }
            }
            Group group = new Group("分组" + i, list);
            mLsData.add(group);
        }

        mLsData.get(mCurrentGroup).visible = true;


        mAdapter = new GroupedListAdapter(this);
        mRvView.setAdapter(mAdapter);
        layout = new LinearLayoutManager(this);
        mRvView.setLayoutManager(layout);

        mRvView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                int last = manager.findLastVisibleItemPosition();
//                //到达最后的group
//                int groupPositionForPosition = mAdapter.getGroupPositionForPosition(last);
//                if (groupPositionForPosition + 1 == mLsData.size() && last + 1 == mAdapter.getItemCount()) {
//                    Toast.makeText(Test3Activity.this, "已到达最后", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                //到达group的最后一个
//                if (last + 1 == mAdapter.getItemCount()) {
//                    groupPositionForPosition++;
//                    mLsData.get(groupPositionForPosition).visible = true;
//                    if (mLsData.get(groupPositionForPosition).list.size() == 0) {
//                        for (int j = 0; j < 20; j++) {
//                            Bean bean = new Bean("img" + groupPositionForPosition + ":" + j);
//                            mLsData.get(groupPositionForPosition).list.add(bean);
//                        }
//                    }
//                    mAdapter.notifyGroupInserted(groupPositionForPosition);
//                }
//
//                //到达第一个group
//                int first = manager.findFirstCompletelyVisibleItemPosition();
//                int groupPositionForPosition1 = mAdapter.getGroupPositionForPosition(first);
//                if (groupPositionForPosition1 == 0 && first == 0) {
//                    Toast.makeText(Test3Activity.this, "已到达第一个", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                //到达group的第一个
//                if (first == 0) {
//                    groupPositionForPosition1--;
//                    mLsData.get(groupPositionForPosition1).visible = true;
//                    if (mLsData.get(groupPositionForPosition1).list.size() == 0) {
//                        for (int j = 0; j < 20; j++) {
//                            Bean bean = new Bean("img" + groupPositionForPosition1 + ":" + j);
//                            mLsData.get(groupPositionForPosition1).list.add(bean);
//                        }
//                    }
//                    mAdapter.notifyGroupInserted(groupPositionForPosition1);
//
//                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_add) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) mRvView.getLayoutManager();
            int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
            int groupPositionForPosition = mAdapter.getGroupPositionForPosition(lastVisibleItemPosition);

            if (groupPositionForPosition == mLsData.size() - 1) {
                Toast.makeText(this, "已到达最后一页", Toast.LENGTH_SHORT).show();
                return;
            }
            groupPositionForPosition++;
            mLsData.get(groupPositionForPosition).visible = true;
            if (mLsData.get(groupPositionForPosition).list.size() == 0) {
                for (int j = 0; j < img.length; j++) {
                    Bean bean = new Bean(img[j]);
                    mLsData.get(groupPositionForPosition).list.add(bean);
                }
            }
            mAdapter.notifyGroupInserted(groupPositionForPosition);
        } else if (v.getId() == R.id.btn_update) {

            LinearLayoutManager layoutManager = (LinearLayoutManager) mRvView.getLayoutManager();
            int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
            int groupPositionForPosition = mAdapter.getGroupPositionForPosition(firstVisibleItem);

            if (groupPositionForPosition == 0) {
                Toast.makeText(this, "已到顶", Toast.LENGTH_SHORT).show();
                return;
            }
            groupPositionForPosition--;
            mLsData.get(groupPositionForPosition).visible = true;
            if (mLsData.get(groupPositionForPosition).list.size() == 0) {
                for (int j = 0; j < img1.length; j++) {
                    Bean bean = new Bean(img1[j]);
                    mLsData.get(groupPositionForPosition).list.add(bean);
                }
            }
            mAdapter.notifyGroupInserted(groupPositionForPosition);
        } else if (v.getId() == R.id.btn_qh) {
            mCurrentGroup = 2;

            int position = mAdapter.getChildPositionForPosition(2, 0);
            if (mLsData.get(mCurrentGroup).visible) {
                layout.scrollToPosition(position);
            } else {
                mLsData.get(mCurrentGroup).visible = true;
                if (mLsData.get(mCurrentGroup).list.size() == 0) {
                    for (int j = 0; j < img.length; j++) {
                        Bean bean = new Bean(img[j]);
                        mLsData.get(mCurrentGroup).list.add(bean);
                    }
                    //重装数据
                }
                mAdapter.notifyGroupInserted(mCurrentGroup);
                layout.scrollToPosition(position);
            }
        }

    }

    public class GroupedListAdapter extends GroupedRecyclerViewAdapter {
        public GroupedListAdapter(Context context) {
            super(context);
        }


        @Override
        public int getGroupCount() {
            return mLsData.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return mLsData.get(groupPosition).visible ? mLsData.get(groupPosition).list.size() : 0;
        }

        @Override
        public boolean hasHeader(int groupPosition) {
            return false;
        }

        @Override
        public boolean hasFooter(int groupPosition) {
            return false;
        }

        @Override
        public int getHeaderLayout(int viewType) {
            return 0;
        }

        @Override
        public int getFooterLayout(int viewType) {
            return 0;
        }

        @Override
        public int getChildLayout(int viewType) {
//            return android.R.layout.simple_list_item_1;
            return R.layout.item_image;
        }

        @Override
        public void onBindHeaderViewHolder(BaseViewHolder holder, int groupPosition) {

        }

        @Override
        public void onBindFooterViewHolder(BaseViewHolder holder, int groupPosition) {

        }

        @Override
        public void onBindChildViewHolder(BaseViewHolder holder, int groupPosition, int childPosition) {
            if (mLsData.get(groupPosition).visible) {
                ImageLoader.with(Test3Activity.this,
                        mLsData.get(groupPosition).list.get(childPosition).img,
                        (ImageView) holder.get(R.id.iv_cover));
//                holder.setText(android.R.id.text1, mLsData.get(groupPosition).list.get(childPosition).img);
            }
        }
    }


    public class Group {

        public Group(String name, List<Bean> list) {
            this.name = name;
            this.list = list;
        }

        public String name;
        public List<Bean> list;
        public boolean visible;


    }

    public class Bean {
        public Bean(String img) {
            this.img = img;
        }

        public String img;
    }
}
