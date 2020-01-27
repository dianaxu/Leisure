package com.example.leisure.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.leisure.R;
import com.example.leisure.widget.textWatcher.MySearchTextWatcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 书架页 数据来源于greendao
 * <p>
 * 功能：
 * 1.可以查询漫画书
 * 2.显示加入书架的漫画书列表 sqlite
 */
public class BookShelfFragment extends Fragment implements MySearchTextWatcher.OnInputCompleteListener {

    private EditText mEtSearch;
    private RecyclerView mRvView;

    public static BookShelfFragment newInstance() {
        BookShelfFragment fragment = new BookShelfFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bookshelf, container, false);
        mEtSearch = view.findViewById(R.id.et_search);
        mRvView = view.findViewById(R.id.rv_view);

        MySearchTextWatcher textWatcher = new MySearchTextWatcher();
        textWatcher.addOnInputCompleteListener(this);
        mEtSearch.addTextChangedListener(textWatcher);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getBookShelfData();
    }

    /**
     * 获取书架的漫画书
     */
    private void getBookShelfData() {

    }

    @Override
    public void onInputComplete(String value) {
        mEtSearch.setText(value);
        mEtSearch.setSelection(value.length());

        //todo  书架中查询数据
        Toast.makeText(getContext(), value, Toast.LENGTH_SHORT).show();
    }
}
