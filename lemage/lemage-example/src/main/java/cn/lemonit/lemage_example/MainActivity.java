package cn.lemonit.lemage_example;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
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
import cn.lemonit.lemage.activity.PreviewActivity;
import cn.lemonit.lemage.bean.FileObj;
import cn.lemonit.lemage.core.LemageScanner;
import cn.lemonit.lemage.interfaces.LemageResultCallback;
import cn.lemonit.lemage.lemageutil.SystemInfo;

public class MainActivity extends Activity implements View.OnClickListener {

    private final String TAG = "MainActivity";

    private Button selectPhotoBut, selectVideoBut, allBut, onlyBut, changeColor, netSourceBut;
    private TextView textview;

    private int themeColor;
    /**
     * 最多允许选择的个数
     */
    private int maxCount = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.support.v7.appcompat.R.style.Theme_AppCompat_DayNight_NoActionBar);
        setContentView(R.layout.activity_main);
        initView();
        initData();
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

        textview = findViewById(R.id.textview);
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
        }
    }

    /**
     * 只选图片
     */
    private void selectPhoto() {
        Lemage.startChooser(MainActivity.this, maxCount, false, themeColor, LemageScanner.STYLE_ONLY_PHOTO, new LemageResultCallback() {
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

    }

    //    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.selectPhoto:
//                Lemage.startChooser(MainActivity.this, 5, false, R.color.colorPrimaryDark, LemageScanner.STYLE_ONLY_PHOTO, null);
//                break;
//            case R.id.selectVideo:
//                Lemage.startChooser(this, 100, false, 0, LemageScanner.STYLE_ONLY_VIDEO, null);
//                break;
//            case R.id.selectAll:
//                Lemage.startChooser(this, 100, false, 0, LemageScanner.STYLE_ALL, null);
//                break;
//            // 获取文件类型
//            case R.id.testType:
////                String pathN = "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=3742173999,1643624888&fm=27&gp=0.jpg";
////                String pathN = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
////                PathUtil.getInstance(this).getUrl(pathN);
////                setImageViewBitMap(image, pathN);
////                PreviewActivity.setCallback(new LemageResultCallback() {
////                    @Override
////                    public void willClose(List<String> imageUrlList, boolean isOriginal, List<FileObj> list) {
////
////                    }
////
////                    @Override
////                    public void closed(List<String> imageUrlList, boolean isOriginal) {
////
////                    }
////                });
//
//                ArrayList<String> listUrlAll = new ArrayList<String>();   // 全部文件路径
//                ArrayList<String> listUrlSelect = new ArrayList<String>();  // 已经选择文件路径
//                listUrlAll.add("lemage://album/localImage/storage/emulated/0/Download/2e2eb9389b504fc2b3b590d7efdde71191ef6d7d.jpeg");
//                listUrlAll.add("lemage://album/localVideo/storage/emulated/0/DCIM/Camera/20180629_131558.mp4");
//                listUrlAll.add("lemage://album/localImage/storage/emulated/0/Download/d6ca7bcb0a46f21fe099b7bdfc246b600d33aeab.jpeg");
//                listUrlAll.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=3742173999,1643624888&fm=27&gp=0.jpg");
////                listUrlAll.add("http://wxsnsdy.tc.qq.com/105/20210/snsdyvideodownload?filekey=30280201010421301f0201690402534804102ca905ce620b1241b726bc41dcff44e00204012882540400&bizid=1023&hy=SH&fileparam=302c020101042530230204136ffd93020457e3c4ff02024ef202031e8d7f02030f42400204045a320a0201000400");
//                listUrlAll.add("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
//                listUrlSelect.addAll(listUrlAll);
//                Intent intent = new Intent(this, PreviewActivity.class);
//                intent.putStringArrayListExtra("listAll", listUrlAll);
//                intent.putStringArrayListExtra("listSelect", listUrlSelect);
//                startActivity(intent);
//                break;
//
//        }
//    }


}
