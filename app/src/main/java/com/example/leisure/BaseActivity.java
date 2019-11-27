package com.example.leisure;

import android.view.View;
import android.widget.ImageView;

import com.example.leisure.util.ScreenInfoUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onResume() {
        super.onResume();
        //隐藏状态栏时，获取状态栏高度
        int statusBarHeight = ScreenInfoUtils.getStatusBarHeight(this);
        ScreenInfoUtils.fullScreen(this);

        //初始化状态栏的高度
        View statusbar = (View) findViewById(R.id.view_statusbar);
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(MATCH_PARENT, statusBarHeight);
        statusbar.setLayoutParams(params);


        //初始化UI控件
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        ImageView ivClose = (ImageView) findViewById(R.id.iv_close_menu);
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        View leftMenu = findViewById(R.id.menu_frame);

    }

    // 设置页面标题
    protected void setTitle(String title) {
//        TextView tv_title = findViewById(R.id.tv_title);
//        tv_title.setText(title);
    }

    public void onClick(View v) {
//        if (v.getId() == R.id.iv_share) {
//            Toast.makeText(this, "请先实现分享功能噢", Toast.LENGTH_LONG).show();
//        }
    }

}
