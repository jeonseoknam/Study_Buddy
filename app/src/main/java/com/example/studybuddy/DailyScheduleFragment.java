package com.example.studybuddy;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class DailyScheduleFragment extends Fragment {

    private TextView dateText;
    private RecyclerView recyclerView;
    private Button addButton;
    private ScheduleAdapter adapter;
    private List<String> scheduleList = new ArrayList<>();
    private String selectedDate;

    public DailyScheduleFragment(String date) {
        this.selectedDate = date; // 전달받은 날짜
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_daily_schedule, container, false);

        // View 초기화
        dateText = view.findViewById(R.id.daily_date_text);
        recyclerView = view.findViewById(R.id.daily_schedule_recycler_view);
        addButton = view.findViewById(R.id.add_schedule_button);

        // 날짜 표시
        dateText.setText(selectedDate);

        // RecyclerView 설정
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ScheduleAdapter(scheduleList);
        recyclerView.setAdapter(adapter);

        // 데이터 로드
        loadSchedules();

        // 일정 추가 버튼 클릭 이벤트
        addButton.setOnClickListener(v -> showAddScheduleDialog());

        return view;
    }

    /**
     * 다이얼로그를 표시하여 일정 추가
     */
    private void showAddScheduleDialog() {
        // 다이얼로그 레이아웃을 설정
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("새 일정 추가");

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_schedule, null, false);
        EditText scheduleInput = dialogView.findViewById(R.id.schedule_input_dialog);

        builder.setView(dialogView);

        // "저장" 버튼 클릭 이벤트
        builder.setPositiveButton("저장", (dialog, which) -> {
            String newSchedule = scheduleInput.getText().toString();
            if (!newSchedule.isEmpty()) {
                addSchedule(newSchedule); // 새 일정 추가
                Toast.makeText(getContext(), "일정이 추가되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "일정을 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        });

        // "취소" 버튼 클릭 이벤트
        builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    /**
     * 새 일정을 리스트와 SharedPreferences에 추가
     */
    private void addSchedule(String schedule) {
        // 리스트에 새 일정 추가
        scheduleList.add(schedule);

        // RecyclerView 업데이트
        adapter.notifyDataSetChanged();

        // SharedPreferences에 저장
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("CalendarData", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();

        // 리스트를 JSON으로 변환하여 저장
        String json = gson.toJson(scheduleList);
        editor.putString(selectedDate, json);
        editor.apply();
    }

    /**
     * SharedPreferences에서 일정 로드
     */
    private void loadSchedules() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("CalendarData", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(selectedDate, null);

        if (json != null) {
            Type type = new TypeToken<List<String>>() {}.getType();
            scheduleList = gson.fromJson(json, type);
            adapter.notifyDataSetChanged();
        }
    }
}
