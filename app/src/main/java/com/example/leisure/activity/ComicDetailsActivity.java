package com.example.leisure.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leisure.MainApplication;
import com.example.leisure.R;
import com.example.leisure.bean.ComicItemBean;
import com.example.leisure.db.greendao.BookChapter;
import com.example.leisure.db.greendao.BookShelf;
import com.example.leisure.eventbus.Event;
import com.example.leisure.eventbus.EventBusUtil;
import com.example.leisure.eventbus.EventCode;
import com.example.leisure.glide.ImageLoader;
import com.example.leisure.greenDao.gen.BookChapterDao;
import com.example.leisure.greenDao.gen.BookShelfDao;
import com.example.leisure.greenDao.gen.ChapterDetailDao;
import com.example.leisure.retrofit.MyComicObserver;
import com.example.leisure.retrofit.RetrofitComicUtils;
import com.example.leisure.retrofit.RxHelper;
import com.example.leisure.widget.CommonToolbar;

import java.util.ArrayList;
import java.util.List;

/**
 * 漫画详情
 */
public class ComicDetailsActivity extends BaseActivity implements View.OnClickListener {
    public static final String BUNDLE_KEY_URL = "key_url";

    private String mhurl1;


    private CommonToolbar mCtbHeader;
    private ImageView mIvCover;
    private TextView mTvName, mTvAuthor, mTvTag, mTvZk, mTvIntroduce, mTvTime, mTvlatest, mTvInfo;
    private RelativeLayout mRlContent, mRlZj;
    private Button mBtnRead, mBtnAddOrRemove;


    private boolean mIsExpand = false;  //简介是否展开
    private ComicItemBean mData;        //漫画详情信息
    private BookShelf mBook;
    private boolean mIsAddBookShelf = false;


    private MyComicObserver mObserver;

    private BookShelfDao mBookShelfDao;
    private BookChapterDao mBookChapterDao;
    private ChapterDetailDao mChapterDetailDao;


    public static void startComicDetailsActivity(Context context, String url) {
        Intent intent = new Intent(context, ComicDetailsActivity.class);
        intent.putExtra(BUNDLE_KEY_URL, url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_details);

        mCtbHeader = findViewById(R.id.ctb_header);
        mIvCover = findViewById(R.id.iv_cover);
        mTvName = findViewById(R.id.tv_name);
        mTvAuthor = findViewById(R.id.tv_author);
        mTvTag = findViewById(R.id.tv_tag);
        mTvZk = findViewById(R.id.tv_zk);
        mTvIntroduce = findViewById(R.id.tv_introduce);
        mTvTime = findViewById(R.id.tv_time);
        mTvlatest = findViewById(R.id.tv_latest);
        mTvInfo = findViewById(R.id.tv_info);
        mRlContent = findViewById(R.id.rl_content);
        mRlZj = findViewById(R.id.rl_zj);
        mBtnRead = findViewById(R.id.btn_read);
        mBtnAddOrRemove = findViewById(R.id.btn_add_or_remove);


        mCtbHeader.setLeftClickListener(new CommonToolbar.OnLeftDrawableClickListener() {
            @Override
            public void onLeftDrawableClick() {
                finish();
            }
        });
        mTvZk.setOnClickListener(this);
        mRlZj.setOnClickListener(this);
        mBtnRead.setOnClickListener(this);
        mBtnAddOrRemove.setOnClickListener(this);

        mhurl1 = getIntent().getStringExtra(BUNDLE_KEY_URL);
        mBookShelfDao = MainApplication.getInstance().getDaoSession().getBookShelfDao();
        mBookChapterDao = MainApplication.getInstance().getDaoSession().getBookChapterDao();
        mChapterDetailDao = MainApplication.getInstance().getDaoSession().getChapterDetailDao();

        //查询书是否已经加入到书架中
        mIsAddBookShelf = bookStateByLocal();
        mBtnAddOrRemove.setText(mIsAddBookShelf ? "移除书架" : "加入书架");

        getComic();
    }

    @Override
    public void onDestroy() {
        if (mObserver != null) {
            mObserver.cancleRequest();
        }
        super.onDestroy();
    }

    /**
     * 从本地库中查询书
     *
     * @return
     */
    private boolean bookStateByLocal() {
        mBook = mBookShelfDao.queryBuilder().where(BookShelfDao.Properties.Url.eq(mhurl1)).build().unique();
        return mBook != null ? true : false;
    }

    /**
     * 获取漫画分类详情
     */
    private void getComic() {
        mObserver = new MyComicObserver<ComicItemBean>(this) {
            @Override
            public void onSuccess(ComicItemBean result) {
                if (result.code.contains("1")) {
                    mTvInfo.setText(result.message);
                    return;
                }
                mTvInfo.setVisibility(View.GONE);
                mRlContent.setVisibility(View.VISIBLE);
                initViewData(result);
            }

            @Override
            public void onFailure(Throwable e, String errorMsg) {
                Toast.makeText(ComicDetailsActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }

        };
        RetrofitComicUtils.getApiUrl()
                .getComicItem(mhurl1)
                .compose(RxHelper.observableIO2Main(this))
                .subscribe(mObserver);
    }

    private void initViewData(ComicItemBean bean) {
        mData = bean;

        mTvName.setText(mData.data.name);
        mTvAuthor.setText("作者：" + mData.data.author);
        mTvTag.setText("分类：" + mData.data.tag);
        mTvIntroduce.setText(mData.data.introduce);
        mTvTime.setText("最近更新：" + mData.data.time);
        mTvlatest.setText("更新至：" + mData.data.latest);

        ImageLoader.with(this, bean.data.cover, mIvCover);

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.tv_zk == id) {
            expandOrHideIntroduce();
        } else if (R.id.rl_zj == id) {
            //跳转到章节页
            ChapterActivity.startChapterActivity(this, mData.list, mData.data.name, mhurl1);
        } else if (R.id.btn_add_or_remove == id) {
            //已存在书架中  需移除
            if (mIsAddBookShelf) {
                removeComicToLocal();
            } else { //没有在书架中  需要加入
                addComicToLocal();
            }
            //todo 需要加入EventBus 通知书架 数据有更改
        } else if (R.id.btn_read == id) {
            //todo 立即阅读 跳转到漫画详情页   后期需要进行对阅读之后的页面跳转
            ComicContentActivity.startComicContentActivity(this, mData.list, 0, mData.data.name, mhurl1);
        }
    }

    //将漫画加入到本地数据库
    private void addComicToLocal() {
        BookShelf entity = new BookShelf();
        entity.setName(mData.data.name);
        entity.setCover(mData.data.cover);
        entity.setIsTop(false);
        entity.setLatest(mData.data.latest);
        entity.setUrl(mhurl1);
        entity.setTime(mData.data.time);
        entity.setLastTime(System.currentTimeMillis());

        long bookId = mBookShelfDao.insert(entity);

        //章节也加入本地数据库
        List<BookChapter> lsBookChapter = new ArrayList<>();
        for (int i = 0; i < mData.list.size() - 1; i++) {
            ComicItemBean.ChapterBean bean = mData.list.get(i);
            BookChapter cEntity = new BookChapter(null, bookId, bean.num, bean.url, false, 0, 0, 0);
            lsBookChapter.add(cEntity);
        }

        mBookChapterDao.insertInTx(lsBookChapter);

        if (bookId >= 0) {
            Event event = new Event(EventCode.BOOKSHELF_ADD_COMIC);
            EventBusUtil.sendEvent(event);
        }

        mBtnAddOrRemove.setText("移除漫画");
    }

    //将漫画移除本地数据库
    private void removeComicToLocal() {
        //删除本地存储的漫画书
        mBookShelfDao.deleteByKey(mBook.get_id());
        //删除本地存储的漫画书所有章节
        mBookChapterDao.queryBuilder().where(BookChapterDao.Properties.BookId.eq(mBook.get_id())).buildDelete().executeDeleteWithoutDetachingEntities();
        //删除本地存储的漫画章节下的所有图片链接
        mChapterDetailDao.queryBuilder().where(ChapterDetailDao.Properties.BookId.eq(mBook.get_id())).buildDelete().executeDeleteWithoutDetachingEntities();

        EventBusUtil.sendEvent(new Event(EventCode.BOOKSHELF_REMOVE_COMIC));

        mBtnAddOrRemove.setText("加入漫画");
    }


    /**
     * 展开漫画简介 或者 隐藏漫画简介
     */
    private void expandOrHideIntroduce() {
        if (mIsExpand) {
            mIsExpand = false;
            mTvIntroduce.setMaxLines(2);// 收起
            mTvZk.setText("展开");
        } else {
            mIsExpand = true;
            mTvIntroduce.setMaxLines(Integer.MAX_VALUE);// 展开
            mTvZk.setText("隐藏");
        }
    }
}
