package com.example.leisure;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.leisure.fragment.FictionFragment;
import com.example.leisure.fragment.LeftMenuFragment;
import com.example.leisure.fragment.MusicFragment;
import com.example.leisure.fragment.RecommendFragment;
import com.example.leisure.fragment.VideoFragment;
import com.example.leisure.util.ScreenInfoUtils;
import com.example.leisure.widget.GradualTabView;
import com.nineoldandroids.view.ViewHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;


public class MainActivity extends AppCompatActivity {
    public static final String BUNDLE_KEY_POS = "bundle_key_pos";
    private final String TAG = "MainActivity";

    private View mStatusbar;
    private Toolbar mToolbar;
    private TextView mTvTitle;
    private DrawerLayout mDrawerLayout;
    private View mLeftMenu;
    private ViewPager mVpContent;
    private GradualTabView mGtvRecommend, mGtvFiction, mGtvMusic, mGtvVideo;

    private List<String> mTabTitles = new ArrayList<>(Arrays.asList("推荐", "小说", "音乐", "视频"));
    private SparseArray<Fragment> mTabFragments = new SparseArray();
    private List<GradualTabView> mGradualTabViews = new ArrayList<>();
    private int mSavePosition;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mSavePosition = savedInstanceState.getInt(BUNDLE_KEY_POS, 0);
        }

        setContentView(R.layout.activity_main);
        //获取屏幕宽度
        final int width = ScreenInfoUtils.getWindowWidth(this);
        //获取屏幕高度
        final int height = ScreenInfoUtils.getFullActivityHeight(this);

        initView();
        initStatusBar();
        initToolbar();
        initMenu(width);
        initDrawerLayout(width);
        initViewPagerAdapter();
        initGradualTabView();

        //设置导航图标
        mToolbar.setNavigationIcon(R.drawable.ic_head);
        //隐藏原标题
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setToolbarTitle(0);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(BUNDLE_KEY_POS, mVpContent.getCurrentItem());
        super.onSaveInstanceState(outState);

    }

    /**
     * 初始化底部导航栏
     */
    private void initGradualTabView() {
        mGradualTabViews.add(mGtvRecommend);
        mGradualTabViews.add(mGtvFiction);
        mGradualTabViews.add(mGtvMusic);
        mGradualTabViews.add(mGtvVideo);

        int sie = mGradualTabViews.size();
        for (int i = 0; i < sie; i++) {
            GradualTabView tabView = mGradualTabViews.get(i);
            final int finalI = i;
            tabView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mVpContent.setCurrentItem(finalI, false);
                    setCurrrentTab(finalI);
                    setToolbarTitle(finalI);
                }
            });
        }
    }

    /**
     * 初始化ViewPager
     */
    private void initViewPagerAdapter() {
        mVpContent.setOffscreenPageLimit(mTabTitles.size());
        mVpContent.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                Fragment fragment = null;
                switch (position) {
                    case 0:
                        fragment = new RecommendFragment();
                        break;
                    case 1:
                        fragment = new FictionFragment();
                        break;
                    case 2:
                        fragment = new MusicFragment();
                        break;
                    case 3:
                        fragment = new VideoFragment();
                        break;
                    default:
                        break;
                }

                return fragment;
            }

            @Override
            public int getCount() {
                return mTabTitles.size();
            }

            @NonNull
            @Override
            public Object instantiateItem(@NonNull ViewGroup container, int position) {
                Fragment fragment = (Fragment) super.instantiateItem(container, position);
                mTabFragments.put(position, fragment);
                return fragment;
            }

            @Override
            public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
                mTabFragments.remove(position);
                super.destroyItem(container, position, object);
            }
        });
        mVpContent.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                mGradualTabViews.get(position).setProgress(1 - positionOffset);
                if (position < mGradualTabViews.size() - 1)
                    mGradualTabViews.get(position + 1).setProgress(positionOffset);

                //左->右 positionOffset  0->1 ,left pos , right pos+1
                //left progress: 1~0  (1-positionOffset);  right  progress: 0~1 (positionOffset)

                //左->右 positionOffset  1->0 ,left pos , right pos+1
                //left progress: 0~1 (1-positionOffset) ; right  progress: 1~0  (positionOffset)


            }

            @Override
            public void onPageSelected(int position) {
                mVpContent.setCurrentItem(position);
                setToolbarTitle(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mVpContent.setCurrentItem(mSavePosition);
    }

    /**
     * 初始化DrawerLayout
     *
     * @param width
     */
    private void initDrawerLayout(final int width) {
        //设置背景蒙层颜色
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        mDrawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerStateChanged(int newState) {
                //侧边栏状态
            }

            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
                //获取主页内容view
                View mContent = mDrawerLayout.getChildAt(0);
                //获取侧边栏内容 view
                View mMenu = drawerView;
                //主页面移动
                ViewHelper.setTranslationX(mContent, width * slideOffset);

                Log.d(TAG, "mMenu width:" + mMenu.getMeasuredWidth() + " : " + "window width" + width);
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }
        });
    }

    /**
     * 初始化侧划页
     *
     * @param width
     */
    private void initMenu(final int width) {
        //获取侧边栏默认宽度
        ViewGroup.LayoutParams leftParams = mLeftMenu.getLayoutParams();
        //设置侧边的宽高(如果不重新设置，即时设置match_parent也只能占屏幕百分80)
        leftParams.width = width;
        leftParams.height = MATCH_PARENT;
        mLeftMenu.setLayoutParams(leftParams);
        LeftMenuFragment menuFragment = (LeftMenuFragment) getSupportFragmentManager().findFragmentByTag("tag");
        if (menuFragment == null) {
            menuFragment = new LeftMenuFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.menu_frame, menuFragment, "tag")
                    .commit();
        }
        menuFragment.setOnCloseMenuListener(new LeftMenuFragment.OnCloseMenuListener() {
            @Override
            public void closeMenu() {
                mDrawerLayout.closeDrawer(mLeftMenu);
            }
        });
    }

    /**
     * 初始化Toolbar
     */
    private void initToolbar() {
        //将ToolBar与ActionBar关联
        setSupportActionBar(mToolbar);

        //另外openDrawerContentDescRes 打开图片   closeDrawerContentDescRes 关闭图片
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, 0, 0);
        //初始化状态
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * 初始化状态栏
     */
    private void initStatusBar() {
        //隐藏状态栏时，获取状态栏高度
        int statusBarHeight = ScreenInfoUtils.getStatusBarHeight(this);
        ScreenInfoUtils.fullScreen(this);

        //初始化状态栏的高度
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(MATCH_PARENT, statusBarHeight);
        mStatusbar.setLayoutParams(params);
    }

    /**
     * 初始化控件
     */
    private void initView() {
        mStatusbar = findViewById(R.id.view_statusbar);
        mToolbar = findViewById(R.id.toolbar);
        mTvTitle = findViewById(R.id.tv_title);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mLeftMenu = findViewById(R.id.menu_frame);

        mVpContent = findViewById(R.id.vp_content);

        mGtvRecommend = findViewById(R.id.gtv_recommend);
        mGtvFiction = findViewById(R.id.gtv_fiction);
        mGtvMusic = findViewById(R.id.gtv_music);
        mGtvVideo = findViewById(R.id.gtv_video);


    }


    /**
     * 设置选中页
     *
     * @param position
     */
    private void setCurrrentTab(int position) {
        int size = mGradualTabViews.size();
        for (int i = 0; i < size; i++) {
            if (i == position) {
                mGradualTabViews.get(i).setProgress(1);
            } else {
                mGradualTabViews.get(i).setProgress(0);
            }
        }
    }

    /**
     * 设置导航条title
     *
     * @param position
     */
    private void setToolbarTitle(int position) {
        mTvTitle.setText(mTabTitles.get(position));
    }

}

