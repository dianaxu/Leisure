package com.example.leisure.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.example.leisure.R;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * 倒计时进度视图
 * <p>
 * 支持种类： 通过xml 设置 progress_style 默认为rect
 * 1.矩形数字倒计时（默认）
 * 2.圆形数字倒计时 （可支持数字是否显示）
 * <p>
 * 统一功能：
 * 可自定义进度 颜色，宽度，每多少毫秒更新进度，做大的时间
 * <p>
 * 注意：
 * 矩形数字倒计时----必显示倒计时数
 * 圆形数字倒计时----可不显示倒计时数，默认是显示的 通过xml中设置show_num
 */
public class CountDownProgressView extends AppCompatTextView {
    public static final int VIEW_STYLE_DEFAULT = 0; //矩形数字倒计时
    public static final int VIEW_STYLE_CIRCLE = 1;  //圆形数字倒计时

    private static final String BUNDLE_PARCELABLE = "bundle_parcelable";
    private static final String BUNDLE_PROGRESS = "bundle_progress";
    private static final String BUNDLE_HAS_CANCEL = "bundle_has_cancel";

    //默认最长时间
    private final int mDefaultMaxTime = 3000;
    //默认时间间隔
    private final int mDefaultTimeInterval = 1000;
    //默认矩形圆角
    private final float mDefaultCorner = 8;
    //默认进度的宽度|矩形边框线宽度
    private final float mDefaultProgressWidth = 4;
    //默认使用矩形数字倒计时
    private final int mDefaultProgressStyle = VIEW_STYLE_DEFAULT;
    //默认使用进度颜色值
    private final int mDefaultProgressColor = Color.parseColor("#1592C4");


    private long mMaxTime = mDefaultMaxTime;
    private long mTimeInterval = mDefaultTimeInterval;
    private float mCorner = mDefaultCorner;
    private float mProgressWidth = mDefaultProgressWidth;
    private int mProgressStyle = mDefaultProgressStyle;
    private int mProgressColor = mDefaultProgressColor;
    //是否取消
    private boolean mHasCancel = false;
    //记录当前的进度
    private long mCurrentProgressTime = 0;
    //显示的默认文本
    private String mText;
    //是否显示数字
    private boolean mHasShowNum = true;

    private Paint mBackPaint;

    //完成监听
    private onTimeFinishListener mOnTimeFinishListener;

    private LooperHandler mLooperHandler = new LooperHandler(getContext());


    /**
     * handler 持有当前 context 的弱引用防止内存泄露
     */
    private class LooperHandler extends Handler {
        WeakReference<Context> mWeakReference;

        public LooperHandler(Context context) {
            this.mWeakReference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            synchronized (mLooperHandler) {
                mCurrentProgressTime = mCurrentProgressTime + mTimeInterval;
                if (mCurrentProgressTime > mMaxTime)
                    mCurrentProgressTime = mMaxTime;
                switch (msg.what) {
                    case 0:
                        setText(mText);
                        break;
                    case 1:
                        setText(mText);
                        invalidate();
                        //需要设置背景进度条
                        break;
                }
                if (mMaxTime - mCurrentProgressTime > 0 && !mHasCancel)
                    mLooperHandler.postDelayed(mRunnable, mTimeInterval);
                else if (mMaxTime - mCurrentProgressTime <= 0) {
                    if (mOnTimeFinishListener != null) mOnTimeFinishListener.onTimeFinish();
                }
            }
        }
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mProgressStyle == VIEW_STYLE_DEFAULT) {
                Message msg = mLooperHandler.obtainMessage(0);
                mLooperHandler.sendMessage(msg);
            } else {
                Message msg = mLooperHandler.obtainMessage(1);
                mLooperHandler.sendMessage(msg);
            }
        }
    };

    @Override
    public void setText(CharSequence text, BufferType type) {
        mText = text == null || TextUtils.isEmpty(text.toString()) ? "" : text.toString();
        int num = (int) Math.ceil((mMaxTime - mCurrentProgressTime) / 1000.0);
        if (num < 0) {
            num = 0;
        }
        text = mText;
        if (mProgressStyle == VIEW_STYLE_DEFAULT) {
            text = mText + num;
        } else {
            if (mHasShowNum)
                text = mText + num;
        }

        super.setText(text, type);
    }

    public void setMaxTime(long time) {
        stopCountDown();
        mMaxTime = time;
        mCurrentProgressTime = 0;
        mHasCancel = false;
        setText(mText);
    }

    public void setOnTimeFinishListener(onTimeFinishListener listener) {
        this.mOnTimeFinishListener = listener;
    }

    public void startCountDown() {
        stopCountDown();
        mCurrentProgressTime = 0;
        mHasCancel = false;
        setText(mText);
        invalidate();
        mLooperHandler.postDelayed(mRunnable, mCurrentProgressTime);
    }

    public void stopCountDown() {
        mHasCancel = true;
        mLooperHandler.removeCallbacks(mRunnable);
    }

    public interface onTimeFinishListener {
        void onTimeFinish();
    }

    public CountDownProgressView(Context context) {
        this(context, null);
    }

    public CountDownProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializePainters(context, attrs);
    }


    private void initializePainters(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CountDownProgressView);

            mMaxTime = typedArray.getInt(R.styleable.CountDownProgressView_max_time, mDefaultMaxTime);
            mTimeInterval = typedArray.getInt(R.styleable.CountDownProgressView_time_interval, mDefaultTimeInterval);
            mCorner = typedArray.getDimension(R.styleable.CountDownProgressView_corner, mDefaultCorner);
            mProgressWidth = typedArray.getDimension(R.styleable.CountDownProgressView_progress_width, mDefaultProgressWidth);
            mProgressStyle = typedArray.getInt(R.styleable.CountDownProgressView_progress_style, mDefaultProgressStyle);
            mProgressColor = typedArray.getColor(R.styleable.CountDownProgressView_progress_color, mDefaultProgressStyle);
            mHasShowNum = typedArray.getBoolean(R.styleable.CountDownProgressView_show_num, true);

            typedArray.recycle();
        }

        setPadding((int) (getPaddingLeft() + mProgressWidth),
                (int) (getPaddingTop() + mProgressWidth),
                (int) (getPaddingRight() + mProgressWidth),
                (int) (getPaddingBottom() + mProgressWidth));
        mText = mText == null || TextUtils.isEmpty(mText) ? getText().toString() : mText;
        setText(mText);

        mBackPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackPaint.setStyle(Paint.Style.STROKE);
        mBackPaint.setStrokeWidth(mProgressWidth);
        mBackPaint.setColor(mProgressColor);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(BUNDLE_PARCELABLE, super.onSaveInstanceState());
        bundle.putLong(BUNDLE_PROGRESS, mCurrentProgressTime);
        bundle.putBoolean(BUNDLE_HAS_CANCEL, mHasCancel);
        return bundle;

    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mCurrentProgressTime = bundle.getLong(BUNDLE_PROGRESS);
            mHasCancel = bundle.getBoolean(BUNDLE_HAS_CANCEL);
            state = bundle.getParcelable(BUNDLE_PARCELABLE);
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if (mProgressStyle == VIEW_STYLE_CIRCLE) {
            setMeasuredDimension(Math.max(width, height), Math.max(width, height));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mProgressStyle == VIEW_STYLE_CIRCLE) {
            int half = (int) (mProgressWidth * 0.5f);
            int startA = (int) (mCurrentProgressTime * 360 / mMaxTime) - 90;
            int sweepA = 270 - startA;

            canvas.drawArc(new RectF(half, half, getMeasuredWidth() - half, getMeasuredHeight() - half),
                    startA, sweepA, false, mBackPaint);
        } else {
            RectF rect = new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight());
            canvas.drawRoundRect(rect, mCorner, mCorner, mBackPaint);
        }
    }

}
