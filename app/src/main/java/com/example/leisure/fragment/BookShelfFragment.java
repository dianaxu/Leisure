package com.example.leisure.fragment;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.leisure.MainApplication;
import com.example.leisure.R;
import com.example.leisure.activity.ComicContentActivity;
import com.example.leisure.adapter.BaseRecyclerViewAdapter;
import com.example.leisure.adapter.BaseViewHolder;
import com.example.leisure.bean.ComicItemBean;
import com.example.leisure.db.greendao.BookChapter;
import com.example.leisure.db.greendao.BookShelf;
import com.example.leisure.eventbus.Event;
import com.example.leisure.eventbus.EventCode;
import com.example.leisure.glide.ImageLoader;
import com.example.leisure.greenDao.gen.BookChapterDao;
import com.example.leisure.greenDao.gen.BookShelfDao;
import com.example.leisure.widget.textWatcher.MySearchTextWatcher;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 书架页 数据来源于greendao
 * <p>
 * 功能：
 * 1.可以查询漫画书
 * 2.显示加入书架的漫画书列表 sqlite
 */
public class BookShelfFragment extends BaseFragment implements TextView.OnEditorActionListener,
        BaseRecyclerViewAdapter.OnRecyclerViewItemClickListener<BookShelf>, MySearchTextWatcher.OnEmptyListener {

    private EditText mEtSearch;
    private RecyclerView mRvView;

    private BookShelfDao mBookShelfDao;
    private BaseRecyclerViewAdapter mAdapter;
    private List<BookShelf> mLsData = new ArrayList<>();

    public static BookShelfFragment newInstance() {
        BookShelfFragment fragment = new BookShelfFragment();
        return fragment;
    }

    @Override
    protected boolean isRegisterEventBus() {
        return true;
    }

    @Override
    protected void receiveEvent(Event event) {
        // 接受到Event后的相关逻辑
        switch (event.getCode()) {
            case EventCode.BOOKSHELF_ADD_COMIC:
            case EventCode.BOOKSHELF_REMOVE_COMIC:
                getBookShelf();
                break;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBookShelfDao = MainApplication.getDaoSession().getBookShelfDao();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookshelf, container, false);
        mEtSearch = view.findViewById(R.id.et_search);
        mRvView = view.findViewById(R.id.rv_view);

        mEtSearch.setOnEditorActionListener(this);
        MySearchTextWatcher watcher = new MySearchTextWatcher();
        watcher.addOnEmptyListener(this);
        mEtSearch.addTextChangedListener(watcher);

        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        mRvView.setLayoutManager(layout);
        mAdapter = new BaseRecyclerViewAdapter<BookShelf>(getContext(), mLsData) {
            @Override
            public int getResourseId() {
                return R.layout.item_book_shelf;
            }

            @Override
            public void onBindView(BaseViewHolder holder, int position) {
                BookShelf bean = mLsData.get(position);

                holder.setTextOfTextView(R.id.tv_name, bean.getName());
                holder.setTextOfTextView(R.id.tv_latest, bean.getLatest());
                holder.setTextOfTextView(R.id.tv_time, bean.getTime());

                ImageLoader.with(getContext(), bean.getCover(), (ImageView) holder.getView(R.id.iv_cover));
            }
        };
        mRvView.setAdapter(mAdapter);
        mAdapter.addOnRecyclerViewItemClickListener(this);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //获取所有书架上的书
        getBookShelf();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            String value = v.getText().toString().trim();

            //在书架上搜索书
            getBookShelf(value);
            return true;
        }
        return false;
    }

    @Override
    public void onRecyclerViewItemClick(View view, int position, BookShelf bean) {
        //跳转到漫画内容页  直接阅读
        List<ComicItemBean.ChapterBean> lsChapter = new ArrayList<>();
        List<BookChapter> list = MainApplication.getDaoSession().getBookChapterDao().queryBuilder().where(BookChapterDao.Properties.BookId.eq(bean.get_id())).build().list();
        //阅读到的章节 包装数据
//        ComicItemBean.ChapterBean readChapter = null;
        int posi = 0;
        for (int i = 0; i < list.size() - 1; i++) {
            BookChapter bookChapter = list.get(i);
            ComicItemBean.ChapterBean chapter = new ComicItemBean.ChapterBean();
            chapter.num = bookChapter.getNum();
            chapter.url = bookChapter.getUrl();
            lsChapter.add(chapter);
            //阅读到的章节

            if (bean.getReadToChapterUrl() != null && bookChapter.getUrl().contains(bean.getReadToChapterUrl())) {
//                readChapter = chapter;
                posi = i;
            }
        }
//        if (readChapter == null) {
//            readChapter = lsChapter.get(0);
//        }

        ComicContentActivity.startComicContentActivity(getContext(), lsChapter, posi, bean.getName(), bean.getUrl());
    }

    @Override
    public void onEmpty() {
        getBookShelf();
    }

    private void getBookShelf() {
        mLsData = mBookShelfDao.queryBuilder()
                .orderDesc(BookShelfDao.Properties.LastTime)
                .list();
        mAdapter.updateData(mLsData);
    }

    private void getBookShelf(String value) {
        mLsData = mBookShelfDao.queryBuilder()
                .where(BookShelfDao.Properties.Name.like(value))
                .orderDesc(BookShelfDao.Properties.LastTime)
                .list();
        mAdapter.updateData(mLsData);
    }
}
