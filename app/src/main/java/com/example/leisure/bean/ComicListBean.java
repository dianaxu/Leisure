package com.example.leisure.bean;

import com.example.leisure.retrofit.BaseComicResponse;

import java.util.List;

public class ComicListBean extends BaseComicResponse {

    /**
     * name : 绝世古尊
     * url : mh123/comic/26620.html
     * cover : https://img.detatu.com/upload/vod/2019-09-17/15686950561.jpg
     * time : 2020-01-05
     * latest : 第47话大..大蒜！
     */

    public List<ListBean> list;

    public static class ListBean {
        public String name;
        public String url;
        public String cover;
        public String time;
        public String latest;
        public String author;
        public String status;


        /**
         * number : 8
         * pages : 2
         * dpages : 9
         */
        public String number;
        public String pages;
        public String dpages;

    }

}
