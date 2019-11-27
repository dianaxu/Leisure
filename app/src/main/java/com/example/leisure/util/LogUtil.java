package com.example.leisure.util;

import android.util.Log;

import com.example.leisure.BuildConfig;

public class LogUtil {

    private static final boolean isDebug = BuildConfig.DEBUG;

    public static void d(String tag, String content) {
        if (!isDebug) return;
        Log.d(tag, content);
    }

    public static void i(String tag, String content) {
        if (!isDebug) return;
        Log.d(tag, content);
    }

    public static void v(String tag, String content) {
        if (!isDebug) return;
        Log.d(tag, content);
    }


    public static void w(String tag, String content) {
        if (!isDebug) return;
        Log.d(tag, content);
    }

    public static void w(String content) {
        if (!isDebug) return;
        Log.d("TAG", content);
    }

    public static void d(String content) {
        if (!isDebug) return;
        Log.d("TAG", content);
    }

    public static void i(String content) {
        if (!isDebug) return;
        Log.d("TAG", content);
    }

    public static void e(String tag, String content) {
        if (!isDebug) return;
        Log.e(tag, content);
    }

    public static void e(String tag, String content, Exception e) {
        if (!isDebug) return;
        Log.e(tag, content + e);
    }
}
