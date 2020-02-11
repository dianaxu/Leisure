package com.example.leisure.retrofit;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public abstract class DownloadComicObserver<T> implements Observer<T> {

    private Context mContext;

    public DownloadComicObserver(Context context) {
        this.mContext = context;
    }

    @Override
    public void onSubscribe(Disposable d) {
        //未连接网络
        if (!isConnected(mContext)) {
            //需要关闭线程 或者是服务
            if (d.isDisposed()) {
                d.dispose();
            }
        } else {
            //正在进行中
        }
    }

    @Override
    public void onNext(T t) {
        onResult(t);
    }

    @Override
    public void onError(Throwable e) {
        onFailure(e, RxExceptionUtil.exceptionHandler(e));
    }

    @Override
    public void onComplete() {

    }

    /**
     * 是否有网络连接，不管是wifi还是数据流量
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) {
            return false;
        }
        boolean available = info.isAvailable();
        return available;
    }

    public abstract void onResult(T result);

    public abstract void onFailure(Throwable e, String errorMsg);
}
