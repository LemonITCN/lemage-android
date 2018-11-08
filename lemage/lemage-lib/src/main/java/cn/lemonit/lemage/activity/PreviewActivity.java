package cn.lemonit.lemage.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import cn.lemonit.lemage.activity_ui.PreviewUi;
import cn.lemonit.lemage.adapter.ImgPagerAdapter;
import cn.lemonit.lemage.base.MediaBaseActivity;
import cn.lemonit.lemage.been.FileObj;
import cn.lemonit.lemage.interfaces.LemageResultCallback;
import cn.lemonit.lemage.view.preview_view.PreviewOperationBar;
import cn.lemonit.lemage.view.select_view.CircleView;
import cn.lemonit.lemage.view.select_view.NavigationBar;
import cn.lemonit.lemage.view.select_view.PreviewBarLeftButton;

/**
 * @author zhaoguangyang
 * @date 2018/11/2
 * Describe:
 */
public class PreviewActivity extends MediaBaseActivity implements NavigationBar.PreviewLeftViewClickListener, NavigationBar.PreviewRightViewClickListener,
        PreviewOperationBar.PreviewOperationBarClickListener, ImgPagerAdapter.ImgOnClickListener {

    private String TAG = "PreviewActivity";

    private static LemageResultCallback callback;

    public static void setCallback(LemageResultCallback mCallback){
        callback = mCallback;
    }

    private ViewPager mViewPager;
    /**
     * 白色覆盖层
     */
    private View whiteView;
    /**
     * 顶部条
     */
    private NavigationBar mNavigationBar;
    /**
     * 底部条
     */
    private PreviewOperationBar mPreviewOperationBar;

    private ImgPagerAdapter mImgPagerAdapter;

    private int currentIndex = 1;
    /**
     * 主题颜色
     */
    private int themeColor;
    /**
     * 标识从哪里跳转（item还是预览按钮）
     */
    private String from;
    /**
     * 显示的初始position
     */
    private int fromPosition;
    /**
     * 允许最多可选择数量
     */
    private int maxChooseCount;
    /**
     * 进入图片预览器后默认首先展示的图片索引
     */
    private int showIndex;

    private ArrayList<FileObj> listPhotoAll = new ArrayList<FileObj>();

    private ArrayList<FileObj> listPhotoSelect = new ArrayList<FileObj>();

    private ArrayList<FileObj> listPhotoAdapterData = new ArrayList<FileObj>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void getData() {
        super.getData();
        maxChooseCount = intent.getIntExtra("maxChooseCount", 0);
        if(maxChooseCount > 99) {
            maxChooseCount = 99;
        }
        themeColor = intent.getIntExtra("themeColor", Color.GREEN);   // 默认绿色主题
        showIndex = intent.getIntExtra("showIndex", 0);
        from = intent.getStringExtra("from");
        // 得到item的position
        if(!TextUtils.isEmpty(from) && from.equals("all")) {
            fromPosition = intent.getIntExtra("position", 0);
        }
        listPhotoAll.clear();
        listPhotoSelect.clear();
        listPhotoAdapterData.clear();
        ArrayList<String> listAll = intent.getStringArrayListExtra("listAll");
        ArrayList<String> listSelect = intent.getStringArrayListExtra("listSelect");

        for(String url : listAll) {
            FileObj fileObj = new FileObj();
            fileObj.setPath(url);
            if(listSelect.contains(url)) {
                fileObj.setStatus(1);
            }else {
                fileObj.setStatus(0);
            }
            listPhotoAll.add(fileObj);
        }
        for(String url : listSelect) {
            FileObj fileObj = new FileObj();
            fileObj.setPath(url);
            fileObj.setStatus(1);
            listPhotoSelect.add(fileObj);
            listPhotoAdapterData.add(fileObj);
        }
    }

    @Override
    protected void init() {
        super.init();
        whiteView = PreviewUi.getWhiteView(this);
        mNavigationBar = PreviewUi.getNavigationBar(this, themeColor, from, fromPosition, maxChooseCount, listPhotoAll, listPhotoSelect);
        mPreviewOperationBar = PreviewUi.getPreviewOperationBar(this, themeColor);
        mImgPagerAdapter = PreviewUi.getImgPagerAdapter(this, from, listPhotoAll, listPhotoAdapterData);
        mViewPager = PreviewUi.getViewPager(this);
        mViewPager.addOnPageChangeListener(mOnPageChangeListener);
        mViewPager.setAdapter(mImgPagerAdapter);
        // 如果是从item跳转过来
        if(!TextUtils.isEmpty(from) && from.equals("all")) {
            mViewPager.setCurrentItem(fromPosition);
        }else {
            mViewPager.setCurrentItem(showIndex);
        }
    }

    @Override
    protected void addView(RelativeLayout rootLayout) {
        super.addView(rootLayout);
        rootLayout.addView(mViewPager);
        rootLayout.addView(whiteView);
        rootLayout.addView(mNavigationBar);
        rootLayout.addView(mPreviewOperationBar);
    }

    /**
     * 顶部条左侧按钮(返回)
     * @param view
     */
    @Override
    public void leftClickListener(PreviewBarLeftButton view) {
        finishCallback(false);
    }

    /**
     * 顶部条右侧按钮(圆圈)
     * @param view
     */
    @Override
    public void rightClickListener(CircleView view) {
        if(listPhotoSelect.size() >= maxChooseCount && view.getStatus() == 0) {
            return;
        }
        if(!TextUtils.isEmpty(from) && from.equals("all")){
            // 得到当前的photo
            FileObj photo = listPhotoAll.get(currentIndex - 1);
            // 未选中变选中，把当前的photo添加到listPhotoSelect，再根据所在的position显示number
            if(view.getStatus() == 0) {
                listPhotoSelect.add(photo);
                view.changeStatus(1, listPhotoSelect.size());
                photo.setStatus(1);
            }else {
                listPhotoSelect.remove(photo);
                view.changeStatus(0, 0);
                photo.setStatus(0);
            }
        }else {
            FileObj file = listPhotoAdapterData.get(currentIndex - 1);
            // 未选中变选中，把当前的photo添加到listPhotoSelect，再根据所在的position显示number
            if(view.getStatus() == 0) {
                listPhotoSelect.add(file);
                view.changeStatus(1, listPhotoSelect.size());
                file.setStatus(1);
            }else {
                listPhotoSelect.remove(file);
                view.changeStatus(0, 0);
                file.setStatus(0);
            }
        }
    }

    /**
     * 完成按钮事件
     */
    @Override
    public void previewOperationBarClick() {
        finishCallback(true);
    }

    /**
     * page  adapter回调接口
     */
    @Override
    public void imgOnClick() {
        if(mNavigationBar.isShown()) {
            mNavigationBar.setVisibility(View.GONE);
        }else {
            mNavigationBar.setVisibility(View.VISIBLE);
        }
        if(mPreviewOperationBar.isShown()) {
            mPreviewOperationBar.setVisibility(View.GONE);
        }else {
            mPreviewOperationBar.setVisibility(View.VISIBLE);
        }
    }

    public ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {
        /**
         * page 开始翻页
         * @param position
         * @param positionOffset
         * @param positionOffsetPixels
         */
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        /**
         * 翻页完成
         * @param position
         */
        @Override
        public void onPageSelected(int position) {
            showIndex = position;
            updataPageUi(position);
        }

        /**
         * 滑动状态，  0 没有滑动或者停滞状态   1 正在滑动   2 滑动停止
         * @param i
         */
        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    /**
     * 翻页时需要更新的UI
     */
    private void updataPageUi(int position) {
        // 左侧按钮更新
        PreviewBarLeftButton previewBarLeftButton = mNavigationBar.getPreviewBarLeftButton();
        if(previewBarLeftButton == null) {
            return;
        }
        currentIndex = position + 1; // 当前的item的position

        if(!TextUtils.isEmpty(from) && from.equals("all")) {
            previewBarLeftButton.changeText(listPhotoAll.size(), currentIndex);
        }else {
            previewBarLeftButton.changeText(listPhotoAdapterData.size(), currentIndex);
        }
        // 右侧按钮更新
        CircleView mCircleView = mNavigationBar.getCircleView();

        if(!TextUtils.isEmpty(from) && from.equals("all")) {
            // 选中状态
            FileObj fileObj = listPhotoAll.get(position);
            if(fileObj.getStatus() == 1) {
                int number = listPhotoSelect.indexOf(fileObj) + 1;
                mCircleView.changeStatus(1, number);
            }else {
                mCircleView.changeStatus(0, 0);
            }
        }else {
            FileObj fileObj = listPhotoAdapterData.get(position);
            mCircleView.changeStatus(fileObj.getStatus(), listPhotoSelect.indexOf(fileObj) + 1);
        }
        // 翻页时判断是否显示白色覆盖层
        if(listPhotoSelect.size() >= maxChooseCount && mCircleView.getStatus() == 0) {
            whiteView.setAlpha(0.5f);
        }else {
            whiteView.setAlpha(0);
        }
    }

    /**
     * 返回（如果是点击完成按钮，返回选中界面的上一个界面）
     */
    private void finishCallback(boolean back) {
        List<String> list = new ArrayList<String>();
        for(FileObj fileObj : listPhotoSelect) {
            if(fileObj.getStatus() == 1) {
                String path = fileObj.getPath();
                // 如果路径是以http开头，证明网络资源没有下载完成，此时不回传
                if(!path.startsWith("http")) {
                    list.add(fileObj.getPath());
                }
            }
        }
        if(back) {
            callback.closed(list, true, listPhotoSelect);
        }else {
            callback.willClose(list, true, listPhotoSelect);
        }
        PreviewActivity.this.finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("showIndex", showIndex);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        showIndex = savedInstanceState.getInt("showIndex");
        mViewPager.setCurrentItem(showIndex);
    }

    /**
     * 返回键的处理
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishCallback(false);
    }
}
