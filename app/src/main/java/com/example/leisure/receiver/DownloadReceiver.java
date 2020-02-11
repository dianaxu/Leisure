package com.example.leisure.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.leisure.util.Constant;

public class DownloadReceiver extends BroadcastReceiver {
    public static final String EXTRA_RECEIVER_STATE = "extra_receiver_state";
    public static final String EXTRA_TOTAL_COUNT = "extra_total_count";
    public static final String EXTRA_MAX_COUNT = "extra_max_count";
    public static final String EXTRA_CHAPTER_ID = "extra_chapter_id";
    private static final String TAG = "DownloadReceiver";
    private onUpdateUIListener mListener;

    public interface onUpdateUIListener {
        void updateUI(int receiverState, int totalCount, int maxCount, long chapterId);
    }

    public DownloadReceiver(onUpdateUIListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mListener != null) {
            int totalCount = intent.getIntExtra(EXTRA_TOTAL_COUNT, 0);
            int maxCount = intent.getIntExtra(EXTRA_MAX_COUNT, 0);
            long chapterId = intent.getLongExtra(EXTRA_CHAPTER_ID, 0);
            int state = intent.getIntExtra(EXTRA_RECEIVER_STATE, Constant.DownloadReceiverState.UPDATE);
            mListener.updateUI(state, totalCount, maxCount, chapterId);
        }
    }
}
