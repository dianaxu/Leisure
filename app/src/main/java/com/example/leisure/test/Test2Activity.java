package com.example.leisure.test;

import android.os.Bundle;

import com.example.leisure.R;
import com.example.leisure.fragment.ComicFragment;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

public class Test2Activity extends FragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_video);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.containerView, ComicFragment.newInstance())
                .commit();
    }
}
