package cn.lemonit.lemage_example;

import android.Manifest;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.lemonit.lemage.Lemage;
import cn.lemonit.lemage.been.FileObj;
import cn.lemonit.lemage.core.LemageScanner;
import cn.lemonit.lemage.interfaces.LemageCameraCallback;
import cn.lemonit.lemage.interfaces.LemageResultCallback;
import cn.lemonit.lemage.util.BitmapUtil;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final int REQUEST_PERMISSION_CODE = 0;
    private Button cameraButton;
    private ImageView imageview;
    private Button selectButton;
    private Button previewButton;
    private ArrayList<String> requestPermissionList = new ArrayList<String>();
    private String[] listPermission = new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setListener();
        checkPermissions(listPermission);
    }

    private void initView() {
        cameraButton = findViewById(R.id.cameraButton);
        imageview = findViewById(R.id.imageview);
        selectButton = findViewById(R.id.selectButton);
        previewButton = findViewById(R.id.previewButton);
    }

    private void setListener() {
        cameraButton.setOnClickListener(this);
        selectButton.setOnClickListener(this);
        previewButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 拍照或者录像
            case R.id.cameraButton:
                startCamera();
                break;
            // 选择器
            case R.id.selectButton:
                selectPhoto();
                break;
            // 预览
            case R.id.previewButton:
                previewMedia();
                break;
        }
    }

    private void startCamera() {
        Lemage.startCamera(this, 5, new LemageCameraCallback() {
            @Override
            public void cameraActionFinish(List<String> list) {
                for(String str : list) {
                    Log.e("MainActivity", "路径str == " + str);
                    Bitmap bitmap = BitmapUtil.url2Bitmap(str, null, MainActivity.this);
                    imageview.setImageBitmap(bitmap);
                }
            }
        });
    }

    private void selectPhoto() {
        int themeColor = Color.GREEN;
        Lemage.startChooser(MainActivity.this, 3, false, themeColor, LemageScanner.STYLE_ONLY_PHOTO, new LemageResultCallback() {
            @Override
            public void willClose(List<String> imageUrlList, boolean isOriginal, List<FileObj> list) {
                if(imageUrlList == null) {
                    return;
                }
                StringBuffer sb = new StringBuffer();
                for(String url : imageUrlList) {
                    Bitmap bitmap = BitmapUtil.url2Bitmap(url, null, MainActivity.this);
                    imageview.setImageBitmap(bitmap);
                    sb.append(url + "\n\n");
                }
                Log.e("Activity", "选择器 ：" + sb.toString());
            }

            @Override
            public void closed(List<String> imageUrlList, boolean isOriginal, List<FileObj> list) {

            }
        });
    }

    private void previewMedia() {
        ArrayList<String> listUrlAll = new ArrayList<String>();
        listUrlAll.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=3742173999,1643624888&fm=27&gp=0.jpg");
        listUrlAll.add("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
        ArrayList<String> listUrlSelect = new ArrayList<String>();
        listUrlSelect.addAll(listUrlAll);
        Lemage.startPreviewer(this, listUrlAll, listUrlSelect, 3, 0, Color.GREEN, new LemageResultCallback() {
            @Override
            public void willClose(List<String> imageUrlList, boolean isOriginal, List<FileObj> list) {
                StringBuffer sb = new StringBuffer();
                for(String url : imageUrlList) {
                    sb.append(url + "\n\n");
                }
            }

            @Override
            public void closed(List<String> imageUrlList, boolean isOriginal, List<FileObj> list) {

            }
        });
    }

    private void checkPermissions(String[] listPermission) {
        boolean checkPermission = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : listPermission) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissionList.add(permission);
                }
            }
            if (requestPermissionList.size() == 0) {
                checkPermission = true;
            } else {
                checkPermission = false;
            }
        } else {
            checkPermission = true;
        }
        if (checkPermission) {
            Toast.makeText(this, "您开通了所有权限，请继续", Toast.LENGTH_LONG).show();
        } else {
            requestPermission(requestPermissionList, REQUEST_PERMISSION_CODE);
        }
    }

    public void requestPermission(List<String> requestPermissionList, int requestCode) {
        ActivityCompat.requestPermissions(this, requestPermissionList.toArray(new String[requestPermissionList.size()]), requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "您拒绝了权限,将退出应用", Toast.LENGTH_LONG).show();
                    MainActivity.this.finish();
                    return;
                }
            }
            Toast.makeText(this, "您开通了所有权限，请继续", Toast.LENGTH_LONG).show();
        }
    }
}
