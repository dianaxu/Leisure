package com.example.leisure.activity;

import android.os.Bundle;
import android.view.View;

import com.example.leisure.util.ScreenInfoUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class BaseActivity extends AppCompatActivity {
    private int mStatusBarHeight = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏状态栏时，获取状态栏高度
        mStatusBarHeight = ScreenInfoUtils.getStatusBarHeight(this);
        //通过设置全屏，设置状态栏透明
        ScreenInfoUtils.fullScreen(this);
    }


    protected void setViewMarginTop(View view) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        params.setMargins(0, mStatusBarHeight, 0, 0);
    }

    protected int getStatusBarHeight() {
        return mStatusBarHeight;
    }
}
