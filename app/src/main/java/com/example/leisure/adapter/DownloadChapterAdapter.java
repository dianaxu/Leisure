package com.example.leisure.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.example.leisure.R;
import com.example.leisure.db.greendao.BookChapter;
import com.example.leisure.util.Constant;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class DownloadChapterAdapter extends BaseRecyclerViewAdapter<BookChapter> {

    private List<Long> mLsPlay = new ArrayList<>();
    private List<Long> mLsPlause = new ArrayList<>();
    private OnTaskListener mListener;
    private String TAG = "DownloadTask";

    public interface OnTaskListener {
        //        void startTask(int position, long chapterId);
        void startTask(BookChapter chapter);

        //        void stopTask(int position, long chapterId);
        void stopTask(BookChapter chapter);

        void startTask(List<BookChapter> list);

        void stopTask(List<BookChapter> list);

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

    public DownloadChapterAdapter(@NonNull Context context, List<BookChapter> data) {
        super(context, data);
    }

    @Override
    public int getResourseId() {
        return R.layout.item_download_chapter;
    }

    @Override
    public void onBindView(BaseViewHolder holder, int position) {
        BookChapter bean = mLsData.get(position);

        holder.setTextOfTextView(R.id.tv_chapter_name, bean.getNum());
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
                v.setSelected(v.isSelected() ? false : true);
                setCacheState(chapterId, v.isSelected());
            }
        });
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

    /**
     * 更新进度
     *
     * @param holder
     * @param position   章节所在Adapter的位置
     * @param chapterId  章节ID
     * @param totalCount 下载的数量
     * @param maxCount   总的数量
     * @param isFinish   true 章节下载任务完成  totalCount=maxCount 则删除数据
     */
    public void updateProgress(BaseViewHolder holder, int position, long chapterId, int totalCount, int maxCount, boolean isFinish) {
        Log.e(TAG, "updateProgress: " + chapterId + ":" + totalCount + ":" + maxCount);

        if (position == -1) return;
        mLsData.get(position).setCacheCount(totalCount);
        mLsData.get(position).setMaxCount(maxCount);
        //章节任务完成：
        // totalCount=maxCount 则需要删除
        // totalCount！=maxCount
        //章节任务更新
        //更新进度
        //更新数字提示
        if (isFinish) {
            //全部下载完
            if (totalCount == maxCount) {
                mLsData.remove(position);
                mLsPlay.remove(chapterId);
                if (mListener != null) mListener.updateButtonText(hasPauseTask());
                notifyItemRemoved(position);
                return;
            } else {
                //任务走完，有部分图片未下载完,更改cachestate =cancel，updateButtonText = true
                mLsData.get(position).setCacheState(Constant.DownloadState.DOWNLOAD_CANCEL);
                mLsPlause.add(chapterId);
                mLsPlay.remove(chapterId);
                if (mListener != null) mListener.updateButtonText(hasPauseTask());
            }
        }

        //对控件的值更新
        if (holder != null) {
            NumberProgressBar bar = (NumberProgressBar) holder.getView(R.id.pb_update_progress);
            bar.setProgress(totalCount * 100 / maxCount);
            holder.setTextOfTextView(R.id.tv_count, String.format("%1$d/%2$d", totalCount, maxCount));
            if (isFinish && holder.getView(R.id.iv_play) != null) {
                ImageView ivPlay = (ImageView) holder.getView(R.id.iv_play);
                ivPlay.setSelected(false);
            } else {
                notifyItemChanged(position);
            }
        } else if (holder == null && isFinish) {
            notifyItemChanged(position);
        }
    }

    public void removeChapter(int position) {
        mLsData.remove(position);
    }

    //更新数据
    public void updateData(List<BookChapter> list) {
        this.mLsData = list;
        mLsPlay.clear();
        mLsPlause.clear();
        for (int i = 0; i < mLsData.size(); i++) {
            if (mLsData.get(i).getCacheState() == Constant.DownloadState.DOWNLOADING)
                mLsPlay.add(mLsData.get(i).get_id());
            else
                mLsPlause.add(mLsData.get(i).get_id());
        }
        Log.e(TAG, "updateData: " + mLsPlay.toString() + mLsPlause.toString());

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
    private List<BookChapter> updateCacheState(int state, List<Long> list) {
        List<BookChapter> chapters = new ArrayList<>();
        for (int i = 0; i < mLsData.size(); i++) {
            if (list.contains(mLsData.get(i).get_id())) {
                Log.e(TAG, mLsData.get(i).get_id() + " updateCacheState: " + state);
                BookChapter bean = mLsData.get(i);
                bean.setCacheState(state);
                chapters.add(bean);
            }
        }
        return chapters;
    }

    //开启或者暂停所有任务
    public void playOrPauseAll() {
        Log.e(TAG, "playOrPauseAll: " + mLsPlay.toString() + mLsPlause.toString());
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
        List<BookChapter> chapters = updateCacheState(Constant.DownloadState.DOWNLOAD_CANCEL, mLsPlay);
        notifyDataSetChanged();

        mLsPlause.addAll(mLsPlay);
        mLsPlay.clear();
        if (mListener != null) {
            mListener.stopTask(chapters);
            mListener.updateButtonText(true);
        }
    }

    //开启所有任务 指的是那些暂停着的任务
    private void startAllTask() {
        Log.e(TAG, "startAllTask: " + mLsPlause.toString());
        List<BookChapter> chapters = updateCacheState(Constant.DownloadState.DOWNLOADING, mLsPlause);
        notifyDataSetChanged();

        mLsPlay.addAll(mLsPlause);
        mLsPlause.clear();
        if (mListener != null) {
            mListener.startTask(chapters);
            mListener.updateButtonText(false);
        }
    }
}
