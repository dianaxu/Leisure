package com.example.leisure.widget.textWatcher;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

public class JumpTextWatcher implements TextWatcher {
    private EditText mThisView;
    private View mNextView;

    //默认设置的是手机长度
    private int mMaxLenght = 11;
    //默认设置的是自动跳转
    private boolean mIsAutoJump = true;

    public JumpTextWatcher(EditText view, View nextView) {
        this.mThisView = view;
        this.mNextView = nextView;
    }

    public void setIsAutoJump(boolean isAutoJump) {
        this.mIsAutoJump = isAutoJump;
    }

    public void setAutoJumpLenght(int lenght) {
        this.mMaxLenght = lenght;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        //位数到达手机号  自动跳转到下一个
        //如果按回车键或者是换行符  自动跳转到下一个
        String str = s.toString();
        //发现输入回车符或换行符
        if (str.indexOf("\r") >= 0 || str.indexOf("\n") >= 0) {
            mThisView.setText(str.replace("\r", "").replace("\n", ""));
            jumpToNextControl();
        } else if (str.length() == mMaxLenght) {
            jumpToNextControl();
        }
    }

    private void jumpToNextControl() {
        if (!mIsAutoJump) return;

        //跳转到下一个控件
        if (mNextView != null) {
            mNextView.requestFocus();
            if (mNextView instanceof EditText) {
                EditText et = (EditText) mNextView;
                et.setSelection(et.getText().length());
            }
        }
    }
}
