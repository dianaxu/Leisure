package com.example.leisure.test;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

        CountDownProgressView view = findViewById(R.id.cdpv_view1);
        TextView tv_text = findViewById(R.id.tv_text);
//        CountDownProgressView view1 = findViewById(R.id.cdpv_view1);
//        CountDownProgressView view2 = findViewById(R.id.cdpv_view2);

        Button btn = findViewById(R.id.btn_start);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.setText("跳过");
                view.startCountDown();
                tv_text.setText("1");
//                view1.startCountDown();
//                view2.startCountDown();
            }
        });

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
//        view.startCountDown();
    }

}
