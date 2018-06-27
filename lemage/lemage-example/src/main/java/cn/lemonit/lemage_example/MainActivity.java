package cn.lemonit.lemage_example;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import cn.lemonit.lemage.Lemage;
import cn.lemonit.lemage.activity.LemageActivity;
import cn.lemonit.lemage.bean.ImageSize;
import cn.lemonit.lemage.bean.Video;
import cn.lemonit.lemage.core.LemageScanner;
import cn.lemonit.lemage.interfaces.VideoScanCompleteCallback;
import cn.lemonit.lemage.view.VideoStartImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "MainActivity";

    private Button button;

    private ImageView image;

    private String path;

    private RelativeLayout layout;

    private String MP4_path;
    private long MP4_size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.support.v7.appcompat.R.style.Theme_AppCompat_DayNight_NoActionBar);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        button = findViewById(R.id.selectPhoto);
        button.setOnClickListener(this);
        image = findViewById(R.id.image);
    }

    private void initData() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test);
        if(bitmap != null) {
            path = Lemage.generateLemageUrl(this, bitmap, true);
            Log.e("MainActivity", "path ==== " + path);
        }else {
            Log.e("MainActivity", "bitmap ==== null");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selectPhoto:
                Lemage.startChooser(MainActivity.this, 5, false, R.color.colorPrimaryDark, null);
//                actionVideo();
//                addView();
                break;
        }
    }

    private void actionVideo() {
        LemageScanner.scanAllVideo(this, new VideoScanCompleteCallback() {
            @Override
            public void scanComplete(List<Video> list) {
                if(list != null && list.size() > 0) {
                    for(Video video : list) {
                        Log.e(TAG, "video.id ==== " + video.getId());
                        Log.e(TAG, "video.title ==== " + video.getTitle());
                        Log.e(TAG, "video.album ==== " + video.getAlbum());
                        Log.e(TAG, "video.artist ==== " + video.getArtist());
                        Log.e(TAG, "video.displayName ==== " + video.getDisplayName());
                        Log.e(TAG, "video.mimeType ==== " + video.getMimeType());
                        Log.e(TAG, "video.path ==== " + video.getPath());
                        Log.e(TAG, "video.duration ==== " + video.getDuration());
                        Log.e(TAG, "video.size ==== " + video.getSize());
                    }
                    Video video = list.get(0);
                    MediaMetadataRetriever media = new MediaMetadataRetriever();
                    MP4_path = video.getPath();
                    MP4_size = video.getSize();
                    media.setDataSource(MP4_path);
                    Bitmap bitmap = media.getFrameAtTime();
                    image.setImageBitmap(bitmap);
                }
            }
        });
    }

    private void addView() {
        layout = findViewById(R.id.layout);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        VideoStartImageView videoStartImageView = new VideoStartImageView(this);
        videoStartImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 播放视频
                startVideo();
            }
        });
        layout.addView(videoStartImageView, layoutParams);
    }


    private void startVideo() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        VideoView mViewoView = new VideoView(this);
        mViewoView.setVideoPath(MP4_path);
//        layout.removeAllViews();
        layout.addView(mViewoView, layoutParams);
        if(!mViewoView.isPlaying()) {
            mViewoView.start();
        }
    }

}
