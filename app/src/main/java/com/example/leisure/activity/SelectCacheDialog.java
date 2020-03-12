package com.example.leisure.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.leisure.R;

import androidx.annotation.NonNull;

public class SelectCacheDialog extends Dialog implements View.OnClickListener {
    private OnSelectCacheListener mListener;
    private static Context mContext;
    private static SelectCacheDialog mDialog;

    public void addOnSelectCacheListener(OnSelectCacheListener listener) {
        this.mListener = listener;
    }

    private SelectCacheDialog(@NonNull Context context) {
        super(context, R.style.CustomDialog);
        mContext = context;
    }

    public static SelectCacheDialog getInterest(Context context) {
        if (mDialog == null || mContext == null || ((Activity) mContext).isFinishing() || mContext != context) {
            mDialog = new SelectCacheDialog(context);
        }
        if (mDialog.isShowing()) mDialog.dismiss();
        return mDialog;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_select_cache);
        findViewById(R.id.tv_all).setOnClickListener(this);
        findViewById(R.id.tv_current_pos).setOnClickListener(this);
        findViewById(R.id.tv_cancel).setOnClickListener(this);

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = mContext.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.9); // 宽度设置为屏幕宽度的80%
        //lp.dimAmount=0.0f;//外围遮罩透明度0.0f-1.0f
        dialogWindow.setAttributes(lp);
        dialogWindow.setGravity(Gravity.BOTTOM);//内围区域底部显示
        dialogWindow.setWindowAnimations(R.style.dialogWindowAnim);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.tv_all == id) {
            if (mListener != null) mListener.onDownloadAll();
            dismiss();
        } else if (R.id.tv_current_pos == id) {
            if (mListener != null) mListener.onCurrentPosition();
            dismiss();
        } else if (R.id.tv_cancel == id) {
            if (mListener != null) mListener.onCancel();
            dismiss();
        }

    }


    public static boolean isShow() {
        if (mDialog == null) return false;
        return mDialog.isShowing();
    }

    public static void changeSize() {
        if (mDialog == null) return;
        Window dialogWindow = mDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = mContext.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.9); // 宽度设置为屏幕宽度的80%
        //lp.dimAmount=0.0f;//外围遮罩透明度0.0f-1.0f
        dialogWindow.setAttributes(lp);
        dialogWindow.setGravity(Gravity.BOTTOM);//内围区域底部显示
        dialogWindow.setWindowAnimations(R.style.dialogWindowAnim);
    }

    public interface OnSelectCacheListener {
        void onDownloadAll();

        void onCurrentPosition();

        void onCancel();
    }
}
