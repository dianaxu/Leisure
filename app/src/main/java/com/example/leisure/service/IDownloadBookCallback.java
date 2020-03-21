package com.example.leisure.service;

public interface IDownloadBookCallback extends IDownloadCallback {
    void onUpdateProgressBook(long bookId, float progress);

    void onFinishBook(long bookId, float progress, int state);

    void onUpdateBookState(long bookId, int state);

}
