package com.example.leisure.api;

import com.example.leisure.bean.ComicContentBean;
import com.example.leisure.bean.ComicItemBean;
import com.example.leisure.bean.ComicListBean;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IComicApi {

    // http://api.pingcc.cn/?mhlb=shaonianrexue-2

    /**
     * 获取漫画分类详情
     *
     * @param mhlb
     * @return
     */
    @GET("?")
    Observable<ComicListBean> getComicList(@Query("mhlb") String mhlb);

    /**
     * 获取漫画详情
     *
     * @return
     */
    @GET("?")
    Observable<ComicItemBean> getComicItem(@Query("mhurl1") String mhurl1);

    /**
     * 获取漫画内容
     *
     * @param mhurl2
     * @return
     */
    @GET("?")
    Observable<ComicContentBean> getComicContent(@Query("mhurl2") String mhurl2);

    @GET("?")
    Observable<ComicListBean> searchComic(@Query("mhname") String mhname);


}

