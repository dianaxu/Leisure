package com.example.leisure.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.leisure.R;
import com.example.leisure.activity.fragment.ChapterFragment;
import com.example.leisure.db.greendao.ComicBookBean;

import androidx.annotation.Nullable;

/**
 * 章节页
 * <p>
 * 显示漫画的所有章节
 * 缓存标记
 */
public class ChapterActivity extends BaseActivity implements ChapterFragment.OnToolbarListener,
        ChapterFragment.OnChapterSelectedListener {
    public static final String BUNDLE_KEY_COMICBOOKBEAN = "key_comicbookbean";

    private ComicBookBean mBook;

    public static void startChapterActivity(Context context, ComicBookBean bean) {
        Intent intent = new Intent(context, ChapterActivity.class);
        intent.putExtra(BUNDLE_KEY_COMICBOOKBEAN, bean);
        context.startActivity(intent);
    }


    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return TransitionMode.RIGHT;
    }

    @Override
    protected boolean isHasStatusBar() {
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);

        mBook = (ComicBookBean) getIntent().getSerializableExtra(BUNDLE_KEY_COMICBOOKBEAN);

        ChapterFragment fragment = ChapterFragment.newInstance(mBook.lsChapter);
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
        ComicContentActivity.startComicContentActivity(this, mBook);
    }
}
