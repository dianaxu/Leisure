package com.example.leisure.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.leisure.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * 推荐页
 */
public class RecommendFragment extends Fragment {
//    public static final String BUNDLE_KEY_TITLE = "key_title";
//    private TextView mTvTitle;
//    private String mTitle;

//    public static RecommendFragment newInstance(String title) {
//        Bundle bundle = new Bundle();
//        bundle.putString(BUNDLE_KEY_TITLE, title);
//        RecommendFragment fragment = new RecommendFragment();
//        fragment.setArguments(bundle);
//        return fragment;
//    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Bundle bundle = getArguments();
//        if (bundle != null) {
//            mTitle = bundle.getString(BUNDLE_KEY_TITLE, "");
//        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);
        return view;
    }
}
