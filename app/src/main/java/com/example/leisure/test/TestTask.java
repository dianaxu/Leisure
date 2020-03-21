package com.example.leisure.test;

import android.os.AsyncTask;
import android.util.Log;

public class TestTask extends AsyncTask<Integer, Integer, Integer> {
    private static final String TAG = "TestService";
    private int group;
    private boolean isCance = false;

    public void cancelTask() {
        isCance = true;
    }

    private OnTaskListener listener;

    public interface OnTaskListener {
        void updateProgress(int group, int position);

        void finishTask(int group);
    }

    public TestTask(int group, OnTaskListener listener) {
        this.group = group;
        this.listener = listener;
    }

    @Override
    protected Integer doInBackground(Integer... voids) {
        for (int i = 0; i < 20; i++) {
            if (isCance) {
                Log.e(TAG, "doInBackground: ---cancel");
                return group;
            }
            publishProgress(i);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.e(TAG, "doInBackground: " + group + "----->" + i);
        }
        return group;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        listener.updateProgress(group, values[0]);
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        listener.finishTask(integer);
    }
}
