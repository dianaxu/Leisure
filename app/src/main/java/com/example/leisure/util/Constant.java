package com.example.leisure.util;

public class Constant {
    public static final String URL_USER_BASE = "https://api.apiopen.top/";
    public static final String URL_COMIC = "http://api.pingcc.cn/";

    //设置默认超时时间
    public static final int DEFAULT_TIME = 10;

    public static final String BASE_FILE_NAME = "leisure";


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

    public class DownloadState {
        public static final int DOWNLOAD_NOT = 0;   //未下载
        public static final int DOWNLOADED = 1;     //已下载
        public static final int DOWNLOADING = 2;    //正在下载
        public static final int DOWNLOAD_CANCEL = 3;//取消下载
    }

    public class DownloadReceiverState {
        public static final int UPDATE = 0;  //更新数据
        public static final int FINISH = 1;  //完成全部下载
        public static final int NO_FINISH_ALL = 2; //任务完成，但未全部下载
    }
}
