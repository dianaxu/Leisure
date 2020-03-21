package com.example.leisure.activity.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.example.leisure.R;
import com.example.leisure.bean.WangYiNewsBean;
import com.example.leisure.glide.ImageLoader;

import java.util.List;

import androidx.annotation.NonNull;

public class WYNewsAdapter extends BaseRecyclerViewAdapter<WangYiNewsBean> implements View.OnClickListener {

    public WYNewsAdapter(@NonNull Context context, List<WangYiNewsBean> data) {
        super(context, data);
    }

    @Override
    public int getResourseId() {
        return R.layout.item_wy_news;
    }

    @Override
    public void onBindView(BaseViewHolder holder, int position) {
        WangYiNewsBean bean = mLsData.get(position);
        holder.setText(R.id.tv_title, bean.title);
        ImageView imageView = (ImageView) holder.getView(R.id.iv_image);
        ImageLoader.getInstance().with(mContext, bean.image, imageView);
        String time = bean.passtime.split(" ")[0].replace("-", ".");
        holder.setText(R.id.tv_time, time);

        holder.setItemListener(position, this);
    }


    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (Integer) v.getTag(), mLsData.get((Integer) v.getTag()));
        }
    }


}
