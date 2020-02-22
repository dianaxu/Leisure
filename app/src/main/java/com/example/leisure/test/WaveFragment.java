package com.example.leisure.test;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.leisure.R;
import com.example.leisure.widget.WaveImageView;

import java.util.Random;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class WaveFragment extends Fragment {
    WaveImageView view1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_wave, container, false);

        view1 = view.findViewById(R.id.wv_view);
        Button btn = view.findViewById(R.id.btn_view);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Random random = new Random();
                float i = random.nextInt(100);


                int headHeight = view1.getHeight();
                float pr = (100 - i) / 100.0f;
                view1.setProgress(pr);
                btn.setText(i + ":" + headHeight + ":" + pr);

            }
        });
        view1.setProgress(0.8f);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
