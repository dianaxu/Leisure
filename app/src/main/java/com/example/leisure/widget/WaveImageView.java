package com.example.leisure.widget;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.example.leisure.util.DensityUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@SuppressLint("AppCompatCustomView")
public class WaveImageView extends View {
    private static final String TAG = "WaveImageView";
    private Path mPath;
    private Paint mPaint;
    private int mWidth;
    private int mHeight;
    private int mWaveColor = Color.parseColor("#50FF3891");
    private int mWaveHeight;
    private int mWaveDx;
    private int dx;
    private float progress;
    private ValueAnimator valueAnimator;


    public WaveImageView(@NonNull Context context) {
        super(context, null);
        init(null);
    }

    public WaveImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        init(attrs);
    }

    public WaveImageView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
//        mPaint.setColor(getResources().getColor(R.color.colorAccent));
        mPaint.setColor(mWaveColor);
        mPaint.setStyle(Paint.Style.FILL);
        mWaveDx = getResources().getDisplayMetrics().widthPixels;
        mPath = new Path();

        initAnimation();
    }

    private void initAnimation() {
        valueAnimator = ValueAnimator.ofInt(0, mWaveDx);
        valueAnimator.setDuration(2000);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //水平方向的偏移量
                dx = (int) animation.getAnimatedValue();
                invalidate();
            }

        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //控件的宽高
        mWidth = measureView(widthMeasureSpec, mWaveDx);
        mHeight = measureView(heightMeasureSpec, 300);
        //水波的高度
        mWaveHeight = DensityUtil.dip2px(getContext(), 16);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (valueAnimator.isRunning()) {
            mPath.reset();
            mPath.moveTo(-mWaveDx + dx, mHeight * progress);
            Log.e(TAG, "onDraw: " + mHeight + "--->" + mHeight * progress);

            for (int i = -mWaveDx; i < getWidth() + mWaveDx; i += mWaveDx) {
                mPath.rQuadTo(mWaveDx / 4, -mWaveHeight, mWaveDx / 2, 0);
                mPath.rQuadTo(mWaveDx / 4, mWaveHeight, mWaveDx / 2, 0);

            }

            //绘制封闭的区域
            mPath.lineTo(mWidth, mHeight);
            mPath.lineTo(0, mHeight);
            mPath.close();
            canvas.drawPath(mPath, mPaint);
        } else {

            Rect rect = new Rect(0, (int) (mHeight * progress), mWidth, mHeight);
            canvas.drawRect(rect, mPaint);
        }
    }

    public void setWaveColor(int waveColor) {
        this.mWaveColor = waveColor;
        mPaint.setColor(mWaveColor);

    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    public boolean isRunning() {
        return valueAnimator.isRunning();
    }

    public void startAnimation() {
        valueAnimator.start();
    }

    public void stopAnimation() {
        valueAnimator.end();
    }

    /**
     * 用于View的测量
     *
     * @param measureSpec 测量模式和大小
     * @param defaultSize 默认的大小
     * @return
     */
    public int measureView(int measureSpec, int defaultSize) {
        int measureSize;
        //获取用户指定的大小以及模式
        int mode = MeasureSpec.getMode(measureSpec);
        int size = MeasureSpec.getSize(measureSpec);
        //根据模式去返回大小
        if (mode == MeasureSpec.EXACTLY) {
            //精确模式（指定大小以及match_parent）直接返回指定的大小
            measureSize = size;
        } else {
            //UNSPECIFIED模式、AT_MOST模式（wrap_content）的话需要提供默认的大小
            measureSize = defaultSize;
            if (mode == MeasureSpec.AT_MOST) {
                //AT_MOST（wrap_content）模式下，需要取测量值与默认值的最小值
                measureSize = Math.min(measureSize, size);
            }
        }
        return measureSize;
    }

    private int measureSize(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = 300;
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;

    }

}
