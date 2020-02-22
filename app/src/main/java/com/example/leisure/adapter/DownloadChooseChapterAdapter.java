package com.example.leisure.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.leisure.R;
import com.example.leisure.db.greendao.BookChapter;
import com.example.leisure.util.Constant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;

public class DownloadChooseChapterAdapter extends BaseRecyclerViewAdapter<BookChapter> {
    private List<Long> mLsSelected = new ArrayList<>();
    private List<Long> mLsNotSelected = new ArrayList<>();

    private OnSelectListener mListener;

    public List<Long> getSelectedChapters() {
        return mLsSelected;
    }

    public interface OnSelectListener {
//        void selectAll();
//
//        void cancelSelectAll();
//
//        void notHasSelect();

        void UpdateUI(String text, int selectCount);
    }

    public void addOnSelectListener(OnSelectListener listener) {
        this.mListener = listener;
    }

    public DownloadChooseChapterAdapter(@NonNull Context context, List<BookChapter> data, List<Long> lsNotSelect) {
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
        BookChapter bean = mLsData.get(position);
        tvName.setText(mLsData.get(position).getNum());

        //未处理的章节
        if (bean.getCacheState() == Constant.DownloadState.DOWNLOAD_NOT) {
            rlView.setTag(position);
            rlView.setOnClickListener(this);
            long id = bean.get_id();
            if (isSelect(id)) {
                tvName.setTextColor(mContext.getResources().getColor(R.color.loginTextColor));

                rlView.setSelected(true);
                rlView.setBackgroundResource(R.drawable.shap_bg_selected);
            } else {
                tvName.setTextColor(mContext.getResources().getColor(R.color.textHeaderColor));
                rlView.setSelected(false);
                rlView.setBackgroundResource(R.drawable.shap_bg_default);

            }
        }
        if (bean.getCacheState() == Constant.DownloadState.DOWNLOADING ||
                bean.getCacheState() == Constant.DownloadState.DOWNLOAD_CANCEL ||
                bean.getCacheState() == Constant.DownloadState.DOWNLOADED) {
            ivImage.setVisibility(View.GONE);
            rlView.setBackgroundResource(R.drawable.shap_bg_grey);
            tvName.setTextColor(mContext.getResources().getColor(R.color.loginTextColor));
            rlView.setClickable(false);
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


    public List<BookChapter> getSelectChapter() {
        if (mLsSelected.size() == 0) return null;
        List<BookChapter> chapters = new ArrayList<>();
        for (int i = 0; i < mLsData.size(); i++) {
            if (mLsSelected.contains(mLsData.get(i).get_id())) {
                chapters.add(mLsData.get(i));
            }
        }
        return chapters;
    }
}
