package com.example.leisure.activity.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.leisure.MainApplication;
import com.example.leisure.R;
import com.example.leisure.activity.adapter.BaseRecyclerViewAdapter;
import com.example.leisure.activity.adapter.BaseViewHolder;
import com.example.leisure.db.greendao.ComicChapterBean;
import com.example.leisure.util.Constant;
import com.example.leisure.widget.CommonToolbar;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 章节页
 * <p>
 * 3.章节的显示
 * 未完成功能：
 * 1.支持排序功能
 * 2.快速滑动
 * 4.跳转到具体内容
 * 5.显示已缓存的章节
 */
public class ChapterFragment extends Fragment implements BaseRecyclerViewAdapter.OnItemClickListener<ComicChapterBean> {
    public static final String BUNDLE_KEY_CHAPTER = "key_chapter";

    private List<ComicChapterBean> mLsData = new ArrayList<>();
    private String mBookName;

    private CommonToolbar mCtbHeader;
    private RecyclerView mRvView;
    private BaseRecyclerViewAdapter mAdapter;

    private OnToolbarListener mListener;
    private OnChapterSelectedListener mChapterSelectedListener;
    private int mReadingPosition = 0;


    public static ChapterFragment newInstance(List<ComicChapterBean> chapterBeanList) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_KEY_CHAPTER, (Serializable) chapterBeanList);
        ChapterFragment fragment = new ChapterFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public void updateDate(List<ComicChapterBean> list) {
        this.mLsData = list;
        mAdapter.updateData(list);
    }

    public void setReadPosition(int position) {
        if (mLsData != null && mLsData.size() > position) {
            mLsData.get(mReadingPosition).isReading = false;
            mReadingPosition = position;
            mLsData.get(mReadingPosition).isReading = true;
            if (mAdapter != null && mRvView != null) {
                mAdapter.notifyDataSetChanged();
                LinearLayoutManager manager = (LinearLayoutManager) mRvView.getLayoutManager();
                manager.scrollToPositionWithOffset(mReadingPosition, 200);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof OnToolbarListener) {
            mListener = (OnToolbarListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnToolbarListener");
        }
        super.onAttach(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mLsData = (List<ComicChapterBean>) bundle.getSerializable(BUNDLE_KEY_CHAPTER);
        }
        mBookName = MainApplication.getInstance().getInfo(Constant.ComicBaseBundle.BUNDLE_NAME);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chapter, container, false);
        mCtbHeader = view.findViewById(R.id.ctb_header);
        mRvView = view.findViewById(R.id.rv_view);

        initToolbar();
        initRecyclerView();

        return view;
    }

    private void initToolbar() {
        mCtbHeader.setText(mBookName);
        mCtbHeader.setLeftClickListener(new CommonToolbar.OnLeftDrawableClickListener() {
            @Override
            public void onLeftDrawableClick() {
                if (mListener != null) {
                    mListener.onBackClick();
                }
            }
        });
        mCtbHeader.setRightTextClickListener(new CommonToolbar.OnRightTextClickListener() {
            @Override
            public void onRightClick(View view) {
                view.setSelected(!view.isSelected());
                mCtbHeader.setRightText(view.isSelected() ? "正序" : "倒序");
                mAdapter.sortData();
            }
        });
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRvView.setLayoutManager(layoutManager);

        mAdapter = new BaseRecyclerViewAdapter<ComicChapterBean>(getActivity(), mLsData) {
            @Override
            public int getResourseId() {
                return R.layout.item_chapter;
            }


            @Override
            public void onBindView(BaseViewHolder holder, int position) {
                ComicChapterBean bean = mLsData.get(position);

                TextView tvNum = (TextView) holder.getView(R.id.tv_num);
                TextView tvPresence = (TextView) holder.getView(R.id.tv_presence);

                tvNum.setText(bean.num);
                if (bean.isReading) {
                    holder.setTextColor(R.id.tv_num, getResources().getColor(R.color.textReadingColor));
//                    tvNum.setTextColor(getResources().getColor(R.color.textReadingColor));
                } else
                    holder.setTextColor(R.id.tv_num, getResources().getColor(R.color.textTitleColor));

//                tvNum.setTextColor(getResources().getColor(R.color.textTitleColor));
                tvPresence.setVisibility(bean.getIsCaching() ? View.VISIBLE : View.GONE);

            }
        };

        mRvView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(View view, int position, ComicChapterBean bean) {
        if (mChapterSelectedListener != null) {
            mChapterSelectedListener.onChapterSelected(position);
        }
    }

    public void addChapterSelectedListener(OnChapterSelectedListener listener) {
        this.mChapterSelectedListener = listener;
    }

    public interface OnToolbarListener {
        void onBackClick();
    }

    public interface OnChapterSelectedListener {
        void onChapterSelected(int position);
    }
}
