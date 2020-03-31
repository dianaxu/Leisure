package com.example.leisure.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.leisure.R;
import com.example.leisure.widget.CountDownProgressView;

public class SplashActivity extends BaseActivity {

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return TransitionMode.RIGHT;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        CountDownProgressView cdpvView = findViewById(R.id.cdpv_view);
        cdpvView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToMainActivity();
            }
        });

        cdpvView.setOnTimeFinishListener(new CountDownProgressView.onTimeFinishListener() {
            @Override
            public void onTimeFinish() {
                jumpToMainActivity();
            }
        });
        cdpvView.startCountDown();
    }


    private void jumpToMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
