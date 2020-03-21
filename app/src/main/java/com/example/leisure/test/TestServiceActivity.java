package com.example.leisure.test;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.leisure.MainApplication;
import com.example.leisure.R;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TestServiceActivity extends AppCompatActivity implements TestService.ITestServiceCallback {
    private static final String TAG = "TestService";

    private TestService mService;
    private EditText etNum;
    Intent intent;
    private boolean hasStopService = false;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TestService.TestBinder binder = (TestService.TestBinder) service;
            mService = binder.getService();
            mService.registerCallBack(TestServiceActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mService.unRegisterCallBack(TestServiceActivity.this);
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_test_service);
        etNum = findViewById(R.id.et_num);
        findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mService != null) {
                    mService.addTask();
                }
            }
        });
        findViewById(R.id.btn_stop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mService != null) {
                    String text = etNum.getText().toString();

                    mService.removeTask(TextUtils.isEmpty(text) ? 2 : Integer.parseInt(text));
                }
            }
        });
        findViewById(R.id.btn_jump).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TestServiceActivity.this, TestService1Activity.class);
                startActivity(intent);
            }
        });

        TestService downloadService = MainApplication.getInstance().getTestService();

        intent = new Intent(this, TestService.class);
        if (downloadService == null) {
            startService(intent);
            Log.e(TAG, "TestServiceActivity  --> startService: ");
        }
        bindService(intent, conn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mService != null)
            mService.registerCallBack(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!hasStopService)
            if (mService != null) {
                mService.unRegisterCallBack(this);
            }
    }

    @Override
    protected void onDestroy() {
//        if (mService != null) {
//            unbindService(conn);
//        }
        Log.e(TAG, " TestServiceActivity-->onDestroy: " + hasStopService);
//        if (mService != null && !mService.hasNoTask()) {
        if (!hasStopService)
            if (mService != null)
                unbindService(conn);
//        }
        super.onDestroy();
    }

    @Override
    public void onUpdateProgress(int group, int position) {
        Toast.makeText(this, "" + group + " : " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finish(int group) {
        Toast.makeText(this, "finish" + group, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void finishStopService() {
        hasStopService = true;
    }
}
