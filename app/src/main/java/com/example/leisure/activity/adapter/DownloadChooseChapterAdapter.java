package com.example.leisure.activity.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.leisure.R;
import com.example.leisure.db.greendao.ComicChapterBean;
import com.example.leisure.util.Constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;

public class DownloadChooseChapterAdapter extends BaseRecyclerViewAdapter<ComicChapterBean> implements View.OnClickListener {
    private List<Long> mLsSelected = new ArrayList<>();
    private List<Long> mLsNotSelected = new ArrayList<>();

    private OnSelectListener mListener;

    public List<Long> getLsNotSelected() {
        return mLsNotSelected;
    }

    public List<Long> getSelectedChapters() {
        return mLsSelected;
    }

    public interface OnSelectListener {
        void UpdateUI(String text, int selectCount);
    }

    public void addOnSelectListener(OnSelectListener listener) {
        this.mListener = listener;
    }

    public DownloadChooseChapterAdapter(@NonNull Context context, List<ComicChapterBean> data, List<Long> lsNotSelect) {
        super(context, data);
        this.mLsNotSelected = lsNotSelect;
    }

    @Override
    public int getResourseId() {
        return R.layout.item_choose_chapter;
    }

    @Override
    public void onBindView(BaseViewHolder holder, int position) {
        RelativeLayout rlView = (RelativeLayout) holder.getView(R.id.rl_view);
        TextView tvName = (TextView) holder.getView(R.id.tv_name);
        ImageView ivImage = (ImageView) holder.getView(R.id.iv_image);
        ComicChapterBean bean = mLsData.get(position);
        tvName.setText(mLsData.get(position).getNum());
        tvName.setLines(1);
        tvName.setEllipsize(TextUtils.TruncateAt.END);

        //未处理的章节
        if (bean.getCacheState() == Constant.DownloadState.DOWNLOAD_NOT) {
            rlView.setTag(position);
            rlView.setOnClickListener(this);
            ivImage.setVisibility(View.VISIBLE);
            long id = bean.get_id();
            if (isSelect(id)) {
                tvName.setTextColor(mContext.getResources().getColor(R.color.loginTextColor));
                rlView.setSelected(true);
                rlView.setBackgroundResource(R.drawable.shap_bg_selected);
            } else {
                tvName.setTextColor(mContext.getResources().getColor(R.color.textHeaderColor));
                rlView.setBackgroundResource(R.drawable.shap_bg_default);
            }
        } else if (bean.getCacheState() == Constant.DownloadState.DOWNLOADING ||
                bean.getCacheState() == Constant.DownloadState.DOWNLOAD_CANCEL ||
                bean.getCacheState() == Constant.DownloadState.DOWNLOADED) {
            ivImage.setVisibility(View.GONE);
            rlView.setBackgroundResource(R.drawable.shap_bg_grey);
            tvName.setTextColor(mContext.getResources().getColor(R.color.loginTextColor));
            rlView.setClickable(false);
        }
    }

    @Override
    public void onClick(View v) {
        int position = (int) v.getTag();
        if (R.id.rl_view == v.getId()) {
            long chapterId = mLsData.get(position).get_id();
            if (v.isSelected()) {
                mLsSelected.remove(chapterId);
                mLsNotSelected.add(chapterId);
            } else {
                mLsNotSelected.remove(chapterId);
                mLsSelected.add(chapterId);
            }
            v.setSelected(!v.isSelected());
            notifyItemChanged(position);
            updateUI();
        }
    }

    private void updateUI() {
        if (isSelectAll()) { //已经全选
            if (mListener != null) mListener.UpdateUI("取消全选", mLsSelected.size());
        } else if (isCancelSelectAll()) { //取消全选
            if (mListener != null) mListener.UpdateUI("全选", mLsSelected.size());
        } else {
            if (mListener != null) mListener.UpdateUI("全选", mLsSelected.size());
        }
    }


    public void setSelectItemOrNot(int position) {
        long id = mLsData.get(position).get_id();
        if (mLsSelected.contains(id)) {
            mLsNotSelected.add(id);
            mLsSelected.remove(id);
        } else {
            mLsSelected.add(id);
            mLsNotSelected.remove(id);
        }
        notifyItemChanged(position);
        updateUI();
    }

    //设置全选  或 取消选择
    public void setSelectAllOrNot() {
        //全选  --- >取消全选
        if (isSelectAll()) {
            mLsNotSelected.addAll(mLsSelected);
            mLsSelected.clear();
        } else {
            //取消全选  --- >全选
            mLsSelected.addAll(mLsNotSelected);
            mLsNotSelected.clear();
        }
        notifyDataSetChanged();

        updateUI();
    }

    private boolean isSelect(Long chapterId) {
        if (mLsSelected.size() == 0) return false;
        return mLsSelected.contains(chapterId);
    }

    public boolean isSelectAll() {
        return mLsNotSelected.size() == 0;
    }

    public boolean isCancelSelectAll() {
        return mLsSelected.size() == 0;
    }

    public int selectCount() {
        return mLsSelected.size();
    }

    public void sortData() {
        Collections.reverse(mLsData);
        notifyDataSetChanged();
    }


    public List<ComicChapterBean> getSelectChapter() {
        if (mLsSelected.size() == 0) return null;
        List<ComicChapterBean> chapters = new ArrayList<>();
        for (int i = 0; i < mLsData.size(); i++) {
            if (mLsSelected.contains(mLsData.get(i).get_id())) {
                chapters.add(mLsData.get(i));
            }
        }
        return chapters;
    }
}
