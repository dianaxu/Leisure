package com.example.leisure.activity.fragment;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.leisure.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * 漫画分类 数据来源于网络
 * <p>
 * 功能：
 * 1.显示分类项tabbar ok
 * 2.分类项可进行编辑
 * 3.具体分类的漫画书 ok
 */
public class ComicFragment extends Fragment {

    private TabLayout mTabLayout;
    private ViewPager mVpMain;

    private List<String> mTabTitleKeys = new ArrayList<>();
    private List<String> mTabTitleValues = new ArrayList<>();
    private SparseArray<Fragment> mTabFragments = new SparseArray();

    public static ComicFragment newInstance() {
        ComicFragment fragment = new ComicFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comic, container, false);
//        mCtbHeader = view.findViewById(R.id.ctb_header);
        mTabLayout = view.findViewById(R.id.tab_layout);
        mVpMain = view.findViewById(R.id.vp_main);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolBar();
        initViewPager();
    }

    private void initToolBar() {
//        mCtbHeader.setLeftClickListener(new CommonToolbar.OnLeftDrawableClickListener() {
//            @Override
//            public void onLeftDrawableClick() {
//                //Todo 显示左侧页面
//            }
//        });

        //初始化漫画分类列
        mTabTitleKeys = Arrays.asList(getResources().getStringArray(R.array.tab_title_keys));
        mTabTitleValues = Arrays.asList(getResources().getStringArray(R.array.tab_title_values));

        mTabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View customView = tab.getCustomView();
                if (customView == null) {
                    customView = LayoutInflater.from(getContext()).inflate(R.layout.tab_layout_text, null);
                }
                ((TextView) customView).setTextAppearance(getActivity(), R.style.TabLayoutTextSize);
                tab.setCustomView(customView);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View customView = tab.getCustomView();
                if (customView == null) {
                    customView = LayoutInflater.from(getContext()).inflate(R.layout.tab_layout_text, null);
                }
                ((TextView) customView).setTextAppearance(getActivity(), R.style.TabLayoutTextSize_two);
                tab.setCustomView(customView);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    //初始化viewpager
    private void initViewPager() {
        mVpMain.setOffscreenPageLimit(3);
        mVpMain.setAdapter(new ComicViewPager(getFragmentManager()));
        mTabLayout.setupWithViewPager(mVpMain);
    }


    protected class ComicViewPager extends FragmentPagerAdapter {

        public ComicViewPager(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return ComicItemFragment.newInstance(mTabTitleKeys.get(position));
        }

        @Override
        public int getCount() {
            return mTabTitleValues.size();
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            mTabFragments.put(position, fragment);
            return super.instantiateItem(container, position);
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            mTabFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return mTabTitleValues.get(position);
        }
    }
}
