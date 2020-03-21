package com.example.leisure.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.leisure.R;
import com.example.leisure.eventbus.Event;
import com.example.leisure.eventbus.EventBusUtil;
import com.example.leisure.util.ScreenInfoUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public abstract class BaseActivity extends AppCompatActivity {
    private int mStatusBarHeight = 1;

    public enum TransitionMode {LEFT, RIGHT, TOP, BOTTOM, SCALE, FADE}

    protected abstract TransitionMode getOverridePendingTransitionMode();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (getOverridePendingTransitionMode() != null) {

            switch (getOverridePendingTransitionMode()) {
                case LEFT:
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    break;
                case RIGHT:
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    break;
                case TOP:
                    overridePendingTransition(R.anim.top_in, R.anim.top_out);
                    break;
                case BOTTOM:
                    overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out);
                    break;
                case SCALE:
                    overridePendingTransition(R.anim.scale_in, R.anim.scale_out);
                    break;
                case FADE:
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    break;

            }
            super.onCreate(savedInstanceState);
        }
        //隐藏状态栏时，获取状态栏高度
        mStatusBarHeight = ScreenInfoUtils.getStatusBarHeight(this);

        //注册事件
        if (isRegisterEventBus()) {
            EventBusUtil.register(this);
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (getOverridePendingTransitionMode() != null) {
            switch (getOverridePendingTransitionMode()) {
                case LEFT:
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    break;
                case RIGHT:
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    break;
                case TOP:
                    overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out);
                    break;
                case BOTTOM:
                    overridePendingTransition(R.anim.top_in, R.anim.top_out);
                    break;
                case SCALE:
                    overridePendingTransition(R.anim.scale_in_disappear, R.anim.scale_out_disappear);
                    break;
                case FADE:
                    overridePendingTransition(R.anim.fade_in_disappear, R.anim.fade_out_disappear);
                    break;
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isHasStatusBar())
            initStatusBar();
    }

    protected boolean isHasStatusBar() {
        return false;
    }

    /**
     * 初始化状态栏
     */
    private void initStatusBar() {
        //通过设置全屏，设置状态栏透明
        boolean isFullScreen = ScreenInfoUtils.fullScreen(this);
        if (isFullScreen) {
            //初始化状态栏的高度
            View bar = findViewById(getStatusBarId());
            if (bar.getLayoutParams() instanceof ConstraintLayout.LayoutParams) {
                ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(MATCH_PARENT, mStatusBarHeight);
                bar.setLayoutParams(params);
            } else if (bar.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(MATCH_PARENT, mStatusBarHeight);
                bar.setLayoutParams(params);
            } else if (bar.getLayoutParams() instanceof LinearLayout.LayoutParams) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(MATCH_PARENT, mStatusBarHeight);
                bar.setLayoutParams(params);
            }
        }

    }

    protected int getStatusBarId() {
        return R.id.view_status_bar;
    }

    /**
     * 是否注册事件分发
     *
     * @return true绑定EventBus事件分发，默认不绑定，子类需要绑定的话复写此方法返回true.
     */
    protected boolean isRegisterEventBus() {
        return false;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventBusCome(Event event) {
        if (event != null) {
            receiveEvent(event);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void onStickyEventBusCome(Event event) {
        if (event != null) {
            receiveStickyEvent(event);
        }
    }

    /**
     * 接收到分发到事件
     *
     * @param event 事件
     */
    protected void receiveEvent(Event event) {

    }

    /**
     * 接受到分发的粘性事件
     *
     * @param event 粘性事件
     */
    protected void receiveStickyEvent(Event event) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isRegisterEventBus()) {
            EventBusUtil.unregister(this);
        }
    }

    protected void setViewMarginTop(View view) {
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        params.setMargins(0, mStatusBarHeight, 0, 0);
    }

    protected int getStatusBarHeight() {
        return mStatusBarHeight;
    }
}
