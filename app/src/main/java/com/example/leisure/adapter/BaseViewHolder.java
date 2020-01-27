package com.example.leisure.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.collection.SparseArrayCompat;
import androidx.recyclerview.widget.RecyclerView;

public class BaseViewHolder extends RecyclerView.ViewHolder {

    private SparseArrayCompat<View> mViews = new SparseArrayCompat<>();

    protected BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public static BaseViewHolder getInstance(Context context, @LayoutRes int layoutId, ViewGroup parent) {
        View itemView = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new BaseViewHolder(itemView);
    }


    public View getView(@IdRes int idRes) {
        View v = mViews.get(idRes);
        if (v == null) {
            v = itemView.findViewById(idRes);
            mViews.put(idRes, v);
        }
        return v;
    }

    public BaseViewHolder setTextOfTextView(@IdRes int idRes, CharSequence text) {
        if (getView(idRes) instanceof TextView) {
            ((TextView) getView(idRes)).setText(text);
        }
        return this;
    }

    public BaseViewHolder setBackgroundOfView(@IdRes int idRes, @DrawableRes int drawableRes) {
        getView(idRes).setBackgroundResource(drawableRes);
        return this;
    }

    public BaseViewHolder setColorOfTextView(@IdRes int idRes, @ColorInt int color) {
        if (getView(idRes) instanceof TextView) {
            ((TextView) getView(idRes)).setTextColor(color);
        }
        return this;
    }

    public BaseViewHolder setImageOfImageView(@IdRes int idRes, @DrawableRes int drawableRes) {
        if (getView(idRes) instanceof ImageView) {
            ((ImageView) getView(idRes)).setImageResource(drawableRes);
        }
        return this;
    }

    public BaseViewHolder setImageOfImageView(@IdRes int idRes, Drawable drawable) {
        if (getView(idRes) instanceof ImageView) {
            ((ImageView) getView(idRes)).setImageDrawable(drawable);
        }
        return this;
    }

    public BaseViewHolder setItemListener(View.OnClickListener listener, int position) {
        itemView.setTag(position);
        itemView.setOnClickListener(listener);
        return this;
    }
}
