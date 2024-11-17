package com.example.studybuddy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MyProfileFragment extends Fragment {

    public MyProfileFragment() {
        // Required empty public constructor
    }

    public static MyProfileFragment newInstance(String param1, String param2) {
        return new MyProfileFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);

        // "나의 캘린더" 버튼 클릭 리스너 설정
        Button calendarButton = view.findViewById(R.id.btn_personalCalender);
        calendarButton.setOnClickListener(v -> {
            // 캘린더 화면으로 전환
            Fragment calendarFragment = new CalendarFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, calendarFragment)
                    .addToBackStack(null) // 뒤로가기 시 이전 화면으로 돌아가기
                    .commit();
        });

        return view;
    }
}
