package com.example.leisure.bean;

import com.example.leisure.retrofit.BaseComicResponse;

import java.util.List;

public class ComicContentBean extends BaseComicResponse {
    public List<ListBean> list;

    public static class ListBean {
        public String img;
    }
}
