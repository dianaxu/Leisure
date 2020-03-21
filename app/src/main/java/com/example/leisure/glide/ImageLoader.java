package com.example.leisure.glide;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.leisure.GlideApp;
import com.example.leisure.R;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class ImageLoader {
    public static final String TAG = "ImageLoader";

    /**
     * 借助内部类 实现线程安全的单例模式
     * 属于懒汉式单例，因为Java机制规定，内部类SingletonHolder只有在getInstance()
     * 方法第一次调用的时候才会被加载（实现了lazy），而且其加载过程是线程安全的。
     * 内部类加载的时候实例化一次instance。
     */
    private ImageLoader() {
    }

    private static class ImageLoaderHolder {
        private final static ImageLoader INSTANCE = new ImageLoader();
    }

    public static ImageLoader getInstance() {
        return ImageLoaderHolder.INSTANCE;
    }

    public void with(Context context, String imageUrl, SubsamplingScaleImageView imageView) {
        if (context != null) {
            GlideApp.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_default_cover)
                    .error(R.drawable.ic_error_img)
                    .fitCenter()
                    .downloadOnly(new SimpleTarget<File>() {
                        @Override
                        public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                            imageView.setImage(ImageSource.uri(Uri.fromFile(resource)));
                        }
                    });
        } else {
            Log.i(TAG, "Picture loading failed,context is null");
        }
    }

    public void with(Activity activity, String imageUrl, SubsamplingScaleImageView imageView) {
        if (!checkActivity(activity)) {
            GlideApp.with(activity)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_default_cover)
                    .error(R.drawable.ic_error_img)
                    .fitCenter()
                    .downloadOnly(new SimpleTarget<File>() {
                        @Override
                        public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                            imageView.setImage(ImageSource.uri(Uri.fromFile(resource)));
                        }
                    });
        } else {
            Log.i(TAG, "Picture loading failed,context is null");
        }
    }

    public void withCircle(Context context, String imageUrl, ImageView imageView) {
        if (context != null) {
            GlideApp.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_default_cover)
                    .error(R.drawable.ic_error_img)
                    .fitCenter()
                    .transform(new CircleCrop())
                    .into(imageView);
        } else {
            Log.i(TAG, "Picture loading failed,context is null");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void withCircle(Activity activity, String imageUrl, ImageView imageView) {
        if (!checkActivity(activity)) {
            GlideApp.with(activity)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_default_cover)
                    .error(R.drawable.ic_error_img)
                    .fitCenter()
                    .transform(new CircleCrop())
                    .into(imageView);
        } else {
            Log.i(TAG, "Picture loading failed,context is null");
        }
    }

    public void with(Context context, String imageUrl, ImageView imageView) {
        if (context != null) {
            if (!imageUrl.equals(imageView.getTag())) {
                imageView.setTag(null);
                GlideApp.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_default_cover)
                        .error(R.drawable.ic_error_img)
                        .fitCenter()//图片缩放到小于等于 ImageView 的尺寸
                        .optionalFitCenter()
                        .transform(new RoundedCorners(4))  //设置圆角
                        .override(140, 187)
                        .into(imageView);
                // imageView.setTag(imageUrl);
            }
        } else {
            Log.i(TAG, "Picture loading failed,context is null");
        }

    }

    public void with(Activity activity, String imageUrl, ImageView imageView) {
        if (!checkActivity(activity)) {
            if (!imageUrl.equals(imageView.getTag())) {
                imageView.setTag(null);
                GlideApp.with(activity)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_default_cover)
                        .error(R.drawable.ic_error_img)
                        .fitCenter()//图片缩放到小于等于 ImageView 的尺寸
                        .optionalFitCenter()
                        .transform(new RoundedCorners(4))  //设置圆角
                        .override(140, 187)
                        .into(imageView);
//                imageView.setTag(imageUrl);

            } else {
                Log.i(TAG, "Picture loading failed,context is null");
            }

        }
    }

    private boolean checkActivity(Activity activity) {
        if (Build.VERSION.SDK_INT >= 17) {
            return activity.isDestroyed();
        } else {
            return activity.isFinishing();
        }
    }

    public void withWidthMatch(Context context, int pWidth, String imageUrl, ImageView imageView) {
        if (context != null) {
            GlideApp.with(context)
                    .asBitmap()
                    .placeholder(R.drawable.ic_default_cover)
                    .error(R.drawable.ic_error_img)
                    .load(imageUrl)
                    .encodeQuality(70)
                    .format(DecodeFormat.PREFER_RGB_565)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            int height = resource.getHeight() * pWidth / resource.getWidth();
                            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                            layoutParams.width = pWidth;
                            layoutParams.height = height;
                            Log.e(TAG, "onResourceReady: " + pWidth + "  :" + height);
                            imageView.setLayoutParams(layoutParams);
                            imageView.setImageBitmap(resource);

                        }
                    });
        } else {
            Log.i(TAG, "Picture loading failed,context is null");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void withWidthMatch(Activity activity, int pWidth, String imageUrl, ImageView imageView) {
        if (!checkActivity(activity)) {
            GlideApp.with(activity)
                    .asBitmap()
                    .placeholder(R.drawable.ic_default_cover)
                    .error(R.drawable.ic_error_img)
                    .load(imageUrl)
                    .encodeQuality(70)
                    .format(DecodeFormat.PREFER_RGB_565)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            int height = resource.getHeight() * pWidth / resource.getWidth();
                            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
                            layoutParams.width = pWidth;
                            layoutParams.height = height;
                            Log.e(TAG, "onResourceReady: " + pWidth + "  :" + height);
                            imageView.setLayoutParams(layoutParams);
                            imageView.setImageBitmap(resource);

                        }
                    });
        } else {
            Log.i(TAG, "Picture loading failed,context is null");
        }
    }
}