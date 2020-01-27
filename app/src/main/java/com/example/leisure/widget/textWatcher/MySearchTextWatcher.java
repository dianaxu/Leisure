package com.example.leisure.widget.textWatcher;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

public class MySearchTextWatcher implements TextWatcher {
    private OnInputCompleteListener mListener;
    private OnEmptyListener mEmptyListener;

    public MySearchTextWatcher() {
    }

    public MySearchTextWatcher(OnInputCompleteListener listener) {
        addOnInputCompleteListener(listener);
    }

    public void addOnInputCompleteListener(OnInputCompleteListener listener) {
        this.mListener = listener;
    }

    public void addOnEmptyListener(OnEmptyListener listener) {
        this.mEmptyListener = listener;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String str = s.toString();
        //发现输入回车符或换行符
        if (str.indexOf("\r") >= 0 || str.indexOf("\n") >= 0) {
            //关闭软键盘
            //完成输入
            if (mListener != null) {
                mListener.onInputComplete(str.replace("\r", "").replace("\n", ""));
            }
        }
        if (TextUtils.isEmpty(str)) {
            if (mEmptyListener != null) {
                mEmptyListener.onEmpty();
            }
        }

    }

    public interface OnInputCompleteListener {
        void onInputComplete(String value);
    }

    public interface OnEmptyListener {
        void onEmpty();
    }
}
