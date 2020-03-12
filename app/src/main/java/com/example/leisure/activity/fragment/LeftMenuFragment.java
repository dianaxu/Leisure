package com.example.leisure.activity.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.leisure.MainApplication;
import com.example.leisure.R;
import com.example.leisure.activity.DownloadComicActivity;
import com.example.leisure.greenDao.gen.DaoSession;
import com.example.leisure.util.Constant;
import com.example.leisure.util.FileUtil;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatImageView;

/**
 * 左侧划页
 */
public class LeftMenuFragment extends BaseFragment implements View.OnClickListener {

    private View mView;
    private AppCompatImageView mIvClose, mIvHead;
    private TextView mTvName, mTvCacheManage, mTvFileSize;
    private RelativeLayout mRlClear;

    private OnCloseMenuListener mListener;

    private String mFilePath;

    @Override
    protected boolean isHintStatusBar() {
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.left_menu, container, false);
        initView(mView);
        mFilePath = FileUtil.getSavePath(getContext(), null, null, null);
        return mView;
    }

    private void initView(View view) {
        mTvName = view.findViewById(R.id.tv_name);
        mIvHead = view.findViewById(R.id.iv_head);
        mIvClose = view.findViewById(R.id.iv_close_menu);
        mTvCacheManage = view.findViewById(R.id.tv_cache_manage);
        mRlClear = view.findViewById(R.id.rl_clear);
        mTvFileSize = view.findViewById(R.id.tv_file_size);

        mIvHead.setOnClickListener(this);
        mIvClose.setOnClickListener(this); //关闭侧滑栏按钮
        mTvCacheManage.setOnClickListener(this);
        mRlClear.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateTvFileSize();
    }

    private void updateTvFileSize() {
        mTvFileSize.setText(FileUtil.getFileOrFilesSize(mFilePath, FileUtil.SIZETYPE_MB) + "MB");
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
        } else if (R.id.tv_cache_manage == id) {
            DownloadComicActivity.startDownloadComicActivity(getContext());
        } else if (R.id.rl_clear == id) {
            showDialog();
        }
    }

    @SuppressLint("ResourceAsColor")
    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle("提醒")//设置对话框 标题
                .setMessage("清除缓存将清除您所有书籍的下载记录，是否确定清除？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clearDataByDB();
                        clearFile();
                        updateTvFileSize();
                        Toast.makeText(getActivity(), "已清除缓存", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#1592C4"));
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#1592C4"));
    }

    private void clearFile() {
        String savePath = FileUtil.getSavePath(getContext(), null, null, null);
        FileUtil.deleteDirWihtFile(new File(savePath), false);
    }

    private void clearDataByDB() {
        try {
            DaoSession daoSession = MainApplication.getInstance().getDaoSession();
            StringBuffer sqlBook = new StringBuffer();
            sqlBook.append("update Comic_Book_Bean set ");
            sqlBook.append(" cache_state = " + Constant.DownloadState.DOWNLOAD_NOT);
            sqlBook.append(", progress = 0 ;");
            daoSession.getDatabase().execSQL(sqlBook.toString());

            StringBuffer sqlChapter = new StringBuffer();
            sqlChapter.append("update Comic_Chapter_Bean set ");
            sqlChapter.append(" cache_state = " + Constant.DownloadState.DOWNLOAD_NOT);
            sqlChapter.append(", is_caching = 0 ");
            sqlChapter.append(", path = ''");
            sqlChapter.append(", cache_count = 0 ;");
            daoSession.getDatabase().execSQL(sqlChapter.toString());

            StringBuffer sqlImage = new StringBuffer();
            sqlImage.append("update Comic_Image_Bean set ");
            sqlImage.append(" is_caching = 0 ");
            sqlImage.append(", path = '' ;");
            daoSession.getDatabase().execSQL(sqlImage.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnCloseMenuListener(OnCloseMenuListener listener) {
        this.mListener = listener;
    }

    public interface OnCloseMenuListener {
        void closeMenu();
    }
}
