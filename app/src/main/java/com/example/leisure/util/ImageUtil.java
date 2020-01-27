package com.example.leisure.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

public class ImageUtil {
    public static Bitmap drawableToBitmap(Drawable drawable) {

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        //canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return bitmap;

    }

    public static Bitmap drawableToZoomBitmap(Drawable drawable, int maxWidth, int maxHeight) {
//        int width = drawable.getIntrinsicWidth();
//        int height = drawable.getIntrinsicHeight();
//        if (height > maxHeight) height = maxHeight;

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(),
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565);
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) maxWidth / drawable.getIntrinsicWidth());
        float scaleHeight = ((float) maxHeight / drawable.getIntrinsicHeight());
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), matrix, true);


        Canvas canvas = new Canvas(newbmp);
        //canvas.setBitmap(bitmap);

        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return newbmp;

//        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
//                maxHeight,
//                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
//                        : Bitmap.Config.RGB_565);
//
//        int w = drawable.getIntrinsicWidth();
//        int h = drawable.getIntrinsicHeight();
//        if (w < maxWidth && h < maxHeight) {
//            return drawableToBitmap(drawable);
//        }
//
//        Bitmap newbmp = null;
//        int showW = w;
//        int showH = h;
//        if (w > maxWidth && h > maxHeight) {
//            Matrix matrix = new Matrix();
//            float scaleWidth = ((float) maxWidth / w);
//            float scaleHeight = ((float) maxHeight / h);
//            matrix.postScale(scaleWidth, scaleHeight);
//            newbmp = Bitmap.createBitmap(bitmap, 0, 0, showW, showH, matrix, true);
//        } else if (w > maxWidth) {
//            showW = maxWidth;
//            newbmp = Bitmap.createBitmap(bitmap, 0, 0, showW, showH);
//        } else if (h > maxHeight) {
//            showH = maxHeight;
//            newbmp = Bitmap.createBitmap(bitmap, 0, 0, showW, showH);
//        }
//        Canvas canvas = new Canvas(newbmp);
//        //canvas.setBitmap(bitmap);
//        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),  drawable.getIntrinsicHeight());
//        drawable.draw(canvas);
//        return newbmp;

    }
}
