package com.twoapp.mengnan;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class VideoActivity extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        videoView = findViewById(R.id.video_view);

        // 获取 raw 目录下的视频文件
        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sample1);
        videoView.setVideoURI(videoUri);

        MediaController mediaController = new MediaController(this);
        mediaController.setMediaPlayer(videoView);
        videoView.setMediaController(mediaController);

        videoView.setOnPreparedListener(mp -> {
            mp.setVolume(1.0f, 1.0f); // 设置声音最大
            videoView.start(); // 自动播放
        });
    }
}