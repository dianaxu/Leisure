package com.example.leisure;

import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;


@GlideModule
public class MyAppGlideModule extends AppGlideModule {
//    @Override
//    public void applyOptions(@NonNull Context context, @NonNull GlideBuilder builder) {
//        super.applyOptions(context, builder);
    //选用默认的
//        int memoryCacheSizeBytes = 1024 * 1024 * 20; // 20mb
//        builder.setMemoryCache(new LruResourceCache(memoryCacheSizeBytes));//内存缓存
//        int bitmapPoolSizeBytes = 1024 * 1024 * 30; // 30mb
//        builder.setBitmapPool(new LruBitmapPool(bitmapPoolSizeBytes));//Bitmap池
//        int diskCacheSizeBytes = 1024 * 1024 * 100;  //100 MB
//        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, diskCacheSizeBytes)); //磁盘缓存大小
//        builder.setDefaultRequestOptions(RequestOptions.circleCropTransform());
//    }
}
