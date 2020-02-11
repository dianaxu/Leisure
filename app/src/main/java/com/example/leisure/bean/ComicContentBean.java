package com.example.leisure.bean;

import com.example.leisure.retrofit.BaseComicResponse;

import java.io.Serializable;
import java.util.List;

public class ComicContentBean extends BaseComicResponse {
    public List<ListBean> list;

    public static class ListBean implements Serializable {
        public String img;
    }
}
