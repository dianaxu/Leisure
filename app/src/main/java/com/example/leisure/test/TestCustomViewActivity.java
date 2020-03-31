package com.example.leisure.test;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.leisure.R;
import com.example.leisure.widget.CountDownProgressView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TestCustomViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_custom_view);

        CountDownProgressView view = findViewById(R.id.cdpv_view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(TestCustomViewActivity.this, "停止", Toast.LENGTH_SHORT).show();
                view.stopCountDown();
            }
        });

        view.setOnTimeFinishListener(new CountDownProgressView.onTimeFinishListener() {
            @Override
            public void onTimeFinish() {
                Toast.makeText(TestCustomViewActivity.this, "完成", Toast.LENGTH_SHORT).show();

            }
        });
        view.startCountDown();
    }
}
