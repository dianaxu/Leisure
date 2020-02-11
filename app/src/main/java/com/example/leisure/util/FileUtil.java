package com.example.leisure.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

import androidx.annotation.Nullable;

import static com.example.leisure.util.Constant.BASE_FILE_NAME;

public class FileUtil {
    public static final int SIZETYPE_B = 1;//获取文件大小单位为B的double值
    public static final int SIZETYPE_KB = 2;//获取文件大小单位为KB的double值
    public static final int SIZETYPE_MB = 3;//获取文件大小单位为MB的double值
    public static final int SIZETYPE_GB = 4;//获取文件大小单位为GB的double值
    private static final String TAG = "FileUtil";

    /**
     * 获取文件指定文件的指定单位的大小
     *
     * @param filePath 文件路径
     * @param sizeType 获取大小的类型1为B、2为KB、3为MB、4为GB
     * @return double值的大小
     */
    public static double getFileOrFilesSize(String filePath, int sizeType) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("获取文件大小", "获取失败!");
        }
        return FormetFileSize(blockSize, sizeType);
    }

    /**
     * 转换文件大小,指定转换的类型
     *
     * @param fileS
     * @param sizeType
     * @return
     */
    private static double FormetFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case SIZETYPE_B:
                fileSizeLong = Double.valueOf(df.format((double) fileS));
                break;
            case SIZETYPE_KB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
                break;
            case SIZETYPE_MB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
                break;
            case SIZETYPE_GB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1073741824));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    private static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 获取指定文件大小
     *
     * @param
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            Log.e("获取文件大小", "文件不存在!");
        }
        return size;
    }

    /**
     * 获取指定文件夹
     *
     * @param f
     * @return
     * @throws Exception
     */
    private static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }


    /**
     * 保存图片到SD卡
     *
     * @param bitmap    图片bitmap对象
     * @param bookId    下载文件保存目录
     * @param imageName 文件名称(不带后缀)
     */
    public static String saveBitmapToFile(Context context, Bitmap bitmap, long bookId, long chapterId, String imageName) throws IOException {
        String fileName = BASE_FILE_NAME + File.separator + bookId + File.separator + chapterId;
        File folder = createFile(context, fileName);
        String savePath = folder.getPath() + File.separator + imageName + ".jpg";

        File file = new File(savePath);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, bos);
        Log.d(TAG, savePath + " 保存成功");
        bos.flush();
        bos.close();
        return savePath;
    }

    /**
     * 创建目录
     *
     * @param comicFileName
     * @return
     */
    public static File createFile(Context context, String comicFileName) {
        String filePath;
        // 如SD卡已存在，则存储；反之存在data目录下
        if (SdcardUtil.hasSdcard()) {
            // SD卡路径
            filePath = Environment.getExternalStorageDirectory()
                    + File.separator + comicFileName;
        } else {
            filePath = context.getCacheDir().getPath() + File.separator
                    + comicFileName;
        }

        File folder = new File(filePath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
    }

    public static String getSavePath(Context context, @Nullable Long bookId, @Nullable Long chapterId, @Nullable String imageName) {
        StringBuffer filePath = new StringBuffer();
        if (SdcardUtil.hasSdcard()) {
            // SD卡路径
            filePath.append(Environment.getExternalStorageDirectory());
        } else {
            filePath.append(context.getCacheDir().getPath());
        }
        filePath.append(File.separator + BASE_FILE_NAME);
        if (bookId != null) {
            filePath.append(File.separator + bookId);
        }
        if (chapterId != null) {
            filePath.append(File.separator + chapterId);
        }
        if (imageName != null) {
            filePath.append(File.separator + imageName + "jpg");
        }

        return filePath.toString();
    }

    public static boolean hasFile(String filePath) {
        return new File(filePath).exists();
    }
}