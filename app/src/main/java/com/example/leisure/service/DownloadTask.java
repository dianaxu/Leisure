package com.example.leisure.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.util.Log;

import com.example.leisure.bean.ComicContentBean;
import com.example.leisure.db.greendao.ComicChapterBean;
import com.example.leisure.db.greendao.ComicImageBean;
import com.example.leisure.greenDao.gen.ComicImageBeanDao;
import com.example.leisure.greenDao.gen.DaoSession;
import com.example.leisure.retrofit.RxExceptionUtil;
import com.example.leisure.util.FileUtil;
import com.example.leisure.util.InputStreamUtil;
import com.example.leisure.util.NetworkUtil;
import com.example.leisure.util.ScreenInfoUtils;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.example.leisure.util.ImageUtil.analysisImageUrl;


public class DownloadTask extends AsyncTask<Object, ComicImageBean, ComicChapterBean> {
    private static final String BASE_URL = "http://api.pingcc.cn/?mhurl2=";
    private static final String TAG = "DownloadTask";

    private Context mContext;
    private DaoSession mDaoSession;
    private ComicChapterBean mChapter;
    private long mBookId;
    private int mPxWidth;
    private boolean mIsCancel = false;
    private boolean mIsConnectionWifi = true;
    private onDownLoadInterface mInterface;

    public void cancelTask() {
        mIsCancel = true;
        this.cancel(true);
    }

    public long getBookId() {
        return mBookId;
    }

    public interface onDownLoadInterface {
        void onStartingChapter(long chapterId);

        void onSaveImageUrlsToDB(ComicChapterBean bean);

        void onUpdateProgressChapter(ComicChapterBean bean, ComicImageBean detail);

        void onFinishChapter(ComicChapterBean bean);

        void onSuccessCancelChapter(ComicChapterBean bean);

        void onFailChapter(ComicChapterBean chapter);

        void onNotConnWifi();
    }

    public DownloadTask(Context context, DaoSession daoSession, long bookId, onDownLoadInterface face) {
        this.mContext = context;
        this.mDaoSession = daoSession;
        this.mBookId = bookId;
        this.mInterface = face;
        this.mPxWidth = ScreenInfoUtils.getWindoeWidth(context);
    }

    //任务开始
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected ComicChapterBean doInBackground(Object... details) {
        mChapter = (ComicChapterBean) details[0];
        mInterface.onStartingChapter(mChapter.get_id());
        if (mIsCancel) {
            mInterface.onSuccessCancelChapter(mChapter);
            return mChapter;
        }

        List<ComicImageBean> lsImg = mChapter.getList();

        if (lsImg.size() == 0) {
            lsImg = getImageUrlsByHttp();
        }
        //没有图片集
        if (lsImg == null) {
            mInterface.onFailChapter(mChapter);
            return mChapter;
        }

        //循环图片集
        int length = lsImg.size();
        mChapter.setMaxCount(length);
        mChapter.setCacheCount(0);
        for (int i = 0; i < length; i++) {
            if (!checkNetworkWifi()) return mChapter;
            //任务中途被取消
            if (mIsCancel) {
                mInterface.onSuccessCancelChapter(mChapter);
                return mChapter;
            }

            ComicImageBean bean = lsImg.get(i);
            //图片存在，但是数据库
            // 没有缓存
            if (bean.getIsCaching()) {
                mChapter.setCacheCount(mChapter.getCacheCount() + 1);
                continue;
            }

            //下载图片
            String filePath = downloadImage(bean.getImg());
            if (filePath != null) {
                bean.setPath(filePath);
                bean.setIsCaching(true);
                mChapter.setCacheCount(mChapter.getCacheCount() + 1);
                //更新进度
                publishProgress(bean);
            }
        }
        return mChapter;
    }

    //进度更新
    @Override
    protected void onProgressUpdate(ComicImageBean... values) {
        super.onProgressUpdate(values);
        mInterface.onUpdateProgressChapter(mChapter, values[0]);
    }

    @Override
    protected void onPostExecute(ComicChapterBean chapter) {
        super.onPostExecute(chapter);
        if (mIsConnectionWifi) {
            mInterface.onFinishChapter(chapter);
        } else
            mInterface.onNotConnWifi();
    }

    private boolean checkNetworkWifi() {
        if (!NetworkUtil.isConnectedByWifi(mContext)) {
            //将所有的任务全部停止
            return mIsConnectionWifi = false;
        }
        return mIsConnectionWifi = true;
    }

    /**
     * 获取章节下的漫画图片集  通过网络
     */
    private List<ComicImageBean> getImageUrlsByHttp() {
        if (!checkNetworkWifi()) {
            return null;
        }

        String url = BASE_URL + mChapter.getUrl();
        HttpURLConnection connection;
        InputStream inputStream;
        try {
            connection = (HttpURLConnection) new URL(url).openConnection(); // 打开一个连接
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();

                String result = InputStreamUtil.readStreamToString(inputStream);
                inputStream.close();

                Gson gson = new Gson();
                ComicContentBean comicContentBean = gson.fromJson(result, ComicContentBean.class);
                if (comicContentBean.code.contains("0") && comicContentBean.list.size() > 0) {
                    Log.e(TAG, mChapter.get_id() + " getComicContent: " + result);
                    mChapter.setMaxCount(comicContentBean.list.size());

                    saveImageUrlsToDB(comicContentBean.list, mChapter.getBookId(), mChapter.get_id());
                    List<ComicImageBean> list = mDaoSession.getComicImageBeanDao().queryBuilder()
                            .where(ComicImageBeanDao.Properties.ChapterId.eq(mChapter.get_id()))
                            .list();
                    mChapter.list = list;
                    return mChapter.getList();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 下载图片到磁盘
     *
     * @param imgUrl
     * @return
     */
    private String downloadImage(String imgUrl) {
        //解析图片名称出问题
        String imageName = analysisImageUrl(imgUrl);
        if (imageName == null) return imageName;

        //图片名称解析完  存放的位置
        String savePath = FileUtil.getSavePath(mContext, mChapter.getBookId(), mChapter.get_id(), imageName);
        if (FileUtil.hasFile(savePath)) {
            return savePath;
        }

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
                            inputStream.close();


                            int width = bitmap.getWidth();
                            int height = bitmap.getHeight();
                            Matrix matrix = new Matrix();
                            //也可以按两者之间最大的比例来设置放大比例，这样不会是图片压缩
                            float scale = caleScale(width);

                            matrix.postScale(scale, scale); // 长和宽放大缩小的比例
                            Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, width,
                                    height, matrix, true);

                            //保存图片到本地
                            savePath = FileUtil.saveBitmapToFile(mContext, resizeBmp,
                                    mChapter.getBookId(), mChapter.get_id(), analysisImageUrl(imgUrl));
                            return savePath;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        } catch (MalformedURLException e) {
            String msg = RxExceptionUtil.exceptionHandler(e);
            e.printStackTrace();
        } catch (IOException e) {
            String msg = RxExceptionUtil.exceptionHandler(e);
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private float caleScale(int width) {
        if (width > mPxWidth) return (float) mPxWidth / width;
        return 1;
    }

    /*
     * 得到图片字节流 数组大小
     * */
    private byte[] readStream(InputStream inStream) throws Exception {
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

    //保存图片路径到本地数据库
    private void saveImageUrlsToDB(List<ComicImageBean> list, Long bookId, Long
            chapterId) {
        List<ComicImageBean> detailList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ComicImageBean bean = new ComicImageBean();
            bean.setChapterId(chapterId);
            bean.setImg(list.get(i).getImg());
            bean.setBookId(bookId);
            detailList.add(bean);
        }
        mDaoSession.getComicImageBeanDao().insertInTx(detailList);
    }
}
