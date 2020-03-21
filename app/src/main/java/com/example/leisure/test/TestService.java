package com.example.leisure.test;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import androidx.annotation.Nullable;

public class TestService extends Service implements TestTask.OnTaskListener {
    private static final String TAG = "TestService";

    private LinkedHashMap<Integer, TestTask> tasks;
    int group;


    public interface ITestServiceCallback {
        void onUpdateProgress(int group, int position);

        void finish(int group);

        void finishStopService();
    }

    private List<ITestServiceCallback> callBacks = new LinkedList<>();

    //注册接口
    public void registerCallBack(ITestServiceCallback callBack) {
        if (callBacks != null) {
            callBacks.add(callBack);
        }
    }

    /**
     * 注销接口 false注销失败
     *
     * @param callBack
     * @return
     */
    public boolean unRegisterCallBack(ITestServiceCallback callBack) {
        if (callBacks != null && callBacks.contains(callBack)) {
            Log.e(TAG, "unRegisterCallBack: success");
            return callBacks.remove(callBack);
        }
        Log.e(TAG, "unRegisterCallBack: fail");
        return false;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        tasks = new LinkedHashMap<>();
        Log.e(TAG, "onCreate: ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand: ");
        if (tasks == null || tasks.size() == 0) {
            //重新获取数据
            for (int i = 0; i < 3; i++) {
                group = i;
                TestTask task = new TestTask(i, this);
                task.execute(i);
                tasks.put(i, task);
            }
        }
        return START_STICKY_COMPATIBILITY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind: ");

        return new TestBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "onUnbind: ");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy: ");
        super.onDestroy();

    }

    private void sendGroupFinish(int group) {
        Log.e(TAG, "sendGroupFinish: " + callBacks.size());
        if (callBacks != null && callBacks.size() > 0) {
            for (ITestServiceCallback callBack :
                    callBacks) {
                callBack.finish(group);
            }
        }
    }

    private void sendGroupProgress(int group, int progress) {
        Log.e(TAG, "sendGroupProgress: " + callBacks.size());
        if (callBacks != null && callBacks.size() > 0) {
            for (ITestServiceCallback callBack :
                    callBacks) {
                callBack.onUpdateProgress(group, progress);
            }
        }
    }

    private void sendServiceFinishStop() {
        if (callBacks != null && callBacks.size() > 0) {
            for (ITestServiceCallback callBack :
                    callBacks) {
                callBack.finishStopService();
            }
        }
    }

    @Override
    public void updateProgress(int group, int position) {
        sendGroupProgress(group, position);
    }

    @Override
    public void finishTask(int group) {
        Log.e(TAG, "finishTask: ");
        tasks.get(group).cancelTask();
        tasks.remove(group);
        if (tasks.size() == 0) {
            Log.e(TAG, "finishAllTask: ");
            stopSelf();
            sendServiceFinishStop();
        } else {
            sendGroupFinish(group);
        }
    }

    public void addTask() {
        group++;
        TestTask task = new TestTask(group, this);
        task.execute(group);
        tasks.put(group, task);
    }

    public void removeTask(int group) {
        tasks.get(group).cancelTask();
    }

    public boolean hasNoTask() {
        return tasks.size() == 0;
    }


    public class TestBinder extends Binder {
        public TestService getService() {
            return TestService.this;
        }
    }
}
