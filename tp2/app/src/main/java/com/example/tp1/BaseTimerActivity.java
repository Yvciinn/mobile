package com.example.tp1;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public abstract class BaseTimerActivity extends AppCompatActivity {
    private static long accumulated = 0; //lel wa9t deja ma7soub
    private static long startTime = 0;
    private static boolean running = false;
    private static boolean autoResume = false;

    private TextView txtTimer;
    private Button btStart;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private final Runnable ticker = new Runnable() {
        @Override
        public void run() {
            updateUi();
            handler.postDelayed(this, 500);
        }
    };
    protected void initTimer() {
        txtTimer = findViewById(R.id.txt_timer);
        btStart = findViewById(R.id.bt_start);
        Button btRestart = findViewById(R.id.bt_restart);
        btStart.setOnClickListener(v -> {
            if (autoResume) {
                pauseTimer();
            } else {
                startTimer();
            }
            updateUi();
        });

        btRestart.setOnClickListener(v -> {
            restartTimer();
            updateUi();
        });

        updateUi();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (autoResume && !running) {
            startTime = SystemClock.elapsedRealtime();
            running = true;
        }
        handler.post(ticker);
    }

    @Override
    protected void onPause() {
        super.onPause();
        freeze();
        handler.removeCallbacks(ticker);
    }

    private static void freeze() {
        if (running) {
            accumulated += SystemClock.elapsedRealtime() - startTime;
            running = false;
        }
    }

    private static void startTimer() {
        autoResume = true;
        if (!running) {
            startTime = SystemClock.elapsedRealtime();
            running = true;
        }
    }

    private static void pauseTimer() {
        freeze();
        autoResume = false;
    }

    private static void restartTimer() {
        accumulated = 0;
        startTime = SystemClock.elapsedRealtime();
        running = true;
        autoResume = true;
    }

    private static long getElapsedMillis() {
        return running
                ? accumulated + (SystemClock.elapsedRealtime() - startTime)
                : accumulated;
    }

    private void updateUi() {
        if (txtTimer == null) return;

        long totalSeconds = getElapsedMillis() / 1000;
        long h = totalSeconds / 3600;
        long m = (totalSeconds % 3600) / 60;
        long s = totalSeconds % 60;
        txtTimer.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s));

        if (autoResume) {
            btStart.setText("Pause");
        } else if (accumulated > 0) {
            btStart.setText("Resume");
        } else {
            btStart.setText("Start");
        }
    }
}