package com.example.leisure.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class TestService extends Service {

    private String TAG = "TestService";

    private int num = 0;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate: ");
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind: ");
        return new TestBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, " xxxx service-- onUnbind: ");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        stop();
        Log.e(TAG, "xxxx  service-- onDestroy: ");
        super.onDestroy();
    }

    private Thread thread;

    public void add() {
        isStop = false;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < Integer.MAX_VALUE; i++) {
                    if (isStop) return;
                    num++;
                    Log.e(TAG, "add: --> " + num);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();


    }


    private boolean isStop = false;

    public void stop() {
        Log.e(TAG, "stop: ");
        isStop = true;
        stopSelf();
    }


    public class TestBinder extends Binder {

        public TestService getTestService() {
            return TestService.this;
        }

    }
}
