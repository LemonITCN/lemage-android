package cn.lemonit.lemage.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cn.lemonit.lemage.adapter.ImgPagerAdapter;
import cn.lemonit.lemage.bean.FileObj;
import cn.lemonit.lemage.bean.Photo;
import cn.lemonit.lemage.bean.Video;
import cn.lemonit.lemage.interfaces.LemageResultCallback;
import cn.lemonit.lemage.util.FileUtil;
import cn.lemonit.lemage.util.PathUtil;
import cn.lemonit.lemage.util.ScreenUtil;
import cn.lemonit.lemage.view.AlbumSelectButton;
import cn.lemonit.lemage.view.CircleView;
import cn.lemonit.lemage.view.NavigationBar;
import cn.lemonit.lemage.view.OperationBar;
import cn.lemonit.lemage.view.PreviewBarLeftButton;
import cn.lemonit.lemage.view.PreviewOperationBar;

/**
 * 预览界面
 * @author: zhaoguangyang
 */
public class PreviewActivity extends AppCompatActivity {

    private static LemageResultCallback callback;

    public static void setCallback(LemageResultCallback mCallback){
        callback = mCallback;
    }

    private final String TAG = "PreviewActivity";

    /**
     * 根视图布局
     */
    private RelativeLayout rootLayout;
    /**
     * 顶部条
     */
    private NavigationBar mNavigationBar;
    /**
     * 底部条
     */
    private PreviewOperationBar mPreviewOperationBar;

    private ViewPager mViewPager;
    /**
     * 白色覆盖层
     */
    private View whiteView;
    /**
     * 可选择图片
     */
    private ArrayList<FileObj> listPhotoAll = new ArrayList<FileObj>();

    private ArrayList<FileObj> listPhotoSelect = new ArrayList<FileObj>();

    private ArrayList<FileObj> listPhotoAdapterData = new ArrayList<FileObj>();

    private ImgPagerAdapter mImgPagerAdapter;

    private int currentIndex = 1;
    /**
     * 从预览按钮跳转过来还是item
     */
    private String from;
    /**
     * 如果是从item跳转过来，点击的position
     */
    private int fromPosition;
    /**
     * 主题颜色
     */
    private int mColor;
    /**
     * 允许最多可选择数量
     */
    private int maxChooseCount;
    /**
     * 进入图片预览器后默认首先展示的图片索引
     */
    private int showIndex;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.support.v7.appcompat.R.style.Theme_AppCompat_DayNight_NoActionBar);
        getData();
        initView();
        addView();
        setContentView(rootLayout);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 停止正在进行的下载任务
//        mImgPagerAdapter.stopDownLoadTask();
        // 清空网络临时文件夹
//        FileUtil.getInstance(this).clearNetFile();
    }

    private void getData() {
        Intent intent = getIntent();
        maxChooseCount = intent.getIntExtra("maxChooseCount", 0);
        if(maxChooseCount > 99) {
            maxChooseCount = 99;
        }
        mColor = intent.getIntExtra("themeColor", Color.GREEN);   // 默认绿色主题
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

    private void initView() {
        getRootLayout();
        getViewPager();
        getNavigationBar();
        getOperationBar();
    }

    private void addView() {
        rootLayout.addView(mViewPager);
        // 添加白色覆盖层
        addWhitView();
        rootLayout.addView(mNavigationBar);

        // 底部条适配说明：
        // 高=屏幕高度的 1 /10
        // 每个块的宽 = 屏幕宽度 1 / 5
        // 底部间距 = 高
        int operationHeight = ScreenUtil.getScreenHeight(this) / 15;
//            RelativeLayout.LayoutParams layoutParamsOperation = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, ScreenUtil.dp2px(this, 50));
        RelativeLayout.LayoutParams layoutParamsOperation = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, operationHeight);
        layoutParamsOperation.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        layoutParamsOperation.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
//            layoutParamsOperation.bottomMargin = 50;
        layoutParamsOperation.bottomMargin = operationHeight;
        mPreviewOperationBar.setLayoutParams(layoutParamsOperation);
        rootLayout.addView(mPreviewOperationBar);

        // 如果是从item跳转过来
        if(!TextUtils.isEmpty(from) && from.equals("all")) {
            mViewPager.setCurrentItem(fromPosition);
        }else {
            mViewPager.setCurrentItem(showIndex);
        }
    }

    private void getRootLayout() {
        if(rootLayout == null) {
            rootLayout = new RelativeLayout(this);
        }
    }

    private void getViewPager() {
        if(mViewPager == null) {
            mViewPager = new ViewPager(this);
            mViewPager.addOnPageChangeListener(mOnPageChangeListener);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            mViewPager.setLayoutParams(layoutParams);
            if(!TextUtils.isEmpty(from) && from.equals("all")) {
                mImgPagerAdapter = new ImgPagerAdapter(this, listPhotoAll);
            }else {
                mImgPagerAdapter = new ImgPagerAdapter(this, listPhotoAdapterData);
            }
            mImgPagerAdapter.setImgOnClickListener(imgOnClickListener);
            mViewPager.setAdapter(mImgPagerAdapter);
        }
    }


    private void getNavigationBar() {
        if(mNavigationBar == null) {
            mNavigationBar = new NavigationBar(this, 1, mColor);
            if(!TextUtils.isEmpty(from) && from.equals("all")) {
                mNavigationBar.changeText(listPhotoAll.size(), 1);
            }else {
                mNavigationBar.changeText(listPhotoSelect.size(), 1);
            }
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ScreenUtil.dp2px(this, 56));
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            mNavigationBar.setLayoutParams(layoutParams);
            mNavigationBar.setBackgroundColor(Color.argb(200, 0, 0, 0));
            // 顶部条左侧按钮点击事件
            mNavigationBar.setPreviewLeftViewClickListener(new NavigationBar.PreviewLeftViewClickListener() {
                @Override
                public void leftClickListener(PreviewBarLeftButton view) {
//                    List<String> list = new ArrayList<String>();
//
//                    for(FileObj fileObj : listPhotoSelect) {
//                        if(fileObj.getStatus() == 1) {
//                            String path = fileObj.getPath();
//                            // 如果路径是以http开头，证明网络资源没有下载完成，此时不回传
//                            if(!path.startsWith("http")) {
//                                list.add(fileObj.getPath());
//                            }
//                        }
//                    }
//                    Log.e(TAG, "选中的 ==================== " + list.size());
//                    callback.willClose(list, true, listPhotoSelect);
//                    PreviewActivity.this.finish();
                    finishCallback();
                }
            });
            // 顶部条右侧按钮点击事件
            mNavigationBar.setPreviewRightViewClickListener(new NavigationBar.PreviewRightViewClickListener() {
                @Override
                public void rightClickListener(CircleView view) {
                    if(listPhotoSelect.size() >= maxChooseCount && view.getStatus() == 0) {
                        return;
                    }
                    if(!TextUtils.isEmpty(from) && from.equals("all")){
                        // 得到当前的photo
                        FileObj photo = listPhotoAll.get(currentIndex - 1);
                        Log.e(TAG, "photo.getPath() =============== " + photo.getPath());
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
            });
            // 预览界面顶部条右侧按钮的初始状态（有时是选中状态，有时是未选中状态）,之后的翻页状态会自动刷新
            if(!TextUtils.isEmpty(from) && from.equals("all")) {
                // 点哪个item进来就从哪个item开始预览
                FileObj fileObj = listPhotoAll.get(fromPosition);
//                int number = fileObj.getNumber();
                int number = listPhotoSelect.indexOf(listPhotoAll.get(fromPosition)) + 1;
                mNavigationBar.changeTextCircle(listPhotoAll.get(fromPosition).getStatus(), number);
            }else {
                mNavigationBar.changeTextCircle(listPhotoSelect.get(0).getStatus(), 1);
            }

            // 允许选择的图片数量，如果传<=0的数，表示关闭选择功能（选择器右上角是否有选择按钮）
            if(maxChooseCount < 1) {
                mNavigationBar.hideSelectButton();
            }
        }
    }

    /**
     * 获取底部条
     */
    private PreviewOperationBar getOperationBar() {
        if(mPreviewOperationBar == null) {
            mPreviewOperationBar = new PreviewOperationBar(this, mColor);
            mPreviewOperationBar.setPreviewOperationBarClickListener(mPreviewOperationBarClickListener);
        }
        return mPreviewOperationBar;
    }

    /**
     * 完成按钮回掉
     */
    private PreviewOperationBar.PreviewOperationBarClickListener mPreviewOperationBarClickListener = new PreviewOperationBar.PreviewOperationBarClickListener() {
        @Override
        public void previewOperationBarClick() {
            finishCallback();
        }
    };

    private ImgPagerAdapter.ImgOnClickListener imgOnClickListener = new ImgPagerAdapter.ImgOnClickListener() {
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
    };

    /**
     * viewpager翻页监听
     */
    private ViewPager.OnPageChangeListener mOnPageChangeListener = new ViewPager.OnPageChangeListener() {

        /**
         * 开始翻页时调用此方法，直到翻页结束
         * @param position
         * @param positionOffset
         * @param positionOffsetPixels
         */
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//            Log.e(TAG, "翻页监听 onPageScrolled   position ==== " + position + "  positionOffset ==== " + positionOffset + "   positionOffsetPixels ====== " + positionOffsetPixels);
        }

        /**
         * 翻页完成时调用
         * @param position
         */
        @Override
        public void onPageSelected(int position) {
            // 左侧按钮更新
            PreviewBarLeftButton previewBarLeftButton = mNavigationBar.getPreviewBarLeftButton();
            if(previewBarLeftButton == null) return;
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
                    mCircleView.changeStatus(1, listPhotoSelect.indexOf(fileObj) + 1);
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
         * 滑动状态，  0 没有滑动或者停滞状态   1 正在滑动   2 滑动停止
         * @param state
         */
        @Override
        public void onPageScrollStateChanged(int state) {
//            int currentIndex = state % listPath.size();
        }
    };


    /**
     * 添加白色覆盖层
     */
    private void addWhitView() {
        whiteView = new View(this);
        RelativeLayout.LayoutParams paramsWhite = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        whiteView.setLayoutParams(paramsWhite);
        whiteView.setBackgroundColor(Color.WHITE);
        whiteView.setAlpha(0);
        rootLayout.addView(whiteView);
    }

    /**
     * 返回键的处理
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishCallback();
    }

    /**
     * 点击完成，顶部条左侧按钮，返回键都回掉的方法
     */
    private void finishCallback() {
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
        Log.e(TAG, "选中的 ==================== " + list.size());
        callback.willClose(list, true, listPhotoSelect);
        PreviewActivity.this.finish();
    }
}
