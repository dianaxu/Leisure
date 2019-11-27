package com.example.leisure.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.leisure.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

/**
 * 左侧划页
 */
public class LeftMenuFragment extends Fragment implements View.OnClickListener {

    private AppCompatImageView mIvClose, mIvHead;
    private TextView mTvName;

    private OnCloseMenuListener mListener;

//    @Override
//    public void onAttach(Context context) {
//        if (context instanceof OnCloseMenuListener) {
//            mListener = (OnCloseMenuListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnCloseMenuListener");
//        }
//        super.onAttach(context);
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.left_menu, container, false);
        initView(view);

        return view;
    }

    private void initView(View view) {
        mTvName = view.findViewById(R.id.tv_name);
        mIvHead = view.findViewById(R.id.iv_head);
        mIvClose = view.findViewById(R.id.iv_close_menu);

        mIvHead.setOnClickListener(this);
        mIvClose.setOnClickListener(this); //关闭侧滑栏按钮
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (R.id.iv_close_menu == id) {
            if (mListener != null) {
                mListener.closeMenu();
            }
        } else if (R.id.iv_head == id) {

            //todo 跳转到个人中心
        }
    }

    public void setOnCloseMenuListener(OnCloseMenuListener listener) {
        this.mListener = listener;
    }

    public interface OnCloseMenuListener {
        void closeMenu();
    }
}
