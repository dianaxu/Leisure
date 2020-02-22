package com.example.leisure.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> implements View.OnClickListener {

    protected Context mContext;
    protected List<T> mLsData;
    protected OnRecyclerViewItemClickListener<T> mOnItemClickListener;


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
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        holder.setItemListener(this, position);
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

    public void addOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener<T> listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onRecyclerViewItemClick(v, (Integer) position, mLsData.get((Integer) position));
        }
    }


    public interface OnRecyclerViewItemClickListener<T> {
        void onRecyclerViewItemClick(View view, int position, T bean);
    }
}
