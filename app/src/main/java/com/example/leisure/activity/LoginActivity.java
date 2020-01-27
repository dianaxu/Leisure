package com.example.leisure.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leisure.MainApplication;
import com.example.leisure.R;
import com.example.leisure.bean.DeveloperBean;
import com.example.leisure.bean.UserInfoBean;
import com.example.leisure.retrofit.MyObserver;
import com.example.leisure.retrofit.RetrofitUtils;
import com.example.leisure.retrofit.RxHelper;
import com.example.leisure.util.Constant;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

/**
 * 登录界面  需要考虑用户及开发者两种登录 提供选项
 * 1.用户选项 0:用户  1：开发者
 * 2.用户登录，记录到数据到共享参数中，为下次直接进入主界面
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    private static final int DEVELOP_MODE = 1;
    public static final int REQUEST_DEVELOP_USER_TO_LOGON = 40;
    public static final int REQUEST_USER_TO_LOGON = 50;


    private String[] mUserOptions = {"用户", "开发者"};


    private Spinner mSpnUserStyle;
    private EditText mEtName, mEtPwd;
    private TextView mTvLogin, mTvForgetPwd, mTvLogon;

    private String mApkid;
    private String mName, mPwd;

    private MyObserver mObserver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        intoJumpMainActivity();

        mSpnUserStyle = findViewById(R.id.spn_user_Style);
        mEtName = findViewById(R.id.et_name);
        mEtPwd = findViewById(R.id.et_pwd);
        mTvLogin = findViewById(R.id.tv_login);
        mTvForgetPwd = findViewById(R.id.tv_forget_pwd);
        mTvLogon = findViewById(R.id.tv_logon);

        mTvLogin.setOnClickListener(this);
        mTvForgetPwd.setOnClickListener(this);
        mTvLogon.setOnClickListener(this);

        initUserStyle();

    }

    private void intoJumpMainActivity() {
        mApkid = MainApplication.getInstance().getInfo(Constant.SharedPref.BASE_DATA_DEV_APKID);
        mName = MainApplication.getInstance().getInfo(Constant.SharedPref.BASE_DATA_USER_NAME);
        mPwd = MainApplication.getInstance().getInfo(Constant.SharedPref.BASE_DATA_USER_PASSWD);
        if (TextUtils.isEmpty(mApkid) || TextUtils.isEmpty(mName) || TextUtils.isEmpty(mPwd)) {
            return;
        }
        jumpToMainActivity();
    }

    private void initUserStyle() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.spinner_checked_text, mUserOptions);
        adapter.setDropDownViewResource(R.layout.spinner_item_layout);
        mSpnUserStyle.setAdapter(adapter);
        mSpnUserStyle.setSelection(0);
        setViewMarginTop(mSpnUserStyle);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onClick(View v) {
        int id = v.getId();
        //登录
        if (R.id.tv_login == id) {
            // 验证用户名密码
            if (!checkoutInfoOk()) {
                return;
            }

            //用户登录 验证用户名及密码
            if (mSpnUserStyle.getSelectedItemPosition() == 0) {
                ;
                if (TextUtils.isEmpty(mApkid)) {
                    Toast.makeText(this, "请先注册开发者", Toast.LENGTH_SHORT).show();
                    return;
                }
                loginUser(mApkid);
            } else {
                developerLogin();
            }

        }
        //注册
        else if (R.id.tv_logon == id) {
            if (mSpnUserStyle.getSelectedItemPosition() == DEVELOP_MODE)
                jumpToLogonDevelopActivity();
            else
                jumpToLogonActivity();
        }
        //忘记密码
        else if (R.id.tv_forget_pwd == id) {
            jumpToFindPwdActivity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case REQUEST_DEVELOP_USER_TO_LOGON:
                    mApkid = data.getStringExtra(LogonDevelopActivity.EXTRA_APKID);
                    break;
                case REQUEST_USER_TO_LOGON:
                    UserInfoBean userBean = (UserInfoBean) data.getSerializableExtra(LogonActivity.EXTRA_USERINFOBEAN);
                    mEtName.setText(userBean.name);
                    mEtPwd.setText(userBean.passwd);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 跳转到开发者注册界面
     */
    private void jumpToLogonDevelopActivity() {
        Intent intent = new Intent(this, LogonDevelopActivity.class);
        startActivityForResult(intent, REQUEST_DEVELOP_USER_TO_LOGON);
    }

    /**
     * 跳转到用户注册界面
     */
    private void jumpToLogonActivity() {
        Intent intent = new Intent(this, LogonActivity.class);
        startActivityForResult(intent, REQUEST_USER_TO_LOGON);
    }

    /**
     * 跳转到用户主界面
     */
    private void jumpToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 跳转到开发者主界面
     */
    private void jumpToMainDevelopActivity() {
        //todo 下次开发 跳转到开发者主界面
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
    }

    /**
     * 检测填入的用户及密码是否正确
     *
     * @return
     */
    private boolean checkoutInfoOk() {
        mName = mEtName.getText().toString().trim();
        mPwd = mEtPwd.getText().toString().trim();
        if (TextUtils.isEmpty(mName)) {
            Toast.makeText(this, "请先填写用户名", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(mPwd)) {
            Toast.makeText(this, "请先填写密码", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * 开发者登录
     */
    private void developerLogin() {
        //TODO 下次开发 目前支持 重新记录apkid  后期需要支持进入开发者主界面
        mObserver = new MyObserver<DeveloperBean>(this) {
            @Override
            public void onSuccess(DeveloperBean result) {
                if (result == null) {
                    Toast.makeText(LoginActivity.this, "无数据", Toast.LENGTH_LONG).show();
                    return;
                }
                mApkid = result.apikey;
                //保存到全局变量中
                MainApplication.getInstance().saveInfo(Constant.SharedPref.BASE_DATA_DEV_APKID, result.apikey);
                //保存到共享文件
                SharedPreferences shared = MainApplication.getInstance().mSharedPref;
                SharedPreferences.Editor edit = shared.edit();
                edit.putString(Constant.SharedPref.BASE_DATA_DEV_APKID, result.apikey);
                edit.putString(Constant.SharedPref.BASE_DATA_DEV_EMAIL, result.email);
                edit.commit();
            }

            @Override
            public void onFailure(Throwable e, String errorMsg) {
                Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        };

        RetrofitUtils.getApiUrl()
                .developerLogin(mName, mPwd)
                .compose(RxHelper.observableIO2Main(this))
                .subscribe(mObserver);
    }

    /**
     * 用户登录
     *
     * @param apkid
     */
    private void loginUser(String apkid) {
        mObserver = new MyObserver<UserInfoBean>(this) {
            @Override
            public void onSuccess(UserInfoBean result) {
                if (result == null) {
                    Toast.makeText(LoginActivity.this, "无数据", Toast.LENGTH_LONG).show();
                    return;
                }
                saveToApplicion(result);
                saveToSharedPreferences(result);
                jumpToMainActivity();
            }

            @Override
            public void onFailure(Throwable e, String errorMsg) {
                Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        };

        RetrofitUtils.getApiUrl()
                .loginUser(apkid, mName, mPwd)
                .compose(RxHelper.observableIO2Main(this))
                .subscribe(mObserver);

    }

    /**
     * 保存数据到全局
     *
     * @param result
     */
    private void saveToApplicion(UserInfoBean result) {
        MainApplication.getInstance().saveInfo(Constant.SharedPref.BASE_DATA_USER_NAME, result.name);
        MainApplication.getInstance().saveInfo(Constant.SharedPref.BASE_DATA_USER_PASSWD, mPwd);
        MainApplication.getInstance().saveInfo(Constant.SharedPref.BASE_DATA_USER_NIKENAME, result.nikeName);
        MainApplication.getInstance().saveInfo(Constant.SharedPref.BASE_DATA_USER_HEADERIMG, result.headerImg);
        MainApplication.getInstance().saveInfo(Constant.SharedPref.BASE_DATA_USER_PHONE, result.phone);
        MainApplication.getInstance().saveInfo(Constant.SharedPref.BASE_DATA_USER_EMAIL, result.email);
        MainApplication.getInstance().saveInfo(Constant.SharedPref.BASE_DATA_USER_VIPGRADE, result.vipGrade);
        MainApplication.getInstance().saveInfo(Constant.SharedPref.BASE_DATA_USER_AUTOGRAPH, result.autograph);
        MainApplication.getInstance().saveInfo(Constant.SharedPref.BASE_DATA_USER_REMARKS, result.remarks);
    }

    /**
     * 保存数据到共享参数中
     */
    private void saveToSharedPreferences(UserInfoBean result) {
        SharedPreferences shared = MainApplication.getInstance().mSharedPref;
        SharedPreferences.Editor edit = shared.edit();
        edit.putString(Constant.SharedPref.BASE_DATA_USER_NAME, result.name);
        edit.putString(Constant.SharedPref.BASE_DATA_USER_PASSWD, mPwd);
        edit.putString(Constant.SharedPref.BASE_DATA_USER_NIKENAME, result.nikeName);
        edit.putString(Constant.SharedPref.BASE_DATA_USER_HEADERIMG, result.headerImg);
        edit.putString(Constant.SharedPref.BASE_DATA_USER_PHONE, result.phone);
        edit.putString(Constant.SharedPref.BASE_DATA_USER_EMAIL, result.email);
        edit.putString(Constant.SharedPref.BASE_DATA_USER_VIPGRADE, result.vipGrade);
        edit.putString(Constant.SharedPref.BASE_DATA_USER_AUTOGRAPH, result.autograph);
        edit.putString(Constant.SharedPref.BASE_DATA_USER_REMARKS, result.remarks);
        edit.commit();
    }

    /**
     * 跳转到忘记密码界面
     */
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void jumpToFindPwdActivity() {
        String name = mEtName.getText().toString().trim();

        FindPwdActivity.startFindPwdActivity(this, name);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mObserver.cancleRequest();
    }
}
