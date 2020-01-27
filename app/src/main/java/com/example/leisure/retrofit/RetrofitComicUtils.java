package com.example.leisure.retrofit;


import com.example.leisure.api.IComicApi;
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
public class RetrofitComicUtils {
    private static final String TAG = "RetrofitUtils";
    private static IComicApi mIComicApi;


    /**
     * 单例模式
     */
    public static IComicApi getApiUrl() {
        if (mIComicApi == null) {
            synchronized (RetrofitComicUtils.class) {
                if (mIComicApi == null) {
                    mIComicApi = new RetrofitComicUtils().getRetrofit();
                }
            }
        }
        return mIComicApi;
    }

    private RetrofitComicUtils() {
    }

    public IComicApi getRetrofit() {
        // 初始化Retrofit
        IComicApi apiUrl = initRetrofit(initOkHttp()).create(IComicApi.class);
        return apiUrl;
    }

    /**
     * 初始化Retrofit
     */
    @NonNull
    private Retrofit initRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .client(client)
                .baseUrl(Constant.URL_COMIC)
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

