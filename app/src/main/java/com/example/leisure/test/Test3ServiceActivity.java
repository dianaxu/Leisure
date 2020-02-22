package com.example.leisure.test;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.example.leisure.R;
import com.example.leisure.service.TestService;

import androidx.annotation.Nullable;


public class Test3ServiceActivity extends Activity implements View.OnClickListener {
    private TestService mService;
    private boolean isCon = false;
    private ServiceConnection mConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TestService.TestBinder binder = (TestService.TestBinder) service;
            mService = binder.getTestService();
            isCon = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isCon = false;
        }
    };
    private String TAG = "TestService";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_service);

        Intent intent = new Intent(this, TestService.class);
        bindService(intent, mConn, BIND_AUTO_CREATE);

        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mService.add();

            }
        });

        findViewById(R.id.btn_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mService.stop();
            }
        });

        findViewById(R.id.btn_jump).setOnClickListener(this
        );
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, Test2ServiceActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy:  ");
        super.onDestroy();
        unbindService(mConn);
    }
}
