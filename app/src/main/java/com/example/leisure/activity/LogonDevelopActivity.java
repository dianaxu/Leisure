package com.example.leisure.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leisure.MainApplication;
import com.example.leisure.R;
import com.example.leisure.bean.DeveloperBean;
import com.example.leisure.retrofit.MyObserver;
import com.example.leisure.retrofit.RetrofitUtils;
import com.example.leisure.retrofit.RxHelper;
import com.example.leisure.util.Constant;
import com.example.leisure.widget.CommonToolbar;


/**
 * 注册界面-开发者  用户
 *
 */
public class LogonDevelopActivity extends BaseActivity {
    public static final String EXTRA_APKID = "extra_apkid";


    private CommonToolbar mCtbHeader;
    private EditText mEtName, mEtPwd, mEtRewritePwd, mEtEmail;
    private TextView mTvLogon;

    private DeveloperBean mDeveloperBean = new DeveloperBean();
    private SharedPreferences mShared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logon_dev);


        mCtbHeader = findViewById(R.id.ctb_header);
        mEtName = findViewById(R.id.et_name);
        mEtPwd = findViewById(R.id.et_pwd);
        mEtRewritePwd = findViewById(R.id.et_rewrite_pwd);
        mEtEmail = findViewById(R.id.et_email);
        mTvLogon = findViewById(R.id.tv_logon);

        setViewMarginTop(mCtbHeader);

        mCtbHeader.setLeftClickListener(new CommonToolbar.OnLeftDrawableClickListener() {
            @Override
            public void onLeftDrawableClick() {
                finish();
            }
        });
        mTvLogon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkInfo()) return;
                logon();
            }
        });
    }


    private void logon() {
        MyObserver observer = new MyObserver<DeveloperBean>(this) {
            @Override
            public void onSuccess(DeveloperBean result) {
                if (result == null) {
                    Toast.makeText(LogonDevelopActivity.this, "无数据", Toast.LENGTH_LONG).show();
                    return;
                }
                saveToSharedPreferences(result);
                saveToApplicion(result);

                Intent intent = new Intent();
                intent.putExtra(EXTRA_APKID, result.apikey);
                // 设置返回码和返回携带的数据
                setResult(Activity.RESULT_OK, intent);
                // RESULT_OK就是一个默认值，=-1，它说OK就OK吧
                finish();
            }

            @Override
            public void onFailure(Throwable e, String errorMsg) {
                Toast.makeText(LogonDevelopActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        };
        RetrofitUtils.getApiUrl()
                .developerRegister(mDeveloperBean.name, mDeveloperBean.passwd, mDeveloperBean.email)
                .compose(RxHelper.observableIO2Main(this))
                .subscribe(observer);
    }

    /**
     * 保存数据到全局
     *
     * @param result
     */
    private void saveToApplicion(DeveloperBean result) {
        MainApplication.getInstance().saveInfo(Constant.SharedPref.BASE_DATA_DEV_APKID, result.apikey);
    }

    /**
     * 保存数据到共享参数中
     */
    private void saveToSharedPreferences(DeveloperBean result) {
        mShared = MainApplication.getInstance().mSharedPref;
        SharedPreferences.Editor edit = mShared.edit();
        edit.putString("apikey", result.apikey);
        edit.commit();
    }

    private boolean checkInfo() {
        mDeveloperBean.name = mEtName.getText().toString().trim();
        mDeveloperBean.passwd = mEtPwd.getText().toString().trim();
        String rewritePwd = mEtRewritePwd.getText().toString().trim();
        mDeveloperBean.email = mEtEmail.getText().toString().trim();

        if (TextUtils.isEmpty(mDeveloperBean.name)) {
            Toast.makeText(this, "请填写用户名", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(mDeveloperBean.passwd)) {
            Toast.makeText(this, "请填写密码", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(rewritePwd)) {
            Toast.makeText(this, "请填写重写密码", Toast.LENGTH_SHORT).show();
            return false;
        }

        //todo 检测email写的格式
        if (TextUtils.isEmpty(mDeveloperBean.email)) {
            Toast.makeText(this, "请填写email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!TextUtils.equals(mDeveloperBean.passwd, rewritePwd)) {
            Toast.makeText(this, "密码填写不一致", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;

    }

}
