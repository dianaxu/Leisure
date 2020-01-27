package com.example.leisure.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.leisure.R;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 网易新闻详情
 *todo 问题1：显示网页版页面 不是手机版
 * 问题2：公共导航返回和分享
 */
public class WYNewsDetailsActivity extends AppCompatActivity {
    public static final String EXTRA_PATH = "extra_path";

    private WebView mWvView;

    public static void startActivity(Context context, String path) {
        Intent intent = new Intent(context, WYNewsDetailsActivity.class);
        intent.putExtra(EXTRA_PATH, path);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wy_news_details);

        Intent intent = getIntent();
        String path = intent.getStringExtra(EXTRA_PATH);


        mWvView = findViewById(R.id.wv_view);

        mWvView.loadUrl(path);
        //系统默认会通过手机浏览器打开网页，为了能够直接通过WebView显示网页，则必须设置
        mWvView.setWebViewClient(new WebViewClient() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

        });



        //声明WebSettings子类

        WebSettings webSettings = mWvView.getSettings();
        //设置自适应屏幕，两者合用
        webSettings.setUseWideViewPort(true); //将图片调整到适合webview的大小
        webSettings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小

    }

}
