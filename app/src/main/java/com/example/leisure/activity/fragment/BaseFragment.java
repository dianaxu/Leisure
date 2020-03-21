package com.example.leisure.activity.fragment;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public abstract class BaseFragment extends Fragment {
    private int mStatusBarHeight = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏状态栏时，获取状态栏高度
        mStatusBarHeight = ScreenInfoUtils.getStatusBarHeight(getContext());

        if (isRegisterEventBus()) {
            EventBusUtil.register(this);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isHintStatusBar())
            initStatusBar(view);
    }

    /**
     * 初始化状态栏
     */
    private void initStatusBar(View view) {
        //通过设置全屏，设置状态栏透明
        boolean isFullScreen = ScreenInfoUtils.fullScreen(getActivity());
        if (isFullScreen) {
            //初始化状态栏的高度
            View bar = view.findViewById(getStatusBarId());
            if (bar == null) return;
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

    protected boolean isHintStatusBar() {
        return false;
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
}
