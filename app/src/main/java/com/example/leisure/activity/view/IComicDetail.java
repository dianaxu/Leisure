package com.example.leisure.activity.view;

import com.example.leisure.db.greendao.ComicBookBean;

public interface IComicDetail {
    void updateUI(String bookName, String bookCover);

    void updateUI(ComicBookBean bean);

    void onFailure(String errorMsg);

    void onAddComicToDBSuccess();

    void onRemoveComicToDBSuccess();
}
