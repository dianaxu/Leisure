package com.example.leisure.activity.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.example.leisure.R;
import com.example.leisure.db.greendao.ComicChapterBean;
import com.example.leisure.util.Constant;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class DownloadChapterAdapter extends BaseRecyclerViewAdapter<ComicChapterBean> {
    public static final String UPDATE_ITEM_STATE = "update_item_state";
    public static final String UPDATE_ITEM_PROGRESS = "update_item_progress";
    public static final String UPDATE_ITEM_FINISH = "update_item_finish";
    public static final String UPDATE_ITEM_FAIL = "update_item_fail";

    private List<Long> mLsPlay = new ArrayList<>();
    private List<Long> mLsPlause = new ArrayList<>();
    private OnTaskListener mListener;
    private String TAG = "DownloadTask";

    public interface OnTaskListener {
        //        void startTask(int position, long chapterId);
        void startTask(ComicChapterBean chapter);

        //        void stopTask(int position, long chapterId);
        void stopTask(ComicChapterBean chapter);

        void startTask(List<ComicChapterBean> list);

        void stopTask(List<ComicChapterBean> list);

        void updateButtonText(Boolean hasPauseTask);
    }

    public List<Long> getmLsPlay() {
        return mLsPlay;
    }

    public List<Long> getmLsPlause() {
        return mLsPlause;
    }

    public void setmLsPlay(List<Long> mLsPlay) {
        this.mLsPlay = mLsPlay;
    }

    public void setmLsPlause(List<Long> mLsPlause) {
        this.mLsPlause = mLsPlause;
    }

    public void addOnTaskListener(OnTaskListener listener) {
        this.mListener = listener;
    }

    public DownloadChapterAdapter(@NonNull Context context, List<ComicChapterBean> data) {
        super(context, data);
    }

    @Override
    public int getResourseId() {
        return R.layout.item_download_chapter;
    }

    @Override
    public void onBindView(BaseViewHolder holder, int position) {
        ComicChapterBean bean = mLsData.get(position);

        holder.setText(R.id.tv_chapter_name, bean.getNum());
        TextView tvCount = (TextView) holder.getView(R.id.tv_count);
        ImageView ivPlay = (ImageView) holder.getView(R.id.iv_play);

        NumberProgressBar bar = (NumberProgressBar) holder.getView(R.id.pb_update_progress);
        if (bean.getMaxCount() != 0) {
            bar.setProgress(bean.getCacheCount() * 100 / bean.getMaxCount());
        } else {
            bar.setProgress(0);
        }

        tvCount.setText(String.format("%1$d/%2$d", bean.getCacheCount(), bean.getMaxCount()));

        ivPlay.setSelected(bean.getCacheState() == Constant.DownloadState.DOWNLOADING);
        ivPlay.setTag(bean.get_id());
        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long chapterId = (long) v.getTag();
                v.setSelected(!v.isSelected());
                setCacheState(chapterId, v.isSelected());
            }
        });
    }

    @Override
    protected void onBindView(BaseViewHolder holder, int position, List<Object> payloads) {
        ComicChapterBean bean = mLsData.get(position);

        TextView tvCount = (TextView) holder.getView(R.id.tv_count);
        ImageView ivPlay = (ImageView) holder.getView(R.id.iv_play);
        NumberProgressBar bar = (NumberProgressBar) holder.getView(R.id.pb_update_progress);

        String state = (String) payloads.get(payloads.size() - 1);
        Log.e(TAG, "chapter--> onBindView: pos" + position + ":" + state + ":" + bean.getCacheCount() + "max" + bean.getMaxCount());
        int cacheState = bean.getCacheState();
        switch (state) {
            case UPDATE_ITEM_STATE:
                ivPlay.setSelected(cacheState == Constant.DownloadState.DOWNLOADING);
                break;
            case UPDATE_ITEM_PROGRESS:
                tvCount.setText(String.format("%1$d/%2$d", bean.getCacheCount(), bean.getMaxCount()));
                if (bean.getMaxCount() != 0) {
                    bar.setProgress(bean.getCacheCount() * 100 / bean.getMaxCount());
                } else {
                    bar.setProgress(0);
                }

                break;
            case UPDATE_ITEM_FINISH:
                ivPlay.setVisibility(View.GONE);
                break;
            case UPDATE_ITEM_FAIL:
                ivPlay.setSelected(bean.getCacheState() == Constant.DownloadState.DOWNLOADING);
                tvCount.setText("下载失败");
                break;
            default:
                break;
        }

    }

    /**
     * @param chapterId
     * @param isSelected true :启动  ； false  暂停
     */
    private void setCacheState(long chapterId, boolean isSelected) {
        int position = getPosition(chapterId);
        if (position == -1) return;
        //暂停--->开启
        if (isSelected) {
            mLsData.get(position).setCacheState(Constant.DownloadState.DOWNLOADING);
            mLsPlause.remove(chapterId);
            mLsPlay.add(chapterId);
            if (mListener != null) {
                mListener.startTask(mLsData.get(position));
                mListener.updateButtonText(hasPauseTask());
            }
        } else {
            //开启--->暂停
            mLsData.get(position).setCacheState(Constant.DownloadState.DOWNLOAD_CANCEL);
            mLsPlay.remove(chapterId);
            mLsPlause.add(chapterId);
            if (mListener != null) {
                mListener.stopTask(mLsData.get(position));
                mListener.updateButtonText(hasPauseTask());
            }
        }
    }

    public int getPosition(long chapterId) {
        int position = -1;
        for (int i = 0; i < mLsData.size(); i++) {
            if (mLsData.get(i).get_id() == chapterId) {
                position = i;
                break;
            }
        }
        return position;
    }

    public void updateState(int position, int state) {
        mLsData.get(position).setCacheState(state);
        notifyItemChanged(position, UPDATE_ITEM_STATE);
    }

    //更新数据
    public void updateData(List<ComicChapterBean> list) {
        if (list == null) return;
        this.mLsData = list;

        mLsPlay.clear();
        mLsPlause.clear();
        for (int i = 0; i < mLsData.size(); i++) {
            if (mLsData.get(i).getCacheState() == Constant.DownloadState.DOWNLOADING)
                mLsPlay.add(mLsData.get(i).get_id());
            else
                mLsPlause.add(mLsData.get(i).get_id());
        }
        notifyDataSetChanged();
    }

    //有暂停的任务
    public boolean hasPauseTask() {
//        if (mLsPlause.size() >= 0) return true;
//        else if (mLsPlay.size() >= 0 && mLsPlause.size() == 0) return false;
        return mLsPlause.size() > 0;
    }

    public boolean hasPlayTask() {
        return mLsPlay.size() > 0;
    }

    //更新
    private List<ComicChapterBean> updateCacheState(int state, List<Long> list) {
        List<ComicChapterBean> chapters = new ArrayList<>();
        for (int i = 0; i < mLsData.size(); i++) {
            if (list.contains(mLsData.get(i).get_id())) {
                Log.e(TAG, mLsData.get(i).get_id() + " updateCacheState: " + state);
                ComicChapterBean bean = mLsData.get(i);
                bean.setCacheState(state);
                chapters.add(bean);
            }
        }
        return chapters;
    }

    //开启或者暂停所有任务
    public void playOrPauseAll() {
        //有取消的任务--->开启所有任务
        if (hasPauseTask()) {
            startAllTask();
        } else if (mLsPlay.size() > 0) {
            //没有暂停的任务，有运行的任务 则需要暂停任务
            stopAllTask();
        }
    }

    //暂停所有的任务  暂停
    private void stopAllTask() {
        Log.e(TAG, "stopAllTask: " + mLsPlay.toString());
        List<ComicChapterBean> chapters = updateCacheState(Constant.DownloadState.DOWNLOAD_CANCEL, mLsPlay);
        notifyDataSetChanged();

        mLsPlause.addAll(mLsPlay);
        mLsPlay.clear();
        if (mListener != null) {
            mListener.updateButtonText(true);
            mListener.stopTask(chapters);
        }
    }

    //开启所有任务 指的是那些暂停着的任务
    private void startAllTask() {
        Log.e(TAG, "startAllTask: " + mLsPlause.toString());
        List<ComicChapterBean> chapters = updateCacheState(Constant.DownloadState.DOWNLOADING, mLsPlause);
        notifyDataSetChanged();

        mLsPlay.addAll(mLsPlause);
        mLsPlause.clear();
        if (mListener != null) {
            mListener.updateButtonText(false);
            mListener.startTask(chapters);
        }
    }
}
