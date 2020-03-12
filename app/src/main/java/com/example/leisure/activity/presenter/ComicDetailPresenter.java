package com.example.leisure.activity.presenter;

import android.content.Context;
import android.os.Bundle;

import com.example.leisure.MainApplication;
import com.example.leisure.activity.view.IComicDetail;
import com.example.leisure.bean.ComicItemBean;
import com.example.leisure.db.greendao.ComicBookBean;
import com.example.leisure.db.greendao.ComicChapterBean;
import com.example.leisure.eventbus.Event;
import com.example.leisure.eventbus.EventBusUtil;
import com.example.leisure.eventbus.EventCode;
import com.example.leisure.greenDao.gen.ComicBookBeanDao;
import com.example.leisure.greenDao.gen.ComicChapterBeanDao;
import com.example.leisure.greenDao.gen.ComicImageBeanDao;
import com.example.leisure.greenDao.gen.DaoSession;
import com.example.leisure.retrofit.MyComicObserver;
import com.example.leisure.retrofit.RetrofitComicUtils;
import com.example.leisure.retrofit.RxHelper;
import com.example.leisure.util.Constant;

public class ComicDetailPresenter {
    private static final String BUNDLE_KEY_DATA = "key_data";
    private static final String BUNDLE_KEY_ISADDBOOKSHELF = "key_isaddbookshelf";

    private Context mContext;
    private IComicDetail mIView;
    private DaoSession mDaoSession;

    private String mhurl1;
    private String mBookName;
    private String mBookCover;

    private boolean mIsAddBookShelf = false;
    private ComicBookBean mBook;
    private MyComicObserver mObserver;

    public ComicDetailPresenter(Context context, Bundle savedInstanceState, IComicDetail iView) {
        this.mContext = context;
        this.mIView = iView;
        this.mDaoSession = MainApplication.getInstance().getDaoSession();

        initBaseDataByApp(savedInstanceState);
        if (savedInstanceState == null)
            initBaseDataByDB();
    }

    //初始化基本数据
    private void initBaseDataByApp(Bundle savedInstanceState) {
        mhurl1 = MainApplication.getInstance().getInfo(Constant.ComicBaseBundle.BUNDLE_HURL1);
        mBookCover = MainApplication.getInstance().getInfo(Constant.ComicBaseBundle.BUNDLE_COVER);
        mBookName = MainApplication.getInstance().getInfo(Constant.ComicBaseBundle.BUNDLE_NAME);

        if (savedInstanceState != null) {
            mBook = (ComicBookBean) savedInstanceState.getSerializable(BUNDLE_KEY_DATA);
            mIsAddBookShelf = savedInstanceState.getBoolean(BUNDLE_KEY_ISADDBOOKSHELF);
            mhurl1 = savedInstanceState.getString(Constant.ComicBaseBundle.BUNDLE_HURL1);
            mBookCover = savedInstanceState.getString(Constant.ComicBaseBundle.BUNDLE_COVER);
            mBookName = savedInstanceState.getString(Constant.ComicBaseBundle.BUNDLE_NAME);
        }
    }

    /**
     * 从本地库中查询书
     *
     * @return
     */
    private void initBaseDataByDB() {
        getComicByDB();
        mIsAddBookShelf = mBook != null ? true : false;
    }

    public void savedInstanceState(Bundle outState) {
        outState.putSerializable(BUNDLE_KEY_DATA, mBook);
        outState.putBoolean(BUNDLE_KEY_ISADDBOOKSHELF, mIsAddBookShelf);
        outState.putString(Constant.ComicBaseBundle.BUNDLE_HURL1, mhurl1);
        outState.putString(Constant.ComicBaseBundle.BUNDLE_COVER, mBookCover);
        outState.putString(Constant.ComicBaseBundle.BUNDLE_NAME, mBookName);
    }

    //是否已经加入书架
    public boolean isAddBookShelf() {
        return mIsAddBookShelf;
    }

    //获取漫画书详情
    public ComicBookBean getComicBook() {
        return mBook;
    }

    /**
     * 获取漫画分类详情
     */
    public void getComicDetail() {
        if (mBook != null) {
            mIView.updateUI(mBook);
            return;
        }

        if (mIsAddBookShelf) {
            getComicByDB();
            mIView.updateUI(mBook);
        } else {
            getComicByHttp(true);
        }
    }

    //通过本地数据获取漫画信息
    private void getComicByDB() {
        if (mBook == null)
            mBook = mDaoSession.getComicBookBeanDao().queryBuilder()
                    .where(ComicBookBeanDao.Properties.Url.eq(mhurl1))
                    .build()
                    .unique();
    }

    //通过网络获取漫画信息
    private void getComicByHttp(boolean isUpdateUi) {
        mObserver = new MyComicObserver<ComicItemBean>(mContext) {
            @Override
            public void onSuccess(ComicItemBean result) {
                if (result.code.contains("1")) {
                    mIView.updateUI(mBookName, mBookCover);
                    mIView.onFailure(result.message);
                    return;
                }
                mBook = result.data;
                mBook.setUrl(mhurl1);
                mBook.lsChapter = result.list;
                //更新界面
                if (isUpdateUi)
                    mIView.updateUI(result.data);
                else {
                    addComicChapterToDB();
                }
            }

            @Override
            public void onFailure(Throwable e, String errorMsg) {
                mIView.updateUI(mBookName, mBookCover);
                mIView.onFailure(errorMsg);
            }

        };
        RetrofitComicUtils.getApiUrl()
                .getComicItem(mhurl1)
                .compose(RxHelper.observableIO2Main(mContext))
                .subscribe(mObserver);
    }

    //将漫画移除本地数据库
    public void removeComicToDB() {
        //删除本地存储的漫画书
        mDaoSession.getComicBookBeanDao().deleteByKey(mBook.get_id());
        //删除本地存储的漫画书所有章节
        mDaoSession.getComicChapterBeanDao().queryBuilder()
                .where(ComicChapterBeanDao.Properties.BookId.eq(mBook.get_id()))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();
        //删除本地存储的漫画章节下的所有图片链接
        mDaoSession.getComicImageBeanDao().queryBuilder()
                .where(ComicImageBeanDao.Properties.BookId.eq(mBook.get_id()))
                .buildDelete()
                .executeDeleteWithoutDetachingEntities();

        EventBusUtil.sendEvent(new Event(EventCode.BOOKSHELF_REMOVE_COMIC));

        mIsAddBookShelf = false;
        mIView.onRemoveComicToDBSuccess();
    }

    //将漫画加入到本地数据库
    public void addComicToDB() {
        mBook.setLastTime(System.currentTimeMillis());
        mBook.setCacheState(Constant.DownloadState.DOWNLOAD_NOT);
        mBook.setProgress(0);
        long bookId = mDaoSession.getComicBookBeanDao().insert(mBook);
        mBook.set_id(bookId);

        //章节也加入本地数据库
        if (mBook.lsChapter == null) getComicByHttp(false);
        else
            addComicChapterToDB();


        if (bookId >= 0) {
            EventBusUtil.sendEvent(new Event(EventCode.BOOKSHELF_ADD_COMIC));
        }

        mIsAddBookShelf = true;
        mIView.onAddComicToDBSuccess();
    }

    //将章节加入到DB
    private void addComicChapterToDB() {
        for (int i = 0; i < mBook.lsChapter.size(); i++) {
            ComicChapterBean chapterBean = mBook.lsChapter.get(i);
            chapterBean.setCacheState(Constant.DownloadState.DOWNLOAD_NOT);
            chapterBean.setIsCaching(false);
            chapterBean.setCacheCount(0);
            chapterBean.setMaxCount(0);
            chapterBean.setBookId(mBook.get_id());
        }
        mDaoSession.getComicChapterBeanDao().insertInTx(mBook.lsChapter);
    }

    //取消请求网络连接
    public void cancleRequest() {
        if (mObserver != null) {
            mObserver.cancleRequest();
        }
    }

}
