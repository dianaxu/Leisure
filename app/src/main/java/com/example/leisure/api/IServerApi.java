package com.example.leisure.api;

import com.example.leisure.bean.DeveloperBean;
import com.example.leisure.bean.FeedbackBean;
import com.example.leisure.bean.JokeBean;
import com.example.leisure.bean.MusicBroadcastingBean;
import com.example.leisure.bean.MusicBroadcastingDetailsBean;
import com.example.leisure.bean.MusicDetailsBean;
import com.example.leisure.bean.UserInfoBean;
import com.example.leisure.bean.WangYiNewsBean;
import com.example.leisure.retrofit.BaseResponse;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IServerApi {


    /**
     * 开发者注册
     *
     * @param name
     * @param passwd
     * @param email
     * @return
     */
    @POST("developerRegister")
    Observable<BaseResponse<DeveloperBean>> developerRegister(@Query("name") String name, @Query("passwd") String passwd, @Query("email") String email);

    /**
     * 开发者登录
     *
     * @param name
     * @param passwd
     * @return
     */
    @POST("developerLogin")
    Observable<BaseResponse<DeveloperBean>> developerLogin(@Query("name") String name, @Query("passwd") String passwd);

    /**
     * 开发者- 查看反馈
     *
     * @param apikey
     * @param page
     * @param count
     * @return
     */
    @POST("getFeedback")
    Observable<BaseResponse<List<FeedbackBean>>> getFeedback(@Query("apikey") String apikey, @Query("page") String page
            , @Query("count") String count);

    /**
     * 开发者- 删除反馈
     *
     * @param apikey
     * @param id
     * @return
     */
    @POST("deleteFeedback")
    Observable deleteFeedback(@Query("apikey") String apikey, @Query("id") String id);


    /**
     * 用户注册
     *
     * @param apikey
     * @param name
     * @param passwd
     * @param nikeName
     * @param headerImg
     * @param phone
     * @param email
     * @param vipGrade
     * @param autograph
     * @param remarks
     * @return
     */
    @POST("registerUser")
    Observable<BaseResponse<UserInfoBean>> registerUser(@Query("apikey") String apikey, @Query("name") String name
            , @Query("passwd") String passwd, @Query("nikeName") String nikeName, @Query("headerImg") String headerImg
            , @Query("phone") String phone, @Query("email") String email, @Query("vipGrade") String vipGrade
            , @Query("autograph") String autograph, @Query("remarks") String remarks);

    /**
     * 用户登录
     *
     * @param apikey
     * @param name
     * @param passwd
     * @return
     */
    @POST("loginUser")
    Observable<BaseResponse<UserInfoBean>> loginUser(@Query("apikey") String apikey, @Query("name") String name, @Query("passwd") String passwd);


    /**
     * 用户更新
     *
     * @param apikey
     * @param name
     * @param passwd
     * @param nikeName
     * @param headerImg
     * @param phone
     * @param email
     * @param vipGrade
     * @param autograph
     * @param remarks
     * @return
     */
    @POST("updateUserInfo")
    Observable<BaseResponse<UserInfoBean>> updateUserInfo(@Query("apikey") String apikey, @Query("name") String name
            , @Query("passwd") String passwd, @Query("nikeName") String nikeName, @Query("headerImg") String headerImg
            , @Query("phone") String phone, @Query("email") String email, @Query("vipGrade") String vipGrade
            , @Query("autograph") String autograph, @Query("remarks") String remarks);

    /**
     * 用户反馈
     *
     * @param apikey
     * @param text
     * @param email
     * @return
     */
    @POST("userFeedback")
    Observable<BaseResponse<UserInfoBean>> userFeedback(@Query("apikey") String apikey, @Query("text") String text
            , @Query("email") String email);

    // https://api.apiopen.top/getJoke?page=1&count=2&type=video

    /**
     * 新实时段子
     *
     * @return
     */
    @POST("getJoke")
    Observable<BaseResponse<List<JokeBean>>> getJoke(@Query("page") int page, @Query("count") int count
            , @Query("type") String type);


    //https://api.apiopen.top/getWangYiNews

    /**
     * 网易新闻
     *
     * @return
     */
    @POST("getWangYiNews")
    Observable<BaseResponse<List<WangYiNewsBean>>> getWangYiNews(@Query("page") int page, @Query("count") int count);

//    通过Id查段子
//    https://api.apiopen.top/getSingleJoke?sid=28654780

    @POST("getSingleJoke")
    Observable<BaseResponse<JokeBean>> getSingleJoke(@Query("sid") String sid);

    //    音乐电台接口：
//    https://api.apiopen.top/musicBroadcasting
    @POST("musicBroadcasting")
    Observable<BaseResponse<List<MusicBroadcastingBean>>> musicBroadcasting();

    //    音乐电台详情接口：
//    https://api.apiopen.top/musicBroadcastingDetails?channelname=public_tuijian_spring
    @POST("musicBroadcastingDetails")
    Observable<BaseResponse<MusicBroadcastingDetailsBean>> musicBroadcastingDetails(@Query("channelname") String channelname);

    //
//    音乐详情接口：
//    https://api.apiopen.top/musicDetails?id=435225
    @POST("musicDetails")
    Observable<BaseResponse<MusicDetailsBean>> musicDetails(@Query("id") String id);

    //    音乐排行榜接口：
//    https://api.apiopen.top/musicRankings
    @POST("musicRankings")
    Observable<BaseResponse<JokeBean>> musicRankings(@Query("sid") String sid);

    //    音乐排行榜详情接口：
//    https://api.apiopen.top/musicRankingsDetails?type=1
    @POST("musicRankingsDetails")
    Observable<BaseResponse<JokeBean>> musicRankingsDetails(@Query("sid") String sid);

    //    每日视频推荐接口：
//    https://api.apiopen.top/todayVideo
    @POST("todayVideo")
    Observable<BaseResponse<JokeBean>> todayVideo(@Query("sid") String sid);

    //    视频大纲获取接口：
//    https://api.apiopen.top/videoHomeTab
    @POST("videoHomeTab")
    Observable<BaseResponse<JokeBean>> videoHomeTab(@Query("sid") String sid);

    //    视频分类推荐接口：
//    https://api.apiopen.top/videoCategory
    @POST("videoCategory")
    Observable<BaseResponse<JokeBean>> videoCategory(@Query("sid") String sid);

    //    视频分类推荐接口：0
//    https://api.apiopen.top/videoCategoryDetails?id=14
    @POST("videoCategoryDetails")
    Observable<BaseResponse<JokeBean>> videoCategoryDetails(@Query("sid") String sid);

    //    根据ID推荐接口：
//    https://api.apiopen.top/videoRecommend?id=127398
    @POST("videoRecommend")
    Observable<BaseResponse<JokeBean>> videoRecommend(@Query("sid") String sid);

}

