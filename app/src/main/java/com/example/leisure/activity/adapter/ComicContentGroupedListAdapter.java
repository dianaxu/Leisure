package com.example.leisure.activity.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.donkingliang.groupedadapter.adapter.GroupedRecyclerViewAdapter;
import com.donkingliang.groupedadapter.holder.BaseViewHolder;
import com.example.leisure.R;
import com.example.leisure.db.greendao.ComicChapterBean;
import com.example.leisure.glide.ImageLoader;

import java.util.List;

public class ComicContentGroupedListAdapter extends GroupedRecyclerViewAdapter {

    private List<ComicChapterBean> mLsData;
    private OnBindChildClickListener mListener;
    private int mWindowWidth;


    public ComicContentGroupedListAdapter(Context context, int windowWidth, List<ComicChapterBean> lsData) {
        super(context);
        this.mLsData = lsData;
        mWindowWidth = windowWidth;

    }

    public void updateData(List<ComicChapterBean> list) {
        this.mLsData = list;
        notifyDataChanged();
    }

    public void updateGroupInserted(List<ComicChapterBean> list, int groupPosition) {
        this.mLsData = list;
        notifyGroupInserted(groupPosition);
    }


    @Override
    public int getGroupCount() {
        return mLsData == null ? 0 : mLsData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (mLsData.get(groupPosition).visible) {
            if (mLsData.get(groupPosition).list != null) {
                return mLsData.get(groupPosition).list.size();
            }
        }
        return 0;
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
            ImageView imageView = holder.get(R.id.iv_cover);
            ImageLoader.getInstance().withWidthMatch(mContext, mWindowWidth, mLsData.get(groupPosition).list.get(childPosition).img, imageView);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        mListener.onBindChildClick();
                    }
                }
            });
        }
    }

    public void addOnBindChildClickListener(OnBindChildClickListener listener) {
        this.mListener = listener;
    }

    public interface OnBindChildClickListener {
        void onBindChildClick();
    }


}
