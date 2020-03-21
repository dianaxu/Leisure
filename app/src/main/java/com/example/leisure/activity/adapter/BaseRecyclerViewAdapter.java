package com.example.leisure.activity.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> implements BaseViewHolder.onItemClickListener {

    protected Context mContext;
    protected List<T> mLsData;
    protected OnItemClickListener<T> mOnItemClickListener;

    @Override
    public void onItemClick(View v, int position) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, position, mLsData.get(position));
        }
    }

    /**
     * item点击监听器
     */
    public interface OnItemClickListener<T> {
        /**
         * item点击事件回调
         *
         * @param view     触发事件View
         * @param position 触发事件的view所在RecyclerView中的位置
         */
        void onItemClick(View view, int position, T bean);
    }

    public void setOnItemClickListener(OnItemClickListener<T> onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }

    public BaseRecyclerViewAdapter(@NonNull Context context, List<T> data) {
        this.mContext = context;
        this.mLsData = data;
    }

    @NonNull
    @Override
    public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return BaseViewHolder.getInstance(mContext, getResourseId(), parent);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty())
            onBindViewHolder(holder, position);
        else {
            //更新控件
            onBindView(holder, position, payloads);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.setItemListener(position, this);
        onBindView(holder, position);
    }

    @Override
    public int getItemCount() {
        return mLsData == null ? 0 : mLsData.size();
    }

    //返回布局id
    public abstract @LayoutRes
    int getResourseId();

    public abstract void onBindView(BaseViewHolder holder, int position);

    protected void onBindView(BaseViewHolder holder, int position, List<Object> payloads) {

    }
    public List<T> getData() {
        return mLsData;
    }

    public void refreshData(List<T> list) {
        mLsData.addAll(0, list);
        notifyDataSetChanged();
    }

    public void addMoreData(List<T> list) {
        mLsData.addAll(list);
        notifyDataSetChanged();
    }

    public void updateData(List<T> list) {
        mLsData = list;
        notifyDataSetChanged();
    }

    public void ItemChanged(T bean, int pos) {
        mLsData.set(pos, bean);
        notifyItemChanged(pos);
    }


    public void sortData() {
        Collections.reverse(mLsData);
        notifyDataSetChanged();
    }

}
