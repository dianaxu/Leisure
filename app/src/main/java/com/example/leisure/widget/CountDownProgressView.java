package com.example.leisure.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
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
 * 新增可设置背景颜色 根据设置的是（progress_style=）矩形或者圆形
 * <p>
 * 注意：
 * 矩形数字倒计时----必显示倒计时数
 * 圆形数字倒计时----可不显示倒计时数，默认是显示的 通过xml中设置show_num
 */
public class CountDownProgressView extends AppCompatTextView {
    public static final int VIEW_STYLE_RECT = 0; //矩形数字倒计时
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
    private final int mDefaultProgressStyle = VIEW_STYLE_RECT;
    //默认使用进度颜色值
    private final int mDefaultProgressColor = Color.parseColor("#1592C4");
    private final int mDefaultBackGroupColor = Color.TRANSPARENT;


    private long mMaxTime = mDefaultMaxTime;
    private long mTimeInterval = mDefaultTimeInterval;
    private float mCorner = mDefaultCorner;
    private float mProgressWidth = mDefaultProgressWidth;
    private int mProgressStyle = mDefaultProgressStyle;
    private int mProgressColor = mDefaultProgressColor;
    private Drawable mBackgroup;
    //是否取消
    private boolean mHasCancel = false;
    //记录当前的进度
    private long mCurrentProgressTime = 0;
    //显示的默认文本
    private String mText;
    //是否显示数字
    private boolean mHasShowNum = true;

    private Bitmap mDstBmp;
    private Bitmap mSrcBmp;
    private Paint mOutLinePaint;
    private Paint mBackgroudPaint;

    //完成监听
    private onTimeFinishListener mOnTimeFinishListener;

    private LooperHandler mLooperHandler = new LooperHandler(getContext());
    private Rect mTextRect;
    private Rect mRect;


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
            if (mProgressStyle == VIEW_STYLE_RECT) {
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
        mText = text == null ? "" : text.toString();
        int num = (int) Math.ceil((mMaxTime - mCurrentProgressTime) / 1000.0);
        if (num < 0) {
            num = 0;
        }
        text = mText;
        if (mProgressStyle == VIEW_STYLE_RECT) {
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
            mProgressColor = typedArray.getColor(R.styleable.CountDownProgressView_progress_color, mDefaultProgressColor);
            mBackgroup = typedArray.getDrawable(R.styleable.CountDownProgressView_backgroup_color);
            mHasShowNum = typedArray.getBoolean(R.styleable.CountDownProgressView_show_num, true);

            typedArray.recycle();
        }

        setPadding((int) (getPaddingLeft() + mProgressWidth),
                (int) (getPaddingTop() + mProgressWidth),
                (int) (getPaddingRight() + mProgressWidth),
                (int) (getPaddingBottom() + mProgressWidth));
        setMaxTime(mMaxTime);
        setLines(1);


        mOutLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mOutLinePaint.setStyle(Paint.Style.STROKE);
        mOutLinePaint.setStrokeWidth(mProgressWidth);
        mOutLinePaint.setColor(mProgressColor);

        mBackgroudPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroudPaint.setStyle(Paint.Style.FILL);

        if (getBackground() instanceof ColorDrawable)
            mBackgroudPaint.setColor(((ColorDrawable) getBackground()).getColor());
        else
            mBackgroudPaint.setColor(Color.WHITE);

        mTextRect = new Rect();
        getPaint().getTextBounds(getText().toString(), 0, getText().length(), mTextRect);

        mRect = new Rect();

    }


    private Bitmap makeOval(int w, int h) {
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        c.drawOval(new RectF(0, 0, w, h), mBackgroudPaint);
        return bm;
    }

    private Bitmap makeRect(int w, int h) {
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        c.drawRect(new RectF(0, 0, w, h), mBackgroudPaint);
        return bm;
    }

    public Bitmap DrawableToBitmap(Drawable drawable) {
        // 获取 drawable 长宽
        int width = drawable.getIntrinsicWidth();
        int heigh = drawable.getIntrinsicHeight();

        drawable.setBounds(0, 0, width, heigh);

        // 获取drawable的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565;
        // 创建bitmap
        Bitmap bitmap = Bitmap.createBitmap(width, heigh, config);
        // 创建bitmap画布
        Canvas canvas = new Canvas(bitmap);
        // 将drawable 内容画到画布中
        drawable.draw(canvas);
        return bitmap;
    }

    public Bitmap ZoomDrawable(Drawable drawable, int w, int h) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        // 调用5 中 drawable转换成bitmap
        Bitmap oldbmp = DrawableToBitmap(drawable);

        // 创建操作图片用的Matrix对象
        Matrix matrix = new Matrix();
        // 计算缩放比例
        float sx = ((float) w / width);
        float sy = ((float) h / height);
        // 设置缩放比例
        matrix.postScale(sx, sy);
        // 建立新的bitmap，其内容是对原bitmap的缩放后的图
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
                matrix, true);
        return newbmp;
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

    private int getMeasureWidth(int widthMeasureSpec, int textWidth) {
        int width = getMeasuredWidth();
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            return width;
        } else {
            width = getMaxTextSpec();
            return width + getPaddingLeft() + getPaddingRight();
        }
    }

    private int getMeasureHeight(int heightMeasureSpec, int textHeight) {
        int height = getMeasuredHeight();
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            return height;
        } else {
            height = getMaxTextSpec();
            return height + getPaddingTop() + getPaddingBottom();
        }
    }

    private int getMaxTextSpec() {
        return Math.max(Math.max(mTextRect.width(), mRect.width()), Math.max(mTextRect.height(), mRect.height()));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        getPaint().getTextBounds(getText().toString(), 0, getText().length(), mRect);
        int width = getMeasureWidth(widthMeasureSpec, mRect.width());
        int height = getMeasureHeight(heightMeasureSpec, mRect.height());

        if (mProgressStyle == VIEW_STYLE_CIRCLE) {
            width = Math.min(width, height);
            height = Math.min(width, height);
        }

        if (getBackground() instanceof BitmapDrawable)
            mSrcBmp = ZoomDrawable(getBackground(), width, height);
        else
            mSrcBmp = makeRect(width, height);

        if (mProgressStyle == VIEW_STYLE_RECT)
            mDstBmp = makeRect(width, height);
        else
            mDstBmp = makeOval(width, height);

        setMeasuredDimension(width, height);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);

        int layerID = canvas.saveLayer(0, 0, getWidth(), getHeight(), mBackgroudPaint, Canvas.ALL_SAVE_FLAG);

        canvas.drawBitmap(mSrcBmp, 0, 0, mBackgroudPaint);
        mBackgroudPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        canvas.drawBitmap(mDstBmp, 0, 0, mBackgroudPaint);
        mBackgroudPaint.setXfermode(null);

        canvas.restoreToCount(layerID);

        if (mProgressStyle == VIEW_STYLE_CIRCLE) {
            int half = (int) (mProgressWidth * 0.5f);
            int startA = (int) (mCurrentProgressTime * 360 / mMaxTime) - 90;
            int sweepA = 270 - startA;
            RectF rectF = new RectF(half, half, getMeasuredWidth() - half, getMeasuredHeight() - half);
            canvas.drawArc(rectF, startA, sweepA, false, mOutLinePaint);

        } else {
            RectF rect = new RectF(0, 0, getMeasuredWidth(), getMeasuredHeight());
            canvas.drawRoundRect(rect, mCorner, mCorner, mOutLinePaint);
        }

        getPaint().getTextBounds(getText().toString(), 0, getText().length(), mRect);
        Paint.FontMetricsInt fontMetrics = getPaint().getFontMetricsInt();
        int baseline = (getMeasuredHeight() - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        canvas.drawText(getText().toString(), getMeasuredWidth() / 2 - mRect.width() / 2 - mRect.left, baseline, getPaint());

    }

}
