package com.example.leisure.bean;

import com.example.leisure.db.greendao.ComicImageBean;
import com.example.leisure.retrofit.BaseComicResponse;

import java.util.List;

public class ComicContentBean extends BaseComicResponse {
    public List<ComicImageBean> list;
}
