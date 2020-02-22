package com.example.leisure.test;

import android.os.Bundle;

import com.example.leisure.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Test4Activity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment, new WaveFragment(), "WaveFragment")
                .commit();
    }
}
