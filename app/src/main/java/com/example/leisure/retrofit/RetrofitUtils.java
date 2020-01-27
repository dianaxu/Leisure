package com.example.leisure.retrofit;


import com.example.leisure.api.IServerApi;
import com.example.leisure.util.Constant;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import io.reactivex.annotations.NonNull;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit封装
 */
public class RetrofitUtils {
    private static final String TAG = "RetrofitUtils";
    private static IServerApi mIServerApi;


    /**
     * 单例模式
     */
    public static IServerApi getApiUrl() {
        if (mIServerApi == null) {
            synchronized (RetrofitUtils.class) {
                if (mIServerApi == null) {
                    mIServerApi = new RetrofitUtils().getRetrofit();
                }
            }
        }
        return mIServerApi;
    }

    private RetrofitUtils() {
    }

    public IServerApi getRetrofit() {
        // 初始化Retrofit
        IServerApi apiUrl = initRetrofit(initOkHttp()).create(IServerApi.class);
        return apiUrl;
    }

    /**
     * 初始化Retrofit
     */
    @NonNull
    private Retrofit initRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .client(client)
                .baseUrl(Constant.URL_USER_BASE)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * 初始化okhttp
     */
    @NonNull
    private OkHttpClient initOkHttp() {
        return new OkHttpClient().newBuilder()
                .readTimeout(Constant.DEFAULT_TIME, TimeUnit.SECONDS)//设置读取超时时间
                .connectTimeout(Constant.DEFAULT_TIME, TimeUnit.SECONDS)//设置请求超时时间
                .writeTimeout(Constant.DEFAULT_TIME, TimeUnit.SECONDS)//设置写入超时时间
                .addInterceptor(new LogInterceptor())//添加打印拦截器
                .retryOnConnectionFailure(true)//设置出现错误进行重新连接。
                .build();
    }
}

