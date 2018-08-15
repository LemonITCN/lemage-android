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
import cn.lemonit.lemage.activity.CameraActivity;
import cn.lemonit.lemage.activity.PreviewActivity;
import cn.lemonit.lemage.bean.FileObj;
import cn.lemonit.lemage.bean.ImageSize;
import cn.lemonit.lemage.core.LemageScanner;
import cn.lemonit.lemage.interfaces.LemageCameraCallback;
import cn.lemonit.lemage.interfaces.LemageResultCallback;
import cn.lemonit.lemage.lemageutil.SystemInfo;
import cn.lemonit.lemage.util.CameraFileUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "MainActivity";
    private final int REQUEST_PERMISSION_CODE = 0;

    private Button selectPhotoBut, selectVideoBut, allBut, onlyBut, changeColor, netSourceBut, cameraBut;
    private TextView textview;
//    private ImageView imageview;

    private int themeColor;
    /**
     * 最多允许选择的个数
     */
    private int maxCount = 5;
    /**
     * 权限相关
     */
    private ArrayList<String> requestPermissionList = new ArrayList<String>();
    private String[] listPermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.support.v7.appcompat.R.style.Theme_AppCompat_DayNight_NoActionBar);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        checkPermissions(listPermission);
    }

    private void initView() {
        selectPhotoBut = findViewById(R.id.selectPhotoBut);
        selectPhotoBut.setOnClickListener(this); 

        selectVideoBut = findViewById(R.id.selectVideoBut);
        selectVideoBut.setOnClickListener(this);

        allBut = findViewById(R.id.allBut);
        allBut.setOnClickListener(this);

        onlyBut = findViewById(R.id.onlyBut);
        onlyBut.setOnClickListener(this);

        changeColor = findViewById(R.id.changeColor);
        changeColor.setOnClickListener(this);

        netSourceBut = findViewById(R.id.netSourceBut);
        netSourceBut.setOnClickListener(this);

        cameraBut = findViewById(R.id.cameraBut);
        cameraBut.setOnClickListener(this);

        textview = findViewById(R.id.textview);

//        imageview = findViewById(R.id.imageview);
    }

    private void initData() {
        // 默认绿色主题
        themeColor = Color.GREEN;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 只选图片
            case R.id.selectPhotoBut:
                selectPhoto();
                break;
            // 只选视频
            case R.id.selectVideoBut:
                selectVideo();
                break;
            // 图片和视频全选
            case R.id.allBut:
                selectAll();
                break;
            // 图片和视频都选择，但是只能选择一个（以选择图片为例）
            case R.id.onlyBut:
                selectOne();
                break;
            // 更改主题颜色
            case R.id.changeColor:
                chengeThemeColor();
                break;
            // 直接传递网络地址进行预览
            case R.id.netSourceBut:
                netSourcePreview();
                break;
            // 拍照
            case R.id.cameraBut:
                Lemage.startCamera(this, 5, new LemageCameraCallback() {
                    @Override
                    public void cameraActionFinish(List<String> list) {
                        textview.setText("");
                        StringBuffer sb = new StringBuffer();
                        for(String url : list) {
                            sb.append(url + "\n\n");
                        }
                        textview.setText(sb.toString());
                    }
                });
                break;
        }
    }


    /**
     * 只选图片
     */
    private void selectPhoto() {
        Lemage.startChooser(MainActivity.this, maxCount, false, themeColor, LemageScanner.STYLE_ONLY_PHOTO, new LemageResultCallback() {
            @Override
            public void willClose(List<String> imageUrlList, boolean isOriginal, List<FileObj> list) {
                if(imageUrlList == null) return;
                textview.setText("");
                StringBuffer sb = new StringBuffer();
                for(String url : imageUrlList) {
                    sb.append(url + "\n\n");
                }
                textview.setText(sb.toString());
            }

            @Override
            public void closed(List<String> imageUrlList, boolean isOriginal) {

            }
        });
    }

    /**
     * 只选视频
     */
    private void selectVideo() {
        Lemage.startChooser(MainActivity.this, maxCount, false, themeColor, LemageScanner.STYLE_ONLY_VIDEO, new LemageResultCallback() {
            @Override
            public void willClose(List<String> imageUrlList, boolean isOriginal, List<FileObj> list) {
                textview.setText("");
                StringBuffer sb = new StringBuffer();
                for(String url : imageUrlList) {
                    sb.append(url + "\n\n");
                }
                textview.setText(sb.toString());
            }

            @Override
            public void closed(List<String> imageUrlList, boolean isOriginal) {

            }
        });
    }

    /**
     * 视频，图片都选
     */
    private void selectAll() {
        Lemage.startChooser(MainActivity.this, maxCount, false, themeColor, LemageScanner.STYLE_ALL, new LemageResultCallback() {
            @Override
            public void willClose(List<String> imageUrlList, boolean isOriginal, List<FileObj> list) {
                textview.setText("");
                StringBuffer sb = new StringBuffer();
                for(String url : imageUrlList) {
                    sb.append(url + "\n\n");
                }
                textview.setText(sb.toString());
            }

            @Override
            public void closed(List<String> imageUrlList, boolean isOriginal) {

            }
        });
    }

    /**
     * 视频和图片都显示，只选一类，另一类覆盖白色，不可选
     */
    private void selectOne() {
        Lemage.startChooser(MainActivity.this, maxCount, false, themeColor, LemageScanner.STYLE_ANYONE_PHOTO, new LemageResultCallback() {
            @Override
            public void willClose(List<String> imageUrlList, boolean isOriginal, List<FileObj> list) {
                textview.setText("");
                StringBuffer sb = new StringBuffer();
                for(String url : imageUrlList) {
                    sb.append(url + "\n\n");
                }
                textview.setText(sb.toString());
            }

            @Override
            public void closed(List<String> imageUrlList, boolean isOriginal) {

            }
        });
    }

    /**
     * 更改主题颜色
     */
    private void chengeThemeColor() {
        if(themeColor == Color.GREEN) {
            themeColor = getResources().getColor(R.color.colorOrange);
            Toast.makeText(this, "主题颜色 : 橙色", Toast.LENGTH_SHORT).show();
        }else {
            themeColor = Color.GREEN;
            Toast.makeText(this, "主题颜色 : 绿色", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 传递网络图片或者视频地址（list）到预览界面
     */
    private void netSourcePreview() {
        ArrayList<String> listUrlAll = new ArrayList<String>();   // 全部文件路径
        ArrayList<String> listUrlSelect = new ArrayList<String>();  // 已经选择文件路径
//        listUrlAll.add("lemage://album/localImage/storage/emulated/0/Download/2e2eb9389b504fc2b3b590d7efdde71191ef6d7d.jpeg");
//        listUrlAll.add("lemage://album/localVideo/storage/emulated/0/DCIM/Camera/20180629_131558.mp4");
//        listUrlAll.add("lemage://album/localImage/storage/emulated/0/Download/d6ca7bcb0a46f21fe099b7bdfc246b600d33aeab.jpeg");
        listUrlAll.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=3742173999,1643624888&fm=27&gp=0.jpg");
//                listUrlAll.add("http://wxsnsdy.tc.qq.com/105/20210/snsdyvideodownload?filekey=30280201010421301f0201690402534804102ca905ce620b1241b726bc41dcff44e00204012882540400&bizid=1023&hy=SH&fileparam=302c020101042530230204136ffd93020457e3c4ff02024ef202031e8d7f02030f42400204045a320a0201000400");
        listUrlAll.add("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
//        listUrlAll.add("http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8");
        listUrlSelect.addAll(listUrlAll);
        Lemage.startPreviewer(this, listUrlAll, listUrlSelect, maxCount, 2, themeColor, new LemageResultCallback() {
            @Override
            public void willClose(List<String> imageUrlList, boolean isOriginal, List<FileObj> list) {
                textview.setText("");
                StringBuffer sb = new StringBuffer();
                for(String url : imageUrlList) {
                    sb.append(url + "\n\n");
                }
                textview.setText(sb.toString());
            }

            @Override
            public void closed(List<String> imageUrlList, boolean isOriginal) {

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
