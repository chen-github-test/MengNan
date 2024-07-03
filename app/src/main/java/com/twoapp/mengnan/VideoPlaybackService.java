package com.twoapp.mengnan;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.widget.VideoView;

import androidx.annotation.Nullable;

public class VideoPlaybackService extends Service {
    private VideoView videoView;
    private static final String CHANNEL_ID = "video_channel";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("ForegroundServiceType")
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();

        // Setup the video view
        videoView = new VideoView(this);
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sample1);
        videoView.setVideoURI(videoUri);
        videoView.setOnPreparedListener(mp -> {
            mp.setOnVideoSizeChangedListener((mp1, width, height) -> videoView.start());
        });

        // Set up the notification
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle("Playing Video")
                    .setContentText("Your video is being played")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .build();
        }

        startForeground(1, notification);
        videoView.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (videoView != null && videoView.isPlaying()) {
            videoView.stopPlayback();
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Video Playback Channel",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Channel for video playback notifications");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
}