package cn.lemonit.lemage_example;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import cn.lemonit.lemage.Lemage;
import cn.lemonit.lemage.activity.LemageActivity;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button button;

    private ImageView image;

    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                action();
//                File file = new File("/storage/emulated/0/Android/data/cn.lemonit.lemage_example/longTermPicture");
//                if (!file.exists()) {
//                    file.mkdirs();
//                }
                break;
        }
    }

    private void action() {
        Bitmap bitmapN = BitmapFactory.decodeStream(Lemage.loadImageInputStream(MainActivity.this, path));   // 以流的形式获取Bitmap
//        byte[] data = Lemage.loadImageData(MainActivity.this, path);
//        Bitmap bitmapN = BitmapFactory.decodeByteArray(data, 0, data.length);   // 以byte[]的形式获取bitmap
        if(bitmapN != null) {
            image.setImageBitmap(bitmapN);
        }else {
            Log.e("MainActivity", "bitmapN ==== null");
        }
    }
}
