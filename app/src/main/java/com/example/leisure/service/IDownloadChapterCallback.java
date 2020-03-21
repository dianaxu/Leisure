package com.example.leisure.service;

public interface IDownloadChapterCallback extends IDownloadCallback {
    void onChapterProgress(long chapterId, int cacheCount, int maxCount);

    void onChapterFinish(long chapterId, int status);

    void onFailure(long chapterId, String msg);

    void onSuccessCancel(long chapterId);

}
