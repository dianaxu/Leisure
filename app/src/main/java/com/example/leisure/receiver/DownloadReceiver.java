package com.example.leisure.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.leisure.util.Constant;

import java.io.Serializable;

/**
 * 下载漫画书广播
 */
public class DownloadReceiver extends BroadcastReceiver {
    //    public static final String EXTRA_RECEIVER_STATE = "extra_receiver_state";
//    public static final String EXTRA_TOTAL_COUNT = "extra_total_count";
//    public static final String EXTRA_MAX_COUNT = "extra_max_count";
//    public static final String EXTRA_CHAPTER_ID = "extra_chapter_id";
//    public static final String EXTRA_PROGRESS = "extra_progress";
//    public static final String EXTRA_BOOK_ID = "extra_book_id";
    public static final String EXTRA_RECEIVER_BEAN = "extra_receiver_bean";
    public static final String EXTRA_RECEIVER_CANCEL_BEAN = "extra_receiver_cancel_bean";
    public static final String EXTRA_RECEIVER_STATE = "extra_receiver_state";
    public static final String EXTRA_RECEIVER_PROMPT = "extra_receiver_prompt";

    public static final int PROMPT_MESSAGE_START = 0;
    public static final int PROMPT_MESSAGE_FAIL = 1;

    private static final String TAG = "DownloadReceiver";
    private onUpdateUIListener mListener;
    private onCancelOrAddListener mCancelListener;

    public interface onUpdateUIListener {
        void updateUI(ReceiverBean bean);

        void promptStart(ReceiverPromptBean bean);

        void promptFail(ReceiverPromptBean bean);

        void finishDown(ReceiverBean bean);
    }

    public interface onCancelOrAddListener {
        void cancelOrAddTask(ReceiverCancelBean cancelBean);
    }


    public DownloadReceiver(onUpdateUIListener listener) {
        this.mListener = listener;
    }

    public void addOnCancelListener(onCancelOrAddListener listener) {
        this.mCancelListener = listener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mListener != null) {

            int state = intent.getIntExtra(EXTRA_RECEIVER_STATE, 0);
            switch (state) {
                case Constant.DownloadReceiverState.PROMPT_FAIL:
                    ReceiverPromptBean prompt = (ReceiverPromptBean) intent.getSerializableExtra(EXTRA_RECEIVER_PROMPT);
                    mListener.promptFail(prompt);
                    break;
                case Constant.DownloadReceiverState.UPDATE:
                    ReceiverBean bean = (ReceiverBean) intent.getSerializableExtra(EXTRA_RECEIVER_BEAN);
                    mListener.updateUI(bean);
                    break;
                case Constant.DownloadReceiverState.PROMPT_START:
                    ReceiverPromptBean promptStart = (ReceiverPromptBean) intent.getSerializableExtra(EXTRA_RECEIVER_PROMPT);
                    mListener.promptStart(promptStart);
                    break;
                case Constant.DownloadReceiverState.FINISH:
                    ReceiverBean finishBean = (ReceiverBean) intent.getSerializableExtra(EXTRA_RECEIVER_BEAN);
                    mListener.finishDown(finishBean);
                    break;
                case Constant.DownloadReceiverState.CANCEL:
                    ReceiverCancelBean cancelBean = (ReceiverCancelBean) intent.getSerializableExtra(EXTRA_RECEIVER_CANCEL_BEAN);
                    if (mCancelListener != null) mCancelListener.cancelOrAddTask(cancelBean);

                default:
                    break;
            }

        }
    }


    public static class ReceiverBean implements Serializable {
        public int totalCount;
        public int maxCount;
        public long chapterId;
        public int receiverState;
        public long bookId;
        public float bookProgress;
        public int bookCacheState;
        public int chapterCacheState;
        public String message;
    }

    public static class ReceiverPromptBean implements Serializable {
        public String message;
        public long chapterId;
    }

    public static class ReceiverCancelBean implements Serializable {
        public long bookId;
        public float bookProgress;
        public int bookState;
    }
}
