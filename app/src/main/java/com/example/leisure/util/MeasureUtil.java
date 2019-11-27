package com.example.leisure.util;

import android.graphics.Paint;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class MeasureUtil {

    /**
     * 获取文本的宽度
     *
     * @param text
     * @param textSize
     * @return
     */
    public static float getTextWidth(String text, float textSize) {
        if (TextUtils.isEmpty(text)) {
            return 0;
        }
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        return paint.measureText(text);
    }

    /**
     * 获取文本的高度 或  获取文本行的高度
     *
     * @param text
     * @param textSize
     * @return
     */
    public static  float getTextHeight(String text, float textSize) {
        if (TextUtils.isEmpty(text)) {
            return 0;
        }
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        // 获取画笔默认字体的度量衡
        Paint.FontMetrics metrics = paint.getFontMetrics();
        //返回文字本身的高度
        return metrics.descent - metrics.ascent;
        //返回文本所在行的高度
        // return metrics.bottom - metrics.top;
    }

    /**
     * 指定线性布局的实际高度
     *
     * @param child
     * @return
     */
    public static float getLayoutHeight(View child) {
        LinearLayout llayout = (LinearLayout) child;
        ViewGroup.LayoutParams params = llayout.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int width = ViewGroup.getChildMeasureSpec(0, 0, params.width);
        int height;

        if (params.height > 0) {
            // 高度大于0，说明这是明确的dp数值
            // 按照精确数值的情况计算高度规格
            height = View.MeasureSpec.makeMeasureSpec(params.height, View.MeasureSpec.EXACTLY);
        } else {
            // MATCH_PARENT=-1，WRAP_CONTENT=-2，所以二者都进入该分支
            // 按照不确定的情况计算高度规则
            height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        }
        // 重新进行线性布局的宽高丈量
        llayout.measure(width, height);
        // 获得并返回线性布局丈量之后的高度数值。调用getMeasuredWidth方法可获得宽度数值
        return llayout.getMeasuredHeight();
    }
}
