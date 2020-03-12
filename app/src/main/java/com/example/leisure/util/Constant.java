package com.example.leisure.util;

public class Constant {
    public static final String URL_USER_BASE = "https://api.apiopen.top/";
    public static final String URL_COMIC = "http://api.pingcc.cn/";

    //设置默认超时时间
    public static final int DEFAULT_TIME = 10;

//    public static final String BASE_FILE_NAME = "images";


    public class SharedPref {
        public static final String BASE_DATA_FILE_NAME = "share";
        public static final String BASE_DATA_DEV_APKID = "apkid";
        public static final String BASE_DATA_DEV_EMAIL = "devEmail";
        public static final String BASE_DATA_USER_NAME = "name";
        public static final String BASE_DATA_USER_PASSWD = "passwd";
        public static final String BASE_DATA_USER_NIKENAME = "nikeName";
        public static final String BASE_DATA_USER_HEADERIMG = "headerImg";
        public static final String BASE_DATA_USER_PHONE = "phone";
        public static final String BASE_DATA_USER_EMAIL = "email";
        public static final String BASE_DATA_USER_VIPGRADE = "vipGrade";
        public static final String BASE_DATA_USER_AUTOGRAPH = "autograph";
        public static final String BASE_DATA_USER_REMARKS = "remarks";

    }


    public class ComicBaseBundle {
        public static final String BUNDLE_HURL1 = "bundle_hurl1";
        public static final String BUNDLE_NAME = "bundle_name";
        public static final String BUNDLE_COVER = "bundle_conver";
    }

    public class DownloadState {
        public static final int DOWNLOAD_NOT = 0;   //未下载
        public static final int DOWNLOADED = 1;     //已下载
        public static final int DOWNLOADING = 2;    //正在下载
        public static final int DOWNLOAD_CANCEL = 3;//取消下载
    }

    public class DownloadReceiverState {
        public static final int PROMPT_START = 0;  //提示信息
        public static final int PROMPT_FAIL = 1;  //失败
        public static final int UPDATE = 2;  //更新
        public static final int FINISH = 3;   //完成
        public static final int CANCEL = 4;   //取消
    }

    public class SortType {
        public static final int DOWN = 0;
        public static final int UP = 1;
    }

    public class ReceiverAction {
        public static final String ACTION_DOWNLOAD = "com.example.leisure.DownloadReceiver";
    }
}
