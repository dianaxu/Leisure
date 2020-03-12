package com.example.leisure;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.IBinder;

import com.danikula.videocache.HttpProxyCacheServer;
import com.example.leisure.db.greendao.DaoManager;
import com.example.leisure.greenDao.gen.DaoSession;
import com.example.leisure.retrofit.RetrofitComicUtils;
import com.example.leisure.retrofit.RetrofitUtils;
import com.example.leisure.service.DownloadService;
import com.example.leisure.util.Constant;

import java.io.File;
import java.util.HashMap;

public class MainApplication extends Application {
    private static final String TAG = "DownloadTask";
    private static MainApplication mApp;

    // 声明一个公共的信息映射对象，可当作全局变量使用
    public HashMap<String, Object> mInfoMap = new HashMap<>();

    // 声明一个公共的图标映射对象，
    public HashMap<Long, Bitmap> mIconMap = new HashMap<Long, Bitmap>();

    public SharedPreferences mSharedPref;

    private DaoSession mDaoSession;
    private DownloadService mDownloadService;
    private ServiceConnection mDownloadConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isCon = true;
            DownloadService.DownloadBinder iBinder = (DownloadService.DownloadBinder) service;
            mDownloadService = iBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isCon = false;
        }
    };


    public static MainApplication getInstance() {
        return mApp;
    }

    private boolean isCon = false;

    @Override
    public void onCreate() {
        initBaseApi();
        initGreenDao();
        getBaseDataBySharedPref();

        Intent intent = new Intent(this, DownloadService.class);
        bindService(intent, mDownloadConn, BIND_AUTO_CREATE);
        super.onCreate();
        mApp = this;
    }

    private void getBaseDataBySharedPref() {
        mSharedPref = getSharedPreferences(Constant.SharedPref.BASE_DATA_FILE_NAME, MODE_PRIVATE);
        String apkid = mSharedPref.getString(Constant.SharedPref.BASE_DATA_DEV_APKID, "");
        String devEmail = mSharedPref.getString(Constant.SharedPref.BASE_DATA_DEV_EMAIL, "");
        String userName = mSharedPref.getString(Constant.SharedPref.BASE_DATA_USER_NAME, "");
        String userPasswd = mSharedPref.getString(Constant.SharedPref.BASE_DATA_USER_PASSWD, "");
        String userPhone = mSharedPref.getString(Constant.SharedPref.BASE_DATA_USER_PHONE, "");
        String userHeaderImg = mSharedPref.getString(Constant.SharedPref.BASE_DATA_USER_HEADERIMG, "");
        String userNikeName = mSharedPref.getString(Constant.SharedPref.BASE_DATA_USER_NIKENAME, "");
        String userRemarks = mSharedPref.getString(Constant.SharedPref.BASE_DATA_USER_REMARKS, "");
        String userAutograph = mSharedPref.getString(Constant.SharedPref.BASE_DATA_USER_AUTOGRAPH, "");
        String userVipgrade = mSharedPref.getString(Constant.SharedPref.BASE_DATA_USER_VIPGRADE, "");
        String userEmail = mSharedPref.getString(Constant.SharedPref.BASE_DATA_USER_EMAIL, "");
        saveInfo(Constant.SharedPref.BASE_DATA_DEV_APKID, apkid);
        saveInfo(Constant.SharedPref.BASE_DATA_DEV_EMAIL, devEmail);
        saveInfo(Constant.SharedPref.BASE_DATA_USER_NAME, userName);
        saveInfo(Constant.SharedPref.BASE_DATA_USER_PASSWD, userPasswd);
        saveInfo(Constant.SharedPref.BASE_DATA_USER_PHONE, userPhone);
        saveInfo(Constant.SharedPref.BASE_DATA_USER_HEADERIMG, userHeaderImg);
        saveInfo(Constant.SharedPref.BASE_DATA_USER_NIKENAME, userNikeName);
        saveInfo(Constant.SharedPref.BASE_DATA_USER_REMARKS, userRemarks);
        saveInfo(Constant.SharedPref.BASE_DATA_USER_AUTOGRAPH, userAutograph);
        saveInfo(Constant.SharedPref.BASE_DATA_USER_VIPGRADE, userVipgrade);
        saveInfo(Constant.SharedPref.BASE_DATA_USER_EMAIL, userEmail);
    }

    private void initBaseApi() {
        RetrofitUtils.getApiUrl();
        RetrofitComicUtils.getApiUrl();
    }

    /**
     * //     * 初始化GreenDao,直接在Application中进行初始化操作
     * //
     */
    private void initGreenDao() {
        DaoManager mDaoManager = DaoManager.getInstance(this);
        mDaoSession = mDaoManager.getDaoSession();
    }


    public void saveInfo(String key, Object value) {
        mInfoMap.put(key, value);
    }

    public <T> T getInfo(String key) {
        return (T) mInfoMap.get(key);
    }

    private HttpProxyCacheServer proxy;


    public static HttpProxyCacheServer getProxy(Context context) {
        MainApplication app = (MainApplication) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer.Builder(this)
                .cacheDirectory(getVideoCacheDir(this))
                .build();
    }

    public static File getVideoCacheDir(Context context) {
        return new File(context.getExternalCacheDir(), "video-cache");
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public DownloadService getDownloadService() {
        if (isCon) {
            return mDownloadService;
        }
        return null;
    }

    public boolean hasConnService() {
        return isCon;
    }
}
