package com.example.leisure.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.donkingliang.groupedadapter.adapter.GroupedRecyclerViewAdapter;
import com.donkingliang.groupedadapter.holder.BaseViewHolder;
import com.example.leisure.R;
import com.example.leisure.bean.ComicItemBean;
import com.example.leisure.glide.ImageLoader;

import java.util.List;

public class ComicContentGroupedListAdapter extends GroupedRecyclerViewAdapter {

    private List<ComicItemBean.ChapterBean> mLsData;
    private OnBindChildClickListener mListener;

    public ComicContentGroupedListAdapter(Context context, List<ComicItemBean.ChapterBean> lsData) {
        super(context);
        this.mLsData = lsData;
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
            ImageView imageView = (ImageView) holder.get(R.id.iv_cover);
            ImageLoader.with(mContext, mLsData.get(groupPosition).list.get(childPosition).img, imageView);
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
