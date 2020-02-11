package com.example.leisure.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.leisure.R;
import com.example.leisure.bean.ComicItemBean;
import com.example.leisure.fragment.ChapterFragment;

import java.io.Serializable;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * 章节页
 * <p>
 * 显示漫画的所有章节
 * 缓存标记
 */
public class ChapterActivity extends BaseActivity implements ChapterFragment.OnToolbarListener, ChapterFragment.OnChapterSelectedListener {
    public static final String BUNDLE_KEY_CHAPTER = "key_chapter";
    public static final String BUNDLE_KEY_TITLE = "key_title";

    private List<ComicItemBean.ChapterBean> mLsData;
    private String mTitle;
    private String mUrl;

    public static void startChapterActivity(Context context, List<ComicItemBean.ChapterBean> chapterBeanList,
                                            String title, String url) {
        Intent intent = new Intent(context, ChapterActivity.class);
        intent.putExtra(BUNDLE_KEY_CHAPTER, (Serializable) chapterBeanList);
        intent.putExtra(BUNDLE_KEY_TITLE, (Serializable) title);
        intent.putExtra(ChapterFragment.BUNDLE_KEY_HURL1, url);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);

        mLsData = (List<ComicItemBean.ChapterBean>) getIntent().getSerializableExtra(BUNDLE_KEY_CHAPTER);
        mTitle = getIntent().getStringExtra(BUNDLE_KEY_TITLE);
        mUrl = getIntent().getStringExtra(ChapterFragment.BUNDLE_KEY_HURL1);

        ChapterFragment fragment = ChapterFragment.newInstance(mLsData, mTitle, mUrl);
        fragment.addChapterSelectedListener(this);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment, fragment)
                .commit();
    }

    @Override
    public void onBackClick() {
        finish();
    }

    @Override
    public void onChapterSelected(int position) {
        //todo  跳转到漫画内容页
        ComicContentActivity.startComicContentActivity(this, mLsData, position, mTitle, mUrl);
    }
}
