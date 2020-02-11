package com.example.leisure.adapter;

import android.content.Context;
import android.view.View;

import com.example.leisure.R;
import com.example.leisure.bean.MusicBroadcastingDetailsBean;

import java.util.List;

import androidx.annotation.NonNull;

public class MusicChannelDetailsAdapter extends BaseRecyclerViewAdapter<MusicBroadcastingDetailsBean.SonglistBean> implements View.OnClickListener {

    public MusicChannelDetailsAdapter(@NonNull Context context, List<MusicBroadcastingDetailsBean.SonglistBean> data) {
        super(context, data);
    }

    @Override
    public int getResourseId() {
        return R.layout.item_music_channel_details;
    }

    @Override
    public void onBindView(BaseViewHolder holder, int position) {
        MusicBroadcastingDetailsBean.SonglistBean bean = mLsData.get(position);
        holder.setTextOfTextView(R.id.tv_num, "" + (position + 1));
        holder.setTextOfTextView(R.id.tv_title, bean.title);
        holder.setTextOfTextView(R.id.tv_artist, bean.artist);

        holder.setItemListener(this, position);
    }


    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onRecyclerViewItemClick(v, (Integer) v.getTag(), mLsData.get((Integer) v.getTag()));
        }
    }


}
