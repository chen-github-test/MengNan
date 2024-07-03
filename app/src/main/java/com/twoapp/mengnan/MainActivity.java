package com.twoapp.mengnan;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private TextView tvCurrentTime;
    private TextView tvCountdown;
    private Button btnSetTime;

    private long countdownEndTime;
    private final Handler handler = new Handler();
    private final Runnable countdownRunnable = new Runnable() {
        @Override
        public void run() {
            updateCountdown();
            updateCurrentTime();
            handler.postDelayed(this, 1000); // 每秒更新倒计时
        }
    };
    private boolean videoShown = false;  // 新增的布尔标志来控制视频的显示
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCurrentTime = findViewById(R.id.tv_current_time);
        tvCountdown = findViewById(R.id.tv_countdown);
        btnSetTime = findViewById(R.id.btn_set_time);

        updateCurrentTime();

        btnSetTime.setOnClickListener(v -> showTimePickerDialog());
    }

    private void updateCurrentTime() {
        Calendar now = Calendar.getInstance();
        tvCurrentTime.setText("当前时间：" + DateFormat.format("yyyy-MM-dd HH:mm:ss", now.getTime()));
    }

    private void showTimePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, selectedHour, selectedMinute) -> {
            calendar.set(Calendar.HOUR_OF_DAY, selectedHour);
            calendar.set(Calendar.MINUTE, selectedMinute);
            calendar.set(Calendar.SECOND, 0);
            countdownEndTime = calendar.getTimeInMillis();

            updateCountdown();
            handler.post(countdownRunnable);
        }, hour, minute, true);

        timePickerDialog.show();
    }

    private void updateCountdown() {
        long now = Calendar.getInstance().getTimeInMillis();
        long remainingTime = countdownEndTime - now;

        if (remainingTime <= 0) {
            if (!videoShown) {  // 只有在视频没有显示的情况下才启动
                videoShown = true;
                if (countdownRunnable != null) {
                    handler.removeCallbacks(countdownRunnable);
                }
                showVideo();
            }
        } else {
            int hours = (int) (remainingTime / (1000 * 60 * 60));
            int minutes = (int) ((remainingTime / (1000 * 60)) % 60);
            int seconds = (int) ((remainingTime / 1000) % 60);
            tvCountdown.setText(String.format("倒计时：%02d:%02d:%02d", hours, minutes, seconds));
        }
    }

    private void showVideo() {
        Intent intent = new Intent(this, VideoActivity.class);
        startActivity(intent);
        finish(); // 关闭主界面
    }
}