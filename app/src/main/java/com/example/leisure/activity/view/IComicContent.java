package com.example.leisure.activity.view;

import com.example.leisure.db.greendao.ComicChapterBean;

import java.util.List;

public interface IComicContent {
//    void updateUI(List<ComicChapterBean> result);

    void updateUI(int groupPosition, List<ComicChapterBean> result, boolean isRefreshAll,boolean isJump);

    void onFailure(String errorMsg);

    void addComicSuccessToDB(long bookId);
}
