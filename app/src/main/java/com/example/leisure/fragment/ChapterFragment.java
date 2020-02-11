package com.example.leisure.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.leisure.R;
import com.example.leisure.adapter.BaseRecyclerViewAdapter;
import com.example.leisure.adapter.BaseViewHolder;
import com.example.leisure.bean.ComicItemBean;
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
public class ChapterFragment extends Fragment implements BaseRecyclerViewAdapter.OnRecyclerViewItemClickListener<ComicItemBean.ChapterBean> {
    public static final String BUNDLE_KEY_CHAPTER = "key_chapter";
    public static final String BUNDLE_KEY_TITLE = "key_title";
    public static final String BUNDLE_KEY_HURL1 = "key_hurl1";         //书的链接

    private List<ComicItemBean.ChapterBean> mLsData = new ArrayList<>();
    private String mTitle;
    private String mUrl;

    private CommonToolbar mCtbHeader;
    private RecyclerView mRvView;
    private BaseRecyclerViewAdapter mAdapter;

    private OnToolbarListener mListener;
    private OnChapterSelectedListener mChapterSelectedListener;
    private int mReadingPosition = 0;


    public static ChapterFragment newInstance(List<ComicItemBean.ChapterBean> chapterBeanList,
                                              String title, String url) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(BUNDLE_KEY_CHAPTER, (Serializable) chapterBeanList);
        bundle.putString(BUNDLE_KEY_TITLE, title);
        bundle.putString(BUNDLE_KEY_HURL1, url);
        ChapterFragment fragment = new ChapterFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    public void setReading(int position) {
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
            mLsData = (List<ComicItemBean.ChapterBean>) bundle.getSerializable(BUNDLE_KEY_CHAPTER);
            mTitle = bundle.getString(BUNDLE_KEY_TITLE);
            mUrl = bundle.getString(BUNDLE_KEY_HURL1);
        }
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
        mCtbHeader.setText(mTitle);
        mCtbHeader.setLeftClickListener(new CommonToolbar.OnLeftDrawableClickListener() {
            @Override
            public void onLeftDrawableClick() {
                if (mListener != null) {
                    mListener.onBackClick();
                }
            }
        });
        mCtbHeader.setRightClickListener(new CommonToolbar.OnRightDrawableClickListener() {
            @Override
            public void onRightDrawableClick() {
                //todo 排序功能
            }
        });
    }

    private void initRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mRvView.setLayoutManager(layoutManager);

        mAdapter = new BaseRecyclerViewAdapter<ComicItemBean.ChapterBean>(getActivity(), mLsData) {
            @Override
            public int getResourseId() {
                return R.layout.item_chapter;
            }


            @Override
            public void onBindView(BaseViewHolder holder, int position) {
                ComicItemBean.ChapterBean bean = mLsData.get(position);

                TextView tvNum = (TextView) holder.getView(R.id.tv_num);
                tvNum.setText(bean.num);
                if (bean.isReading) {
                    holder.setColorOfTextView(R.id.tv_num, getResources().getColor(R.color.textReadingColor));
//                    tvNum.setTextColor(getResources().getColor(R.color.textReadingColor));
                } else
                    holder.setColorOfTextView(R.id.tv_num, getResources().getColor(R.color.textTitleColor));

//                tvNum.setTextColor(getResources().getColor(R.color.textTitleColor));

                //todo 需要去判断是否已经缓存了
                holder.getView(R.id.tv_presence).setVisibility(View.GONE);

            }
        };

        mRvView.setAdapter(mAdapter);
        mAdapter.addOnRecyclerViewItemClickListener(this);

    }

    @Override
    public void onRecyclerViewItemClick(View view, int position, ComicItemBean.ChapterBean bean) {
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
