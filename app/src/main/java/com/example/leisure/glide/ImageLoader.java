package com.example.leisure.glide;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
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

public class ImageLoader {
    public static final String TAG = "mainActivity";

    public static void with(Context context, String imageUrl, SubsamplingScaleImageView imageView) {
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
    }

    public static void withCircle(Context context, String imageUrl, ImageView imageView) {
        GlideApp.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_default_cover)
                .error(R.drawable.ic_error_img)
                .fitCenter()
                .transform(new CircleCrop())
                .into(imageView);
    }

    public static void with(Context context, String imageUrl, ImageView imageView) {
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

    }

    public static void withWidthWa(Context context, int pWidth, String imageUrl, ImageView imageView) {
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
//                .into(new ImageViewTarget<Bitmap>(imageView) {
//                    @Override
//                    protected void setResource(@Nullable Bitmap resource) {
//                        int height = resource.getHeight() * resource.getWidth() / pWidth;
//                        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(pWidth, height);
//                        imageView.setLayoutParams(params);
//                        imageView.setImageBitmap(resource);
//                    }
//                });
    }
}