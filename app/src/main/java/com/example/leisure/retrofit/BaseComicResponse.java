package com.example.leisure.retrofit;

import org.greenrobot.greendao.annotation.Transient;

/**
 * 统一响应
 */
public class BaseComicResponse {
    @Transient
    public String code;
    @Transient
    public String message;
}
