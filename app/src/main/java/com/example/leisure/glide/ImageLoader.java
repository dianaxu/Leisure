package com.example.leisure.glide;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.bitmap.CircleCrop;
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

    public static void with(Context context, String imageUrl, SubsamplingScaleImageView imageView) {
        GlideApp.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.ic_arrow)
                .error(R.drawable.ic_close_menu)
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
                .placeholder(R.drawable.ic_arrow)
                .error(R.drawable.ic_close_menu)
                .fitCenter()
                .transform(new CircleCrop())
                .into(imageView);
    }

    public static void with(Context context, String imageUrl, ImageView imageView) {
//        Random random = new Random();
//        int height = (random.nextInt(4) * 100) + 200;
//        int width = imageView.getWidth();
//        imageView.setMaxWidth(width);
//        ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
//        layoutParams.height = 400;
//        imageView.setLayoutParams(layoutParams);
        if (!imageUrl.equals(imageView.getTag())) {
            imageView.setTag(null);
            GlideApp.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.iphone_bg)
                    .fitCenter()//图片缩放到小于等于 ImageView 的尺寸
//                .centerCrop() //将图片按比例缩放到足矣填充 ImageView 的尺寸
                    .optionalFitCenter()
//                    .optionalCenterCrop()
                    .into(imageView);
            // imageView.setTag(imageUrl);
        }
//        GlideApp.with(context)
//                .load(imageUrl)
//                .dontAnimate()
//                .into(new SimpleTarget<Drawable>() {
//                    @Override
//                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
//                        int imageHeight = resource.getIntrinsicHeight();
//                        if (imageHeight > 4096) {
//                            imageHeight = 4096;
//                            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
//                            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
//                            layoutParams.height = imageHeight;
//                            imageView.setLayoutParams(layoutParams);
//
//                            GlideApp.with(context)
//                                    .load(imageUrl)
//                                    .dontAnimate()
//                                    .centerCrop()
//                                    .into(imageView);
//                        } else {
//                            GlideApp.with(context)
//                                    .load(imageUrl)
//                                    .dontAnimate()
//                                    .into(imageView);
//                        }
//                    }
//                });
//                .into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
//                    @Override
//                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
//                        int imageHeight = resource.getHeight();
//                        if (imageHeight > 4096) {
//                            imageHeight = 4096;
//                            ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
//                            layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
//                            layoutParams.height = imageHeight;
//                            imageView.setLayoutParams(layoutParams);
//
//                            GlideApp.with(context)
//                                    .load(imageUrl)
//                                    .dontAnimate()
//                                    .centerCrop()
//                                    .into(imageView);
//                        } else {
//                            GlideApp.with(context)
//                                    .load(imageUrl)
//                                    .dontAnimate()
//                                    .into(imageView);
//                        }
//                    }

//                });
    }
}