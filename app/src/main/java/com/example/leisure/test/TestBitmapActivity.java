package com.example.leisure.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leisure.R;
import com.example.leisure.glide.ImageLoader;
import com.example.leisure.retrofit.RxExceptionUtil;
import com.example.leisure.util.FileUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class TestBitmapActivity extends AppCompatActivity {
    private static int widthPixels;
    private ImageView mIvImage1;
    private ImageView mIvImage2;
    private ImageView mIvImage3;
    private TextView mTvInfo;
    private String imageUrl = "https://m.comic123.net/pic-qq//manhua.qpic.cn/manhua_detail/0/30_17_33_5bf822924711dcbeefaeb2e54b2d1625_6689.jpg/0";

    private Handler mHandle = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            double size = (double) msg.obj;
            switch (msg.what) {
                case 1:
                    mTvInfo.setText("\n" + mTvInfo.getText() + "原图大小：" + size);
                    //解析图片名称出问题
                    String imageName = analysisImageUrl(imageUrl);
                    //图片名称解析完  存放的位置
                    String savePath = FileUtil.getSavePath(TestBitmapActivity.this, (long) 1, (long) 1, imageName);
                    ImageLoader.getInstance().withWidthMatch(TestBitmapActivity.this, widthPixels, savePath, mIvImage1);
                    break;
                case 2:
                    mTvInfo.setText("\n" + mTvInfo.getText() + "质量下降到70：" + size);
                    //解析图片名称出问题
                    String imageName1 = analysisImageUrl(imageUrl);
                    //图片名称解析完  存放的位置
                    String savePath1 = FileUtil.getSavePath(TestBitmapActivity.this, (long) 1, (long) 2, imageName1);
                    ImageLoader.getInstance().withWidthMatch(TestBitmapActivity.this, widthPixels,savePath1, mIvImage2);
                    break;
                case 3:
                    mTvInfo.setText("\n" + mTvInfo.getText() + "最优方式：" + size);
                    //解析图片名称出问题
                    String imageName2 = analysisImageUrl(imageUrl);
                    //图片名称解析完  存放的位置
                    String savePath2 = FileUtil.getSavePath(TestBitmapActivity.this, (long) 1, (long) 3, imageName2);
                    ImageLoader.getInstance().withWidthMatch(TestBitmapActivity.this, widthPixels,savePath2, mIvImage3);
                    break;
                default:
                    break;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_bitmap);
        mIvImage1 = findViewById(R.id.iv_image1);
        mIvImage2 = findViewById(R.id.iv_image2);
        mIvImage3 = findViewById(R.id.iv_image3);
        mTvInfo = findViewById(R.id.tv_info);

        getScreenRelatedInformation(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                String path = downloadImage(imageUrl);
                double filesSize = FileUtil.getFileOrFilesSize(path, FileUtil.SIZETYPE_MB);
                Message msg = new Message();
                msg.what = 1;
                msg.obj = filesSize;
                mHandle.sendMessage(msg);
            }
        }).start();


        new Thread(new Runnable() {
            @Override
            public void run() {
                String path1 = downloadImage1(imageUrl);
                double filesSize1 = FileUtil.getFileOrFilesSize(path1, FileUtil.SIZETYPE_MB);
                Message msg = new Message();
                msg.what = 2;
                msg.obj = filesSize1;
                mHandle.sendMessage(msg);
            }
        }).start();


        new Thread(new Runnable() {
            @Override
            public void run() {
                String path2 = downloadImage2(imageUrl);
                double filesSize2 = FileUtil.getFileOrFilesSize(path2, FileUtil.SIZETYPE_MB);
                Message msg = new Message();
                msg.what = 3;
                msg.obj = filesSize2;
                mHandle.sendMessage(msg);
            }
        }).start();


    }

    private String downloadImage(String imgUrl) {
        //解析图片名称出问题
        String imageName = analysisImageUrl(imgUrl);
        //图片名称解析完  存放的位置
        String savePath;

        Bitmap bitmap;
        HttpURLConnection connection;
        InputStream inputStream;
        try {
            connection = (HttpURLConnection) new URL(imgUrl).openConnection(); // 打开一个连接
            connection.setConnectTimeout(3000);   // 设置连接时长
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
//                if (bitmap == null) return null;

                //保存图片到本地
                savePath = FileUtil.saveBitmapToFile(this, bitmap,
                        (long) 1, (long) 1, analysisImageUrl(imgUrl));
                return savePath;
            }
        } catch (MalformedURLException e) {
            String msg = RxExceptionUtil.exceptionHandler(e);
            e.printStackTrace();
        } catch (IOException e) {
            String msg = RxExceptionUtil.exceptionHandler(e);
            e.printStackTrace();
        }
        return null;
    }

    private String downloadImage1(String imgUrl) {
        //解析图片名称出问题
        String imageName = analysisImageUrl(imgUrl);
        //图片名称解析完  存放的位置
        String savePath;

        Bitmap bitmap;
//        HttpURLConnection connection;
//        InputStream inputStream;
        try {
            URL url = new URL(imgUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = connection.getInputStream();
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();


            //保存图片到本地
            savePath = FileUtil.saveBitmapToFile1(this, bitmap,
                    (long) 1, (long) 2, analysisImageUrl(imgUrl));
            return savePath;

        } catch (MalformedURLException e) {
            String msg = RxExceptionUtil.exceptionHandler(e);
            e.printStackTrace();
        } catch (IOException e) {
            String msg = RxExceptionUtil.exceptionHandler(e);
            e.printStackTrace();
        }
        return null;
    }

    private String downloadImage2(String imgUrl) {
        //解析图片名称出问题
        String imageName = analysisImageUrl(imgUrl);
        //图片名称解析完  存放的位置
        String savePath;

        Bitmap bitmap;
        HttpURLConnection connection;
        InputStream inputStream;
        try {
            connection = (HttpURLConnection) new URL(imgUrl).openConnection(); // 打开一个连接
            connection.setConnectTimeout(3000);   // 设置连接时长
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();

                if (inputStream == null) {
                    throw new RuntimeException("stream is null");
                } else {
                    try {
                        byte[] data = readStream(inputStream);
                        if (data != null) {
                            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                            int width = bitmap.getWidth();
                            int height = bitmap.getHeight();
                            Matrix matrix = new Matrix();
                            //也可以按两者之间最大的比例来设置放大比例，这样不会是图片压缩
                            matrix.postScale((float) widthPixels / width, (float) widthPixels / width); // 长和宽放大缩小的比例
                            Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, width,
                                    height, matrix, true);

                            //保存图片到本地
                            savePath = FileUtil.saveBitmapToFile1(this, resizeBmp,
                                    (long) 1, (long) 3, analysisImageUrl(imgUrl));
                            return savePath;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    inputStream.close();
                }

            }
        } catch (MalformedURLException e) {
            String msg = RxExceptionUtil.exceptionHandler(e);
            e.printStackTrace();
        } catch (IOException e) {
            String msg = RxExceptionUtil.exceptionHandler(e);
            e.printStackTrace();
        }
        return null;
    }

    /*
     * 得到图片字节流 数组大小
     * */
    public static byte[] readStream(InputStream inStream) throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        inStream.close();
        return outStream.toByteArray();
    }

    /**
     * 解析出图片名称
     *
     * @param imageUrl
     * @return
     */
    private String analysisImageUrl(String imageUrl) {
        String imageFileName = null;
        String[] split = imageUrl.split("/");
        for (int i = 0; i < split.length; i++) {
            if (split[i].contains(".jpg") || split[i].contains("png")) {
                imageFileName = split[i];
            }
        }
        if (imageFileName == null) return imageFileName;
        else
            return imageFileName.replace("_", "")
                    .replace(".jpg", "")
                    .replace(".png", "");
    }

    public static void getScreenRelatedInformation(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) {
            DisplayMetrics outMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(outMetrics);
            widthPixels = outMetrics.widthPixels;
            int heightPixels = outMetrics.heightPixels;
            int densityDpi = outMetrics.densityDpi;
            float density = outMetrics.density;
            float scaledDensity = outMetrics.scaledDensity;
            //可用显示大小的绝对宽度（以像素为单位）。
            //可用显示大小的绝对高度（以像素为单位）。
            //屏幕密度表示为每英寸点数。
            //显示器的逻辑密度。
            //显示屏上显示的字体缩放系数。
            Log.d("display", "widthPixels = " + widthPixels + ",heightPixels = " + heightPixels + "\n" +
                    ",densityDpi = " + densityDpi + "\n" +
                    ",density = " + density + ",scaledDensity = " + scaledDensity);
        }
    }
}
