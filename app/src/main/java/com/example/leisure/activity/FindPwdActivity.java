package com.example.leisure.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.leisure.R;
import com.example.leisure.widget.CommonToolbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.leisure.util.Util.PACKAGE;

/**
 * 找回密码界面
 * 此界面没有相应的接口，功能未开
 */
public class FindPwdActivity extends AppCompatActivity {
    public static final String EXTRA_NAME = PACKAGE + "name";

    private CommonToolbar mCtbHeader;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static void startFindPwdActivity(Context context, String name) {
        Intent intent = new Intent(context, FindPwdActivity.class);
        intent.putExtra(EXTRA_NAME, name);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_features);
        mCtbHeader = findViewById(R.id.ctb_header);
        mCtbHeader.setLeftClickListener(new CommonToolbar.OnLeftDrawableClickListener() {
            @Override
            public void onLeftDrawableClick() {
                finish();
            }
        });
    }

}
