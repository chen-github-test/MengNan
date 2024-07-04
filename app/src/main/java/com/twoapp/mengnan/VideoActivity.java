package com.twoapp.mengnan;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class VideoActivity extends AppCompatActivity {
    private static final String TAG = "VideoActivity";
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        videoView = findViewById(R.id.video_view);

        String videoPath = "android.resource://" + getPackageName() + "/" + R.raw.sample1;
        Uri uri = Uri.parse(videoPath);
        videoView.setVideoURI(uri);
        videoView.setMediaController(new MediaController(this));
        videoView.setOnPreparedListener(mediaPlayer -> {
            mediaPlayer.setLooping(true);
            mediaPlayer.start();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.stopPlayback();
        }
    }
}