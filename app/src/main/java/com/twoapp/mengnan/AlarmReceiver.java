package com.twoapp.mengnan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;

import androidx.annotation.RequiresApi;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CountdownApp::AlarmWakeLock");
        wl.acquire(10 * 60 * 1000L /*10 minutes*/);

        Intent videoIntent = new Intent(context, VideoActivity.class);
        videoIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(videoIntent);

        wl.release();
    }
}