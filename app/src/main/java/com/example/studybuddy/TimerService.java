package com.example.studybuddy;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

public class TimerService extends Service {

    private final IBinder binder = new TimerBinder();
    private Handler handler = new Handler(Looper.getMainLooper());
    private long startTime = 0;
    private boolean isRunning = false;

    public class TimerBinder extends Binder {
        public TimerService getService() {
            return TimerService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void startTimer() {
        if (!isRunning) {
            startTime = System.currentTimeMillis();
            isRunning = true;
            System.out.println("Timer started at: " + startTime);
        }
    }

    public void pauseTimer() {
        isRunning = false;
    }

    public void resetTimer() {
        isRunning = false;
        startTime = 0;
    }

    public long getElapsedTime() {
        if (isRunning) {
            return System.currentTimeMillis() - startTime;
        } else {
            return 0;
        }
    }

    // isRunning() 메서드 추가
    public boolean isRunning() {
        System.out.println("Timer running status: " + isRunning);
        return isRunning;
    }
}
