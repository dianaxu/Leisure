package com.example.leisure.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.example.leisure.R;
import com.example.leisure.bean.JokeBean;
import com.example.leisure.glide.ImageLoader;

import java.util.List;

import androidx.annotation.NonNull;

public class JokeAdapter extends BaseRecyclerViewAdapter<JokeBean> {

    public JokeAdapter(@NonNull Context context, List<JokeBean> data) {
        super(context, data);
    }

    @Override
    public int getResourseId() {
        return R.layout.item_joke;
    }

    @Override
    public void onBindView(BaseViewHolder holder, int position) {
        JokeBean bean = mLsData.get(position);
        ImageLoader.with(mContext, bean.images, (ImageView) holder.getView(R.id.iv_image));
        ImageLoader.withCircle(mContext, bean.header, (ImageView) holder.getView(R.id.iv_header));
        holder.setTextOfTextView(R.id.tv_text, bean.text);
        holder.setTextOfTextView(R.id.tv_up, bean.up);
        holder.setTextOfTextView(R.id.tv_name, bean.name);
    }
}
