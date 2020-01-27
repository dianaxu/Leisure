package com.example.leisure.retrofit;



import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * 数据返回统一处理  参考https://www.jianshu.com/p/ff619fea7e22
 *
 */
public abstract class BaseComicObserver<T extends BaseComicResponse> implements Observer<T> {
    @Override
    public void onNext(T response) {
        //在这边对 基础数据 进行统一处理  举个例子：
        if (response.code.equals("0")) {
            onSuccess(response);
        } else {
            onFailure(null, response.message);
        }
    }

    @Override
    public void onError(Throwable e) {//服务器错误信息处理
        onFailure(e, RxExceptionUtil.exceptionHandler(e));
    }

    @Override
    public void onComplete() {

    }

    @Override
    public void onSubscribe(Disposable d) {
    }

    public abstract void onSuccess(T result);

    public abstract void onFailure(Throwable e, String errorMsg);

}
