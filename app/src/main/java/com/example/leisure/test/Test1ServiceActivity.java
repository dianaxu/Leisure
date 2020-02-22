package com.example.leisure.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.leisure.MainApplication;
import com.example.leisure.R;
import com.example.leisure.service.TestService;

import androidx.annotation.Nullable;

public class Test1ServiceActivity extends Activity implements View.OnClickListener {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_service);
        findViewById(R.id.btn_jump).setOnClickListener(this);
        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TestService testService = MainApplication.getInstance().getTestService();
                if (testService != null)
                    testService.add();
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, Test3ServiceActivity.class);
        startActivity(intent);
    }
}
