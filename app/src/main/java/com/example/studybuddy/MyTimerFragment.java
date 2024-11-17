package com.example.studybuddy;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class MyTimerFragment extends Fragment {

    // 파라미터 키를 정의
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private TextView timerTextView;
    private Button startButton, pauseButton, resetButton;

    private TimerService timerService;
    private boolean isServiceBound = false;

    private Handler handler = new Handler(Looper.getMainLooper());


    public MyTimerFragment(){

    }
    public static MyTimerFragment newInstance(String param1, String param2) {
        MyTimerFragment fragment = new MyTimerFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimerService.TimerBinder binder = (TimerService.TimerBinder) service;
            timerService = binder.getService();
            isServiceBound = true;
            System.out.println("Service connected.");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isServiceBound = false;
            System.out.println("Service disconnected.");
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_timer, container, false);

        timerTextView = view.findViewById(R.id.timer_text_view);
        startButton = view.findViewById(R.id.start_button);
        pauseButton = view.findViewById(R.id.pause_button);
        resetButton = view.findViewById(R.id.reset_button);

        startButton.setOnClickListener(v -> {
            if (isServiceBound) {
                timerService.startTimer();
                System.out.println("Start button clicked, timer started.");
                updateTimer();
            } else {
                System.out.println("Service not bound");
            }
        });

        pauseButton.setOnClickListener(v -> {
            if (isServiceBound) {
                timerService.pauseTimer();
                System.out.println("Pause button clicked, timer paused.");
            }
        });

        resetButton.setOnClickListener(v -> {
            if (isServiceBound) {
                timerService.resetTimer();
                timerTextView.setText("00:00:00");
                System.out.println("Reset button clicked, timer reset.");
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(getActivity(), TimerService.class);
        getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isServiceBound) {
            getActivity().unbindService(serviceConnection);
            isServiceBound = false;
        }
    }

    private void updateTimer() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (isServiceBound) {
                    long elapsedMillis = timerService.getElapsedTime();
                    int seconds = (int) (elapsedMillis / 1000) % 60;
                    int minutes = (int) (elapsedMillis / (1000 * 60)) % 60;
                    int hours = (int) (elapsedMillis / (1000 * 60 * 60));

                    String time = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                    timerTextView.setText(time);

                    System.out.println("Timer updated: " + time);

                    if (timerService.isRunning()) {
                        handler.postDelayed(this, 1000); // 1초마다 갱신
                    } else {
                        System.out.println("Timer is not running");
                    }
                }
            }
        });
    }
}
