package com.example.leisure.activity.fragment;

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
import com.example.leisure.activity.adapter.BaseRecyclerViewAdapter;
import com.example.leisure.activity.adapter.BaseViewHolder;
import com.example.leisure.db.greendao.ComicBookBean;
import com.example.leisure.db.greendao.ComicChapterBean;
import com.example.leisure.eventbus.Event;
import com.example.leisure.eventbus.EventCode;
import com.example.leisure.glide.ImageLoader;
import com.example.leisure.greenDao.gen.ComicBookBeanDao;
import com.example.leisure.greenDao.gen.ComicChapterBeanDao;
import com.example.leisure.util.Constant;
import com.example.leisure.widget.SpacesItemDecoration;
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
        BaseRecyclerViewAdapter.OnItemClickListener<ComicBookBean>, MySearchTextWatcher.OnEmptyListener {

    private View mView;
    private EditText mEtSearch;
    private RecyclerView mRvView;

    private ComicBookBeanDao mComicBookBeanDao;
    private BaseRecyclerViewAdapter mAdapter;
    private List<ComicBookBean> mLsData = new ArrayList<>();

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
            case EventCode.BOOKSHELF_UPDATE_LAST_TIME:
                getComicBookBean();
                break;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mComicBookBeanDao = MainApplication.getInstance().getDaoSession().getComicBookBeanDao();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_bookshelf, container, false);
        mEtSearch = mView.findViewById(R.id.et_search);
        mRvView = mView.findViewById(R.id.rv_view);

        mEtSearch.setOnEditorActionListener(this);
        MySearchTextWatcher watcher = new MySearchTextWatcher();
        watcher.addOnEmptyListener(this);
        mEtSearch.addTextChangedListener(watcher);

        initRecyclerView();
        return mView;
    }

    private void initRecyclerView() {
        LinearLayoutManager layout = new LinearLayoutManager(getContext());
        mRvView.setLayoutManager(layout);
        mRvView.addItemDecoration(new SpacesItemDecoration(16));
        mAdapter = new BaseRecyclerViewAdapter<ComicBookBean>(getContext(), mLsData) {
            @Override
            public int getResourseId() {
                return R.layout.item_book_shelf;
            }

            @Override
            public void onBindView(BaseViewHolder holder, int position) {
                ComicBookBean bean = mLsData.get(position);

                holder.setText(R.id.tv_name, bean.getName());
                holder.setText(R.id.tv_latest, bean.getLatest());
                holder.setText(R.id.tv_time, bean.getTime());

                ImageLoader.with(getContext(), bean.getCover(), (ImageView) holder.getView(R.id.iv_cover));
            }
        };
        mRvView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //获取所有书架上的书
        getComicBookBean();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            String value = v.getText().toString().trim();

            //在书架上搜索书
            getComicBookBean(value);
            return true;
        }
        return false;
    }

    @Override
    public void onItemClick(View view, int position, ComicBookBean bean) {
        //跳转到漫画内容页  直接阅读
        List<ComicChapterBean> list = MainApplication.getInstance().getDaoSession().getComicChapterBeanDao().queryBuilder()
                .where(ComicChapterBeanDao.Properties.BookId.eq(bean.get_id()))
                .build()
                .list();
        //阅读到的章节 包装数据
//        ComicChapterBean readChapter = null;
//        int posi = 0;
//        for (int i = 0; i < list.size() - 1; i++) {
//            BookChapter bookChapter = list.get(i);
//            ComicChapterBean chapter = new ComicChapterBean();
//            chapter.num = bookChapter.getNum();
//            chapter.url = bookChapter.getUrl();
//            lsChapter.add(chapter);
//            //阅读到的章节
//
//            if (bean.getReadToChapterUrl() != null && bookChapter.getUrl().contains(bean.getReadToChapterUrl())) {
////                readChapter = chapter;
//                posi = i;
//            }
//        }
//        if (readChapter == null) {
//            readChapter = lsChapter.get(0);
//        }

        MainApplication.getInstance().saveInfo(Constant.ComicBaseBundle.BUNDLE_HURL1, bean.getUrl());
        MainApplication.getInstance().saveInfo(Constant.ComicBaseBundle.BUNDLE_NAME, bean.getName());
        ComicContentActivity.startComicContentActivity(getContext(), bean);
    }

    @Override
    public void onEmpty() {
        getComicBookBean();
    }

    private void getComicBookBean() {
        mLsData = mComicBookBeanDao.queryBuilder()
                .orderDesc(ComicBookBeanDao.Properties.LastTime)
                .list();
        mAdapter.updateData(mLsData);
    }

    private void getComicBookBean(String value) {
        mLsData = mComicBookBeanDao.queryBuilder()
                .where(ComicBookBeanDao.Properties.Name.like(value))
                .orderDesc(ComicBookBeanDao.Properties.LastTime)
                .list();
        mAdapter.updateData(mLsData);
    }
}
