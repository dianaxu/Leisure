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
public class ChapterActivity extends BaseActivity implements ChapterFragment.OnToolbarListener {
    public static final String BUNDLE_KEY_CHAPTER = "key_chapter";
    public static final String BUNDLE_KEY_TITLE = "key_title";

    private List<ComicItemBean.ChapterBean> mLsData;
    private String mTitle;

    public static void startChapterActivity(Context context, List<ComicItemBean.ChapterBean> chapterBeanList,
                                            String title) {
        Intent intent = new Intent(context, ChapterActivity.class);
        intent.putExtra(BUNDLE_KEY_CHAPTER, (Serializable) chapterBeanList);
        intent.putExtra(BUNDLE_KEY_TITLE, (Serializable) title);
        context.startActivity(intent);
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);

        mLsData = (List<ComicItemBean.ChapterBean>) getIntent().getSerializableExtra(BUNDLE_KEY_CHAPTER);
        mTitle = getIntent().getStringExtra(BUNDLE_KEY_TITLE);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment, ChapterFragment.newInstance(mLsData, mTitle))
                .commit();
    }

    @Override
    public void onBackClick() {
        finish();
    }
}
