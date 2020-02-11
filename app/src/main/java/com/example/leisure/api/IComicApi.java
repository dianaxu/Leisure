package com.example.leisure.api;

import com.example.leisure.bean.ComicContentBean;
import com.example.leisure.bean.ComicItemBean;
import com.example.leisure.bean.ComicListBean;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

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
     * 获取漫画详情 其中包括了章节
     *
     * @return
     */
    @GET("?")
    Observable<ComicItemBean> getComicItem(@Query("mhurl1") String mhurl1);

    /**
     * 章节下的图片链接集
     *
     * @param mhurl2
     * @return
     */
    @GET("?")
    Observable<ComicContentBean> getComicContent(@Query("mhurl2") String mhurl2);

    /**
     * 搜索漫画
     *
     * @param mhname
     * @return
     */
    @GET("?")
    Observable<ComicListBean> searchComic(@Query("mhname") String mhname);

    /**
     * 图片下载
     *
     * @param fileUrl
     * @return
     */
    @GET
    @Streaming
    Observable<ResponseBody> downloadPicFromNet(@Url String fileUrl);


}

