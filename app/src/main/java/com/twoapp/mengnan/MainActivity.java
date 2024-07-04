package com.twoapp.mengnan;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_IGNORE_BATTERY_OPTIMIZATIONS = 1001;
    private TextView tvCurrentTime;
    private TextView tvCountdown;
    private TextView tvMotivation; // 新增的文本视图
    private Button btnSetTime;

    private long countdownEndTime;

    private final Handler handler = new Handler();
    private final Runnable countdownRunnable = new Runnable() {
        @Override
        public void run() {
            updateCountdown();
            handler.postDelayed(this, 1000); // 每秒更新倒计时
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvCurrentTime = findViewById(R.id.tv_current_time);
        tvCountdown = findViewById(R.id.tv_countdown);
        tvMotivation = findViewById(R.id.tv_motivation); // 初始化文本视图
        btnSetTime = findViewById(R.id.btn_set_time);

        updateCurrentTime();

        btnSetTime.setOnClickListener(v -> showTimePickerDialog());

        // 请求忽略电池优化权限
        requestIgnoreBatteryOptimizations();
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

            // 显示“加油，猛男”文本
            tvMotivation.setVisibility(View.VISIBLE);

            updateCountdown();
            handler.post(countdownRunnable);

            // 启动服务
            startCountdownService();
        }, hour, minute, true);

        timePickerDialog.show();
    }

    private void startCountdownService() {
        Intent serviceIntent = new Intent(this, CountdownService.class);
        serviceIntent.putExtra("countdownEndTime", countdownEndTime);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        }
    }

    private void updateCountdown() {
        long now = System.currentTimeMillis();
        long remainingTime = countdownEndTime - now;

        if (remainingTime <= 0) {
            handler.removeCallbacks(countdownRunnable);
        } else {
            int hours = (int) (remainingTime / (1000 * 60 * 60));
            int minutes = (int) ((remainingTime / (1000 * 60)) % 60);
            int seconds = (int) ((remainingTime / 1000) % 60);
            tvCountdown.setText(String.format("倒计时：%02d:%02d:%02d", hours, minutes, seconds));
        }
    }

    private void requestIgnoreBatteryOptimizations() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        if (pm != null && !pm.isIgnoringBatteryOptimizations(getPackageName())) {
            Intent intent = new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            startActivityForResult(intent, REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IGNORE_BATTERY_OPTIMIZATIONS) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            if (pm != null && pm.isIgnoringBatteryOptimizations(getPackageName())) {
                // 已获得忽略电池优化权限
            } else {
                // 用户拒绝了权限请求
                new AlertDialog.Builder(this)
                        .setMessage("应用需要忽略电池优化权限，以便后台正常运行。")
                        .setPositiveButton("重试", (dialog, which) -> requestIgnoreBatteryOptimizations())
                        .setNegativeButton("取消", null)
                        .show();
            }
        }
    }
}