package com.example.studybuddy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CalendarFragment extends Fragment {

    private CalendarView calendarView;
    private TextView selectedDateText;
    private EditText scheduleInput;
    private Button saveButton;
    private Button listButton;

    private String selectedDate = ""; // 사용자가 선택한 날짜

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);

        // View 초기화
        calendarView = view.findViewById(R.id.calendar_view);
        selectedDateText = view.findViewById(R.id.selected_date_text);
        scheduleInput = view.findViewById(R.id.schedule_input);
        saveButton = view.findViewById(R.id.save_schedule_button);
        listButton = view.findViewById(R.id.schedule_list_button);


        // 캘린더 날짜 선택 리스너
        calendarView.setOnDateChangeListener((view1, year, month, dayOfMonth) -> {
            selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year;
            selectedDateText.setText("선택된 날짜: " + selectedDate);
        });

        // 저장 버튼 클릭 이벤트 설정
        saveButton.setOnClickListener(v -> {
            if (!selectedDate.isEmpty()) {
                String schedule = scheduleInput.getText().toString();
                saveSchedule(selectedDate, schedule);

                // DailyScheduleFragment로 이동
                Fragment dailyScheduleFragment = new DailyScheduleFragment(selectedDate);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, dailyScheduleFragment)
                        .addToBackStack(null)
                        .commit();
            } else {
                Toast.makeText(getContext(), "날짜를 선택하세요.", Toast.LENGTH_SHORT).show();
            }
        });

        // 목록 버튼 클릭 이벤트 설정
        listButton.setOnClickListener(v -> {

                // DailyScheduleFragment로 이동
                Fragment dailyScheduleFragment = new DailyScheduleFragment(selectedDate);
                requireActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, dailyScheduleFragment)
                        .addToBackStack(null)
                        .commit();

        });

        return view;
    }

    // 일정 저장 로직
    private void saveSchedule(String date, String schedule) {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("CalendarData", Context.MODE_PRIVATE);
        Gson gson = new Gson();

        // 기존 일정 로드
        String json = sharedPreferences.getString(date, null);
        List<String> scheduleList;

        if (json != null) {
            Type type = new TypeToken<List<String>>() {}.getType();
            scheduleList = gson.fromJson(json, type);
        } else {
            scheduleList = new ArrayList<>();
        }

        // 새 일정 추가
        scheduleList.add(schedule);

        // 데이터 저장
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(date, gson.toJson(scheduleList));
        editor.apply();
    }
}
