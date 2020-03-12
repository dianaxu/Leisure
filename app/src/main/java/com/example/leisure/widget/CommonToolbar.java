package com.example.leisure.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.leisure.R;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 图片  搜索 文字 图片
 * 默认不显示搜索
 */
public class CommonToolbar extends FrameLayout implements View.OnClickListener {

    private ImageView mIvLeft, mIvRight;
    private TextView mTvTitle, mTvRight;
    private LinearLayout mLlSearch;
    private TextView mTvSearch;

    private OnLeftDrawableClickListener mLeftListener;
    private OnRightDrawableClickListener mRightListener;
    private OnSearchClickListener mSearchListener;
    private OnRightTextClickListener mRightTextListener;


    public CommonToolbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        inflate(context, R.layout.common_toolbar, this);

        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CommonToolbar);
        Drawable leftDrawable = typedArray.getDrawable(R.styleable.CommonToolbar_toolbar_left_drawable);
        Drawable rightDrawable = typedArray.getDrawable(R.styleable.CommonToolbar_toolbar_right_drawable);

        String text = typedArray.getString(R.styleable.CommonToolbar_toolbar_text);
        int textColor = typedArray.getColor(R.styleable.CommonToolbar_toolbar_text_color, Color.WHITE);
        int textSize = typedArray.getInt(R.styleable.CommonToolbar_toolbar_text_size, 16);

        boolean isShowSearch = typedArray.getBoolean(R.styleable.CommonToolbar_toolbar_search_enable, false);
        String hint = typedArray.getString(R.styleable.CommonToolbar_toolbar_search_hint);
        boolean isShowTitle = typedArray.getBoolean(R.styleable.CommonToolbar_toolbar_text_enable, true);

        String rightText = typedArray.getString(R.styleable.CommonToolbar_toolbar_right_text);

        typedArray.recycle();

        mIvLeft = findViewById(R.id.iv_left);
        mIvRight = findViewById(R.id.iv_right);
        mTvTitle = findViewById(R.id.tv_title);
        mLlSearch = findViewById(R.id.ll_search);
        mTvSearch = findViewById(R.id.tv_search);
        mTvRight = findViewById(R.id.tv_right);

        mIvLeft.setOnClickListener(this);
        mIvRight.setOnClickListener(this);
        mLlSearch.setOnClickListener(this);
        mTvRight.setOnClickListener(this);

        setLeftDrawable(leftDrawable);
        setRightDrawable(rightDrawable);
        setText(text);
        setTextSize(textSize);
        setTextColor(textColor);
        setTitleEnable(isShowTitle);
        setSearchEnable(isShowSearch);
        setRightText(rightText);
        mTvSearch.setText(hint);
    }

    public void setLeftDrawable(Drawable drawable) {
        mIvLeft.setImageDrawable(drawable);
    }

    public void setRightDrawable(Drawable drawable) {
        mIvRight.setImageDrawable(drawable);
    }

    public void setText(String value) {
        mTvTitle.setText(value);
    }

    public void setTextColor(@ColorInt int resId) {
        mTvTitle.setTextColor(resId);
    }

    public void setTextSize(int resId) {
        mTvTitle.setTextSize(resId);
    }

    public void setLeftClickListener(OnLeftDrawableClickListener listener) {
        this.mLeftListener = listener;
    }

    public void setRightClickListener(OnRightDrawableClickListener listener) {
        this.mRightListener = listener;
    }

    public void setSearchClickListener(OnSearchClickListener listener) {
        this.mSearchListener = listener;
    }

    public void setRightTextClickListener(OnRightTextClickListener listener) {
        this.mRightTextListener = listener;
    }

    public void setSearchEnable(boolean enable) {
        this.mLlSearch.setVisibility(enable ? VISIBLE : GONE);
    }

    public void setTitleEnable(boolean enable) {
        this.mTvTitle.setVisibility(enable ? VISIBLE : GONE);
    }

    public void setRightText(String text) {
        this.mTvRight.setText(text);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.iv_left == id) {
            if (mLeftListener != null) {
                mLeftListener.onLeftDrawableClick();
            }
        } else if (R.id.iv_right == id) {
            if (mRightListener != null) {
                mRightListener.onRightDrawableClick();
            }
        } else if (R.id.ll_search == id) {
            if (mSearchListener != null) {
                mSearchListener.onSearchClick();
            }
        } else if (R.id.tv_right == id) {
            if (mRightTextListener != null) {
                mRightTextListener.onRightClick(v);
            }
        }

    }

    public interface OnLeftDrawableClickListener {
        void onLeftDrawableClick();
    }

    public interface OnRightDrawableClickListener {
        void onRightDrawableClick();
    }

    public interface OnSearchClickListener {
        void onSearchClick();
    }

    public interface OnRightTextClickListener {
        void onRightClick(View view);
    }
}
