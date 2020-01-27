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
 * 视频页
 * 1.实现xml
 * 2.视频列表展示
 * 3.视频课直接播放
 * 4.实现视频全屏播放
 * 5.检测网络  wifi则自动播放  流量则不播放
 * 6.后期支持可分享到微信，QQ
 *
 *
 * 暂时直接显示视频 http://baobab.kaiyanapp.com/api/v4/discovery/category?start=0&num=10
 * 后期再进行分类显示视频
 */
public class VideoFragment extends Fragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        return view;
    }
}
