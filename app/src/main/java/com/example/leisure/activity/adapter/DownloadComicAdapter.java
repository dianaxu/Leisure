package com.example.leisure.activity.adapter;


import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leisure.R;
import com.example.leisure.db.greendao.ComicBookBean;
import com.example.leisure.glide.ImageLoader;
import com.example.leisure.util.Constant;
import com.example.leisure.widget.WaveImageView;

import java.util.List;

import androidx.annotation.NonNull;

public class DownloadComicAdapter extends BaseRecyclerViewAdapter<ComicBookBean> implements View.OnClickListener {
    public static final String UPDATE_ITEM_STATE = "update_item_state";
    public static final String UPDATE_ITEM_PROGRESS = "update_item_progress";
    public static final String UPDATE_ITEM_FINISH = "update_item_finish";

    private int mCurrentDownloadBookId = 0; //当前正在下载的书
    private onTaskListener mTaskListener;
    private String TAG = "DownloadTask";

    public void setmCurrentDownloadBookId(int mCurrentDownloadBookId) {
        this.mCurrentDownloadBookId = mCurrentDownloadBookId;
    }

    public void addnTaskListener(onTaskListener taskListener) {
        this.mTaskListener = taskListener;
    }

    public DownloadComicAdapter(@NonNull Context context, List<ComicBookBean> data) {
        super(context, data);
    }

    @Override
    public int getResourseId() {
        return R.layout.item_cache_book;
    }

    @Override
    public void onBindView(BaseViewHolder holder, int position) {
        ComicBookBean bean = mLsData.get(position);

        holder.setText(R.id.tv_name, bean.getName()); //书名
        ImageLoader.getInstance().with(mContext, bean.getCover(), (ImageView) holder.getView(R.id.iv_image)); //书图片
        ImageView ivPlay = (ImageView) holder.getView(R.id.iv_play); //可暂停|下载
        ivPlay.setTag(position);
        WaveImageView waveImageView = (WaveImageView) holder.getView(R.id.wiv_view); //显示进度
        waveImageView.setProgress((1.0f - bean.getProgress()));
        TextView tvFinish = (TextView) holder.getView(R.id.tv_finish);
        //正在下载的书标记
        int cacheState = bean.getCacheState();
        if (cacheState == Constant.DownloadState.DOWNLOADING) { //正在下载
            tvFinish.setVisibility(View.GONE);
            ivPlay.setVisibility(View.VISIBLE);
            ivPlay.setImageResource(R.drawable.pause);

        } else if (cacheState == Constant.DownloadState.DOWNLOAD_CANCEL) {  //取消下载
            tvFinish.setVisibility(View.GONE);
            ivPlay.setVisibility(View.VISIBLE);
            ivPlay.setImageResource(R.drawable.play);
            waveImageView.stopAnimation();
        } else if (cacheState == Constant.DownloadState.DOWNLOADED) { //下载完成
            tvFinish.setVisibility(View.VISIBLE);
            ivPlay.setVisibility(View.GONE);
            waveImageView.stopAnimation();
            waveImageView.setVisibility(View.GONE);
        } else if (cacheState == Constant.DownloadState.DOWNLOAD_NOT) { //下载未完成
            tvFinish.setVisibility(View.GONE);
            ivPlay.setVisibility(View.GONE);
            waveImageView.stopAnimation();
        }

        ivPlay.setOnClickListener(this);
    }

    @Override
    public void onBindView(BaseViewHolder holder, int position, List<Object> payloads) {
        ComicBookBean bean = mLsData.get(position);
        TextView tvFinish = (TextView) holder.getView(R.id.tv_finish);
        ImageView ivPlay = (ImageView) holder.getView(R.id.iv_play);
        WaveImageView waveImageView = (WaveImageView) holder.getView(R.id.wiv_view); //显示进度

        String state = (String) payloads.get(payloads.size()-1);
        int cacheState = bean.getCacheState();
        switch (state) {
            case UPDATE_ITEM_STATE:
                if (cacheState == Constant.DownloadState.DOWNLOADING) {
                    waveImageView.startAnimation();
                    ivPlay.setVisibility(View.VISIBLE);
                    ivPlay.setImageResource(R.drawable.pause);
                } else if (cacheState == Constant.DownloadState.DOWNLOAD_CANCEL) {
                    ivPlay.setVisibility(View.VISIBLE);
                    ivPlay.setImageResource(R.drawable.play);
                    waveImageView.stopAnimation();
                } else {
                    ivPlay.setVisibility(View.GONE);
                    waveImageView.stopAnimation();
                }
                break;
            case UPDATE_ITEM_PROGRESS:
                waveImageView.setProgress((1.0f - bean.getProgress()));
                if (!waveImageView.isRunning())
                    waveImageView.startAnimation();
                ivPlay.setImageResource(cacheState == Constant.DownloadState.DOWNLOADING ? R.drawable.pause : R.drawable.play);
                break;
            case UPDATE_ITEM_FINISH:
                if (cacheState == Constant.DownloadState.DOWNLOADED) {
                    tvFinish.setVisibility(View.VISIBLE);
                    ivPlay.setVisibility(View.GONE);
                    waveImageView.stopAnimation();
                } else if (cacheState == Constant.DownloadState.DOWNLOAD_NOT) {
                    ivPlay.setVisibility(View.GONE);
                    if (!waveImageView.isRunning())
                        waveImageView.stopAnimation();
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onClick(View v) {
        if (R.id.iv_play == v.getId()) {
            int position = (int) v.getTag();
            ComicBookBean bean = mLsData.get(position);
            //取消这本书的缓存任务
            if (bean.getCacheState() == Constant.DownloadState.DOWNLOADING) {
                bean.setCacheState(Constant.DownloadState.DOWNLOAD_CANCEL);
                if (mTaskListener != null) mTaskListener.stopTask(bean);
                this.notifyItemChanged(position, UPDATE_ITEM_STATE);
            } else {
                //开始这本书的缓存任务
                bean.setCacheState(Constant.DownloadState.DOWNLOADING);
                if (mTaskListener != null) mTaskListener.startTask(bean);
                this.notifyItemChanged(position, UPDATE_ITEM_STATE);
            }
        }
    }

    public int getPosition(long bookId) {
        int position = -1;
        for (int i = 0; i < mLsData.size(); i++) {
            if (mLsData.get(i).get_id() == bookId) {
                position = i;
                break;
            }
        }
        return position;
    }

    public void updateState(BaseViewHolder holder, int position, int state) {
        if (holder != null) {
            WaveImageView imageView = (WaveImageView) holder.getView(R.id.wv_view);
            if (imageView != null) {
                if (state != mLsData.get(position).getCacheState()) {
                    mLsData.get(position).setCacheState(state);
                    switch (state) {
                        case Constant.DownloadState.DOWNLOADING:
                            if (!imageView.isRunning()) imageView.startAnimation();
                            break;
                        case Constant.DownloadState.DOWNLOAD_CANCEL:
                            imageView.stopAnimation();
                            break;
                        case Constant.DownloadState.DOWNLOAD_NOT:
                            imageView.stopAnimation();
                            break;
                        case Constant.DownloadState.DOWNLOADED:
                            imageView.stopAnimation();
                            imageView.setVisibility(View.GONE);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    //更新进度
    public void updateProgress(BaseViewHolder holder, int position, long bookId, float progress,
                               int state, boolean isFinish) {
        Log.e(TAG, "updateProgress: ");
        /**
         *  章节的下载，对书本的进度有影响
         *  更新阶段：支队waveIamge进行进度的更新
         *  根据state 来对waveIamge 的显示还是隐藏 进行做处理
         *
         */
        mLsData.get(position).setProgress(progress);

        if (holder != null) {
            WaveImageView imageView = (WaveImageView) holder.getView(R.id.wv_view);
            if (imageView != null) {
                imageView.setProgress(progress);
            } else
                notifyItemChanged(position);

        } else {
            mLsData.get(position).setCacheState(state);
            notifyItemChanged(position);
        }
    }

    public interface onTaskListener {
        //开始这本书的缓存任务
        void startTask(ComicBookBean bean);

        //取消这本书的缓存任务
        void stopTask(ComicBookBean bean);
    }
}
