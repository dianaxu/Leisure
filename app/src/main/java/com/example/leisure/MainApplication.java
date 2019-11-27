package com.example.leisure;

import android.app.Application;
import android.graphics.Bitmap;

import java.util.HashMap;

public class MainApplication extends Application {
    private static MainApplication mApp;

    // 声明一个公共的信息映射对象，可当作全局变量使用
    public HashMap<String, String> mInfoMap = new HashMap<>();

    // 声明一个公共的图标映射对象，
    public HashMap<Long, Bitmap> mIconMap = new HashMap<Long, Bitmap>();

    public static MainApplication getInstance() {
        return mApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
    }

}
