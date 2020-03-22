package com.example.leisure.activity.presenter;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import com.example.leisure.MainApplication;
import com.example.leisure.activity.view.IComicContent;
import com.example.leisure.bean.ComicContentBean;
import com.example.leisure.bean.ComicItemBean;
import com.example.leisure.db.greendao.ComicBookBean;
import com.example.leisure.db.greendao.ComicChapterBean;
import com.example.leisure.db.greendao.ComicImageBean;
import com.example.leisure.db.greendao.ReadPosition;
import com.example.leisure.eventbus.Event;
import com.example.leisure.eventbus.EventBusUtil;
import com.example.leisure.eventbus.EventCode;
import com.example.leisure.greenDao.gen.ComicBookBeanDao;
import com.example.leisure.greenDao.gen.ComicChapterBeanDao;
import com.example.leisure.greenDao.gen.DaoSession;
import com.example.leisure.greenDao.gen.ReadPositionDao;
import com.example.leisure.retrofit.MyComicObserver;
import com.example.leisure.retrofit.RetrofitComicUtils;
import com.example.leisure.retrofit.RxHelper;
import com.example.leisure.util.Constant;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;

public class ComicContentPresenter {
    private static final String BUNDLE_KEY_READPOSITION = "key_readposition";
    private static final String BUNDLE_KEY_DATA = "key_data";
    private static final String BUNDLE_KEY_ISADDBOOKSHELF = "key_isaddbookshelf";
    private Context mContext;
    private IComicContent mIView;

    private String mBookName;  //漫画名称
    private String mBookUrl;

    private ComicBookBean mBook;
    private boolean mIsAddBookShelf = false;
    private ReadPosition mReadPosition;  //当前的章节信息
    private boolean mIsUpdateAll = true;
    private boolean mIsJump = true;

    private DaoSession mDaoSession;
    private MyComicObserver mObserver;

    public boolean isAddBookShelf() {
        return mIsAddBookShelf;
    }

    public Long getBookId() {
        if (mBook != null)
            return mBook.get_id();
        return null;
    }

    public boolean isBookCached() {
        return mBook.getCacheState() == Constant.DownloadState.DOWNLOADED;
    }

    public String getmBookName() {
        return mBookName;
    }

    public List<ComicChapterBean> getLsChapter() {
        if (mBook == null) return null;
        return mBook.getLsChapter();
    }

    public void setReadPosition(int position) {
        mReadPosition.setPosition(position);
        mBook.lsChapter.get(position).visible = true;
    }

    public int getReadPosition() {
        return mReadPosition.getPosition();
    }

    public void updateLsChapter(List<ComicChapterBean> list) {
        mBook.lsChapter = list;
    }

    public ComicContentPresenter(Context context, Bundle savedInstanceState, ComicBookBean bean, IComicContent iView) {
        this.mContext = context;
        this.mIView = iView;
        this.mBook = bean;
        mDaoSession = MainApplication.getInstance().getDaoSession();

        initBaseDataByApp(savedInstanceState);

        if (savedInstanceState == null)
            initBaseDataByDB();

        //加入暑假中的 DB中没有数据 联网获取网络
        if (mBook != null && mBook.getLsChapter() == null) {
            getComicChaptersByHttp(isAddBookShelf());
        } else if (mBook == null) {
            getComicChaptersByHttp(isAddBookShelf());
        }
    }

    //保存数据
    public void savedInstanceState(Bundle outState) {
        outState.putSerializable(BUNDLE_KEY_DATA, mBook);
        outState.putSerializable(BUNDLE_KEY_READPOSITION, mReadPosition);
        outState.putBoolean(BUNDLE_KEY_ISADDBOOKSHELF, mIsAddBookShelf);
        outState.putString(Constant.ComicBaseBundle.BUNDLE_NAME, mBookName);
        outState.putString(Constant.ComicBaseBundle.BUNDLE_HURL1, mBookUrl);
    }

    //初始化基本数据
    private void initBaseDataByApp(Bundle savedInstanceState) {
        mBookName = MainApplication.getInstance().getInfo(Constant.ComicBaseBundle.BUNDLE_NAME);
        mBookUrl = MainApplication.getInstance().getInfo(Constant.ComicBaseBundle.BUNDLE_HURL1);

        if (savedInstanceState != null) {
            mBook = (ComicBookBean) savedInstanceState.getSerializable(BUNDLE_KEY_DATA);
            mReadPosition = (ReadPosition) savedInstanceState.getSerializable(BUNDLE_KEY_READPOSITION);
            mIsAddBookShelf = savedInstanceState.getBoolean(BUNDLE_KEY_ISADDBOOKSHELF);
            mBookName = savedInstanceState.getString(Constant.ComicBaseBundle.BUNDLE_NAME);
            mBookUrl = savedInstanceState.getString(Constant.ComicBaseBundle.BUNDLE_HURL1);
        }
    }

    //初始化基本数据
    private void initBaseDataByDB() {
        //获取在书架中符合条件的书
        ComicBookBean book = mDaoSession.getComicBookBeanDao().queryBuilder()
                .where(ComicBookBeanDao.Properties.Url.eq(mBookUrl))
                .unique();
        mIsAddBookShelf = book != null ? true : false;
        if (book != null) mBook = book;

        mReadPosition = mDaoSession.getReadPositionDao().queryBuilder()
                .where(ReadPositionDao.Properties.BookUrl.eq(mBookUrl))
                .unique();
        if (mReadPosition == null) {
            mReadPosition = new ReadPosition();
            mReadPosition.setPosition(0);
            mReadPosition.setBookUrl(mBookUrl);
        }
    }

    //通过网络获取漫画章节信息
    private void getComicChaptersByHttp(boolean isSave) {
        mObserver = new MyComicObserver<ComicItemBean>(mContext) {
            @Override
            public void onSuccess(ComicItemBean result) {
                if (result.code.contains("1")) {
                    mIView.onFailure(result.message);
                    return;
                }
                mBook.lsChapter = result.list;
                if (isAddBookShelf() || isSave)
                    saveComicChapterToDB(0, Constant.DownloadState.DOWNLOAD_NOT);

                getComicImagesHttp(getReadPosition());
            }

            @Override
            public void onFailure(Throwable e, String errorMsg) {
                mIView.onFailure(errorMsg);
            }

        };
        RetrofitComicUtils.getApiUrl()
                .getComicItem(mBookUrl)
                .compose(RxHelper.observableIO2Main(mContext))
                .subscribe(mObserver);
    }


    //获取章节下的图片集
    public void getComicImages(int position, boolean isUpdateAll, boolean isJump) {
        this.mIsUpdateAll = isUpdateAll;
        this.mIsJump = isJump;
        //过滤已经是第一章
        if (position != 0) {
            //将上一章已经存在图片集的章节设置为显示
            if ((mIsAddBookShelf && !hasNoDataByDB(position - 1)) ||
                    !mIsAddBookShelf && !hasNoDataByHttp(position - 1))
                mBook.lsChapter.get(position - 1).visible = true;
        }
        //书架中 先进行向数据库查询图片集  若没有数据  则网络下载图片集
        if (mIsAddBookShelf) {
            boolean haveDate = getComicImagesByDB(position);
            if (!haveDate) {
                getComicImagesHttp(position);
            }else{
                if (mIView != null) mIView.updateUI(position, mBook.getLsChapter(), true, false);
            }
        } else {
            if (hasNoDataByHttp(position))
                getComicImagesHttp(position);
            else {
                if (mIView != null) mIView.updateUI(position, mBook.lsChapter, true, false);
            }
        }

    }

    //对数据库的数据  查询用get
    private boolean hasNoDataByDB(int position) {
        if (mBook == null) return true;
        List<ComicChapterBean> lsChapter = mBook.getLsChapter();
        if (lsChapter == null || lsChapter.size() == 0 || position >= lsChapter.size()) return true;
        ComicChapterBean bean = lsChapter.get(position);
        if (bean == null || bean.getList() == null || bean.getList().size() == 0) return true;

        return false;
    }

    //对网络的数据  用list
    private boolean hasNoDataByHttp(int position) {
        if (mBook == null) return true;
        List<ComicChapterBean> lsChapter = mBook.lsChapter;
        if (lsChapter == null || lsChapter.size() == 0 || position >= lsChapter.size()) return true;
        ComicChapterBean bean = lsChapter.get(position);
        if (bean == null || bean.list == null || bean.list.size() == 0) return true;

        return false;
    }

    //从DB中获取图片集
    private boolean getComicImagesByDB(int position) {
        if (mBook != null && mBook.getLsChapter() != null && mBook.getLsChapter().get(position) != null) {
            mBook.getLsChapter().get(position).visible = true;
            mBook.getLsChapter().get(position).getList();
            if (mBook.getLsChapter().get(position).getList() != null && mBook.getLsChapter().get(position).getList().size() > 0) {
                setReadPosition(position);
                mIView.updateUI(position, mBook.lsChapter, mIsUpdateAll, mIsJump);
                return true;
            }
        }
        return false;
    }

    /**
     * 从网络获取图片集
     */
    private void getComicImagesHttp(int position) {
        mObserver = new MyComicObserver<ComicContentBean>(mContext) {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(ComicContentBean result) {
                if (result == null) {
                    mIView.onFailure(result.message);
                    return;
                }
                setReadPosition(position);
                mBook.lsChapter.get(position).list = result.list;
                //更新界面
                mIView.updateUI(position, mBook.lsChapter, mIsUpdateAll, mIsJump);

                //如果是在书架中  则需要进行保存数据
                saveImagesToDB(result.list);
                updateChapter(result.list.size());
                updateReadPositionToDB();
                updateReadPositionIdToBook();
            }

            @Override
            public void onFailure(Throwable e, String errorMsg) {
                mIView.onFailure(errorMsg);
            }
        };

        RetrofitComicUtils.getApiUrl()
                .getComicContent(mBook.lsChapter.get(position).url)
                .compose(RxHelper.observableIO2Main(mContext))
                .subscribe(mObserver);


    }


    //将章节加入到DB
    private void saveComicChapterToDB(int startPosition, int state) {

        List<ComicImageBean> lsImages = new ArrayList<>();
        for (int i = 0; i < mBook.lsChapter.size(); i++) {
            ComicChapterBean chapterBean = mBook.lsChapter.get(i);
            if (i >= startPosition)
                chapterBean.setCacheState(state);
            else
                chapterBean.setCacheState(Constant.DownloadState.DOWNLOAD_NOT);
            chapterBean.setIsCaching(false);
            chapterBean.setCacheCount(0);
            chapterBean.setMaxCount(mBook.lsChapter.get(i).list != null ? mBook.lsChapter.get(i).list.size() : 0);
            chapterBean.setBookId(mBook.get_id());
            long chapterId = mDaoSession.getComicChapterBeanDao().insert(chapterBean);
            chapterBean.set_id(chapterId);
            if (chapterBean.getMaxCount() != 0) {
                int count = mBook.lsChapter.get(i).list.size();
                for (int j = 0; j < count; j++) {
                    ComicImageBean imageBean = mBook.lsChapter.get(i).list.get(j);
                    imageBean.setChapterId(chapterId);
                    imageBean.setBookId(mBook.get_id());
                    imageBean.setIsCaching(false);
                    lsImages.add(imageBean);
                }
            }
        }
        if (lsImages.size() > 0)
            mDaoSession.getComicImageBeanDao().insertInTx(lsImages);
    }

    //保存图片集到DB
    private void saveImagesToDB(List<ComicImageBean> list) {
        if (!mIsAddBookShelf || list == null || list.size() == 0) return;

        Long chapterId = mBook.lsChapter.get(getReadPosition()).get_id();
        if (chapterId == null)
            chapterId = mDaoSession.getComicChapterBeanDao().queryBuilder()
                    .where(ComicChapterBeanDao.Properties.BookId.eq(mBook.get_id()),
                            ComicChapterBeanDao.Properties.Url.eq(mBook.lsChapter.get(getReadPosition()).url))
                    .unique()
                    .get_id();
        long bookId = mBook.get_id();

        for (int i = 0; i < list.size(); i++) {
            list.get(i).setBookId(bookId);
            list.get(i).setChapterId(chapterId);
        }
        mDaoSession.getComicImageBeanDao().insertInTx(list);
    }

    private void updateChapter(int maxCount) {
        if (!mIsAddBookShelf) return;
        ComicChapterBean chapter = mBook.lsChapter.get(getReadPosition());
        chapter.setMaxCount(maxCount);
        mDaoSession.getComicChapterBeanDao().update(chapter);
    }

    /**
     * 保存 书架上的书的阅读位置  到本地数据库
     */
    private void updateReadPositionToDB() {
        //将阅读到的章节更新到数据库
        //阅读的位置
        long readPosition = mDaoSession.getReadPositionDao().insertOrReplace(mReadPosition);
        mReadPosition.set_id(readPosition);
    }

    private void updateReadPositionIdToBook() {
        if (!mIsAddBookShelf) return;
        if (mBook.getReadPositionId() != null) return;
        mBook.setReadPositionId(mReadPosition.get_id());
        mDaoSession.getComicBookBeanDao().update(mBook);
    }

    public void addBookToDB(boolean isDownloadAll) {
        int startPos = getReadPosition();
        if (isDownloadAll) {
            startPos = 0;
        }

        mBook.setLastTime(System.currentTimeMillis());
        mBook.setCacheState(Constant.DownloadState.DOWNLOADING);
        mBook.setProgress(0);
        long bookId = mDaoSession.getComicBookBeanDao().insert(mBook);
        mBook.set_id(bookId);

        //章节也加入本地数据库
        if (mBook.lsChapter == null) getComicChaptersByHttp(true);
        else
            saveComicChapterToDB(startPos, Constant.DownloadState.DOWNLOADING);

        //图片加入

        if (bookId >= 0) {
            EventBusUtil.sendEvent(new Event(EventCode.BOOKSHELF_ADD_COMIC));
        }

        mIsAddBookShelf = true;
        mIView.addComicSuccessToDB(getBookId());
    }

    public void updateState(boolean isDownloadAll) {
        int startPosition = getReadPosition();
        if (isDownloadAll) {
            startPosition = 0;
        }
        mBook.setCacheState(Constant.DownloadState.DOWNLOADING);
        mDaoSession.update(mBook);

        int chapterSize = mBook.getLsChapter().size();
        for (int i = 0; i < chapterSize; i++) {
            if (i >= startPosition && !mBook.getLsChapter().get(i).getIsCaching())
                mBook.getLsChapter().get(i).setCacheState(Constant.DownloadState.DOWNLOADING);
        }
        mDaoSession.getComicChapterBeanDao().updateInTx(mBook.getLsChapter());
    }


    //取消请求网络连接
    public void cancleRequest() {
        if (mObserver != null) {
            mObserver.cancleRequest();
        }
    }
}