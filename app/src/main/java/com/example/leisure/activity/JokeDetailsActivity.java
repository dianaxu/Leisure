package com.example.leisure.activity;

import android.os.Bundle;

import com.example.leisure.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 推荐页详情
 * 1.实现xml
 * 2.需要知道评论的显示
 * 3.图片的显示
 * 4.可以评论的
 */
public class JokeDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joke_details);
    }
}
