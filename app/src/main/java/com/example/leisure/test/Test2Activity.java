package com.example.leisure.test;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.leisure.R;
import com.example.leisure.receiver.DownloadReceiver;
import com.example.leisure.service.DownloadService;
import com.example.leisure.util.FileUtil;
import com.example.leisure.util.SdcardUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

public class Test2Activity extends FragmentActivity implements View.OnClickListener, DownloadReceiver.onUpdateUIListener {
    private static final String TAG = "Test2Activity";
    private RecyclerView mRvView;
    private ImageView mIvImage;
    private TextView mTvFileSize;
    private TextView mTvDownload;
    private ProgressBar mProgressBar;

    private DownloadReceiver mReceiver;
    private DownloadService.DownloadBinder mBinder;
    private MyServiceConn mServiceConn = new MyServiceConn();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_video);

        mRvView = findViewById(R.id.rv_view);
        mIvImage = findViewById(R.id.iv_image);
        mTvFileSize = findViewById(R.id.tv_file_size);
        mTvDownload = findViewById(R.id.tv_download);
        mProgressBar = findViewById(R.id.progressBar);
        mTvDownload.setMovementMethod(ScrollingMovementMethod.getInstance());

        findViewById(R.id.btn_download).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.example.leisure.DownloadReceiver");
        mReceiver = new DownloadReceiver(this);
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_stop) {
            //停止
//            Intent stopIntent = new Intent(this, DownloadService.class);
//            stopService(stopIntent);

//            DisplayMetrics dm = getResources().getDisplayMetrics();
//            int heigth = dm.heightPixels;
//            int width = dm.widthPixels;
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append("总：" + SdcardUtil.getTotalSize() + "MB；\n ");
            stringBuffer.append("剩" + SdcardUtil.getAvailableSize() + "MB;\n");
//            stringBuffer.append("width:" + width + "  heigth:" + heigth + "\n");
            String filePath = FileUtil.getSavePath(this, null, null, null);

            stringBuffer.append("总文件大小：" + FileUtil.getFileOrFilesSize(filePath, FileUtil.SIZETYPE_MB) + "MB\n");

            String filePath1 = this.getCacheDir().getPath() + File.separator;
            stringBuffer.append("cachedir:" + filePath1 + "\n");

            mTvFileSize.setText(stringBuffer.toString());
//            mBinder.cancelTask();
        } else if (id == R.id.btn_download) {
            ActivityCompat.requestPermissions(Test2Activity.this, new String[]{android
                    .Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

            ArrayList<Integer> list = new ArrayList<>();
            Integer[] pos = {1, 6};
            list.addAll(Arrays.asList(pos));

            //启动
            Intent intent = new Intent(this, DownloadService.class);
            startService(intent);
            bindService(intent, mServiceConn, BIND_AUTO_CREATE);
        }
    }

    /**
     * 请求手机权限结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //创建文件夹
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                        File file = new File(Environment.getExternalStorageDirectory() + "/comic/");
                        if (!file.exists()) file.mkdirs();
                    }
                    break;
                }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
        unbindService(mServiceConn);
    }

    @Override
    public void updateUI(int receiverState, int totalCount, int maxCount, long chapterId) {
        CharSequence text = mTvDownload.getText();
        mTvDownload.setText("receiverState=>" + text + "\n" + totalCount + ":" + maxCount + "--" + chapterId);
    }

    //-------------------------------------------------压缩图片

    //计算图片的缩放值
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth,
                                            int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        return inSampleSize;
    }

    // 根据路径获得图片并压缩，返回bitmap用于显示
    public static Bitmap getSmallBitmap(String filePath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 384, 592);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(filePath, options);
    }

    //把bitmap转换成String
    public static String bitmapToString(String filePath) {

        Bitmap bm = getSmallBitmap(filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public class MyServiceConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (DownloadService.DownloadBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

}


