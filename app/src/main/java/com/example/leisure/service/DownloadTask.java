package com.example.leisure.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.example.leisure.bean.ComicContentBean;
import com.example.leisure.db.greendao.BookChapter;
import com.example.leisure.db.greendao.ChapterDetail;
import com.example.leisure.greenDao.gen.ChapterDetailDao;
import com.example.leisure.greenDao.gen.DaoSession;
import com.example.leisure.util.FileUtil;
import com.example.leisure.util.InputStreamUtil;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DownloadTask extends AsyncTask<Object, ChapterDetail, BookChapter> {
    private static final String BASE_URL = "http://api.pingcc.cn/?mhurl2=";
    private static final String TAG = "DownloadTask";

    private Context mContext;
    private BookChapter mChapter;
    private DaoSession mDaoSession;

    private onDownLoadInterface mInterface;

    public BookChapter getBookChapter() {
        return mChapter;
    }

    public interface onDownLoadInterface {
        void onStarting(long chapterId, int imgCount);

        void onProgressUpdate(BookChapter bean, ChapterDetail detail);

        void onFinish(BookChapter bean);
    }

    public DownloadTask(Context context, DaoSession daoSession, onDownLoadInterface face) {
        this.mContext = context;
        this.mDaoSession = daoSession;
        this.mInterface = face;
    }

    //任务开始
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected BookChapter doInBackground(Object... details) {
        mChapter = (BookChapter) details[0];

        List<ChapterDetail> lsImg = mChapter.getMLsChapterImage();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.e(TAG, mChapter.get_id() + "  doInBackground  " + df.format(new Date()));

        if (lsImg.size() == 0) {
            lsImg = getComicContent();
        }
        //没有图片集
        if (lsImg == null) {
            return mChapter;
        }

        //循环图片集
        int length = lsImg.size();
        for (int i = 0; i < length; i++) {
            //任务中途被取消
            if (isCancelled()) {
                return mChapter;
            }
            ChapterDetail bean = lsImg.get(i);
            //图片存在，但是数据库

            //已经缓存
            if (!bean.getIsCaching()) {
                //下载图片
                String filePath = downloadImage(bean.getImg());
                if (filePath != null) {
                    //todo 需要保存path 及 iscaching
                    bean.setPath(filePath);
                    bean.setIsCaching(true);
                    //更新进度
                    publishProgress(bean);
                }
            }
        }

        return mChapter;
    }

    //进度更新
    @Override
    protected void onProgressUpdate(ChapterDetail... values) {
        super.onProgressUpdate(values);
        mInterface.onProgressUpdate(mChapter, values[0]);
    }

    @Override
    protected void onPostExecute(BookChapter chapter) {
        super.onPostExecute(chapter);
        mInterface.onFinish(chapter);
    }

    private String downloadImage(String imgUrl) {
        String savePath = FileUtil.getSavePath(mContext, mChapter.getBookId(), mChapter.get_id(), analysisImageUrl(imgUrl));
        if (FileUtil.hasFile(savePath)) {
            return savePath;
        }

        Bitmap bitmap;
        HttpURLConnection connection;
        InputStream inputStream;
        try {
            connection = (HttpURLConnection) new URL(imgUrl).openConnection(); // 打开一个连接
//                    connection.setConnectTimeout(5000);   // 设置连接时长
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                //保存图片到本地
                savePath = FileUtil.saveBitmapToFile(mContext, bitmap,
                        mChapter.getBookId(), mChapter.get_id(), analysisImageUrl(imgUrl));
                return savePath;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取章节下的漫画图片集  通过网络
     */
    private List<ChapterDetail> getComicContent() {
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
                    //保存图片链接
                    saveImageUrlsToDB(comicContentBean.list, mChapter.getBookId(), mChapter.get_id());

                    List<ChapterDetail> lsDetail = mDaoSession.getChapterDetailDao().queryBuilder()
                            .where(ChapterDetailDao.Properties.ChapterId.eq(mChapter.get_id()))
                            .list();

                    return lsDetail;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //保存图片路径到本地数据库
    private void saveImageUrlsToDB(List<ComicContentBean.ListBean> list, Long bookId, Long
            chapterId) {
        List<ChapterDetail> detailList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ChapterDetail bean = new ChapterDetail();
            bean.setChapterId(chapterId);
            bean.setImg(list.get(i).img);
            bean.setBookId(bookId);
            detailList.add(bean);
        }
        mDaoSession.getChapterDetailDao().insertInTx(detailList);
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
        return imageFileName.replace("_", "")
                .replace(".jpg", "")
                .replace(".png", "");
    }

}
