package com.example.leisure.util;

import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.util.regex.Pattern;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class Util {
    public static final String TOUCHLOG = "TouchDemo";
    public static final String WECHATLOG = "WechatDemo";

    public static final String PACKAGE = "com.example.androidtest";

    /**
     * 显示键盘
     *
     * @param et 输入焦点
     */
    public static void showInput(final EditText et, Context context) {
        et.requestFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(et, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * 隐藏键盘
     */
    public static void hideInput(Context context, Window window) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
        View v = window.peekDecorView();
        if (null != v) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }


    public static boolean isPhoneNumber(String phoneNumber) {
        String regex = "(1[0-9][0-9]|15[0-9]|18[0-9])\\d{8}";
        Pattern p = Pattern.compile(regex);
        return p.matcher(phoneNumber).matches();
    }

    /**
     * 创建验证码
     */
    public static String createCode4() {
        return String.valueOf((int) ((Math.random() * 9 + 1) * 1000));
    }

    public static String getResourceString(Context context, int resId) {
        return context.getResources().getString(resId);
    }

}
