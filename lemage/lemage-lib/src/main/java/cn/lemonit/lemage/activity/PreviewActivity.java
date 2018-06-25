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
import cn.lemonit.lemage.bean.Photo;
import cn.lemonit.lemage.interfaces.LemageResultCallback;
import cn.lemonit.lemage.util.ScreenUtil;
import cn.lemonit.lemage.view.AlbumSelectButton;
import cn.lemonit.lemage.view.CircleView;
import cn.lemonit.lemage.view.NavigationBar;
import cn.lemonit.lemage.view.OperationBar;
import cn.lemonit.lemage.view.PreviewBarLeftButton;
import cn.lemonit.lemage.view.PreviewOperationBar;

/**
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
     * 可选择图片
     */
    private ArrayList<Photo> listPhotoAll = new ArrayList<Photo>();

    private ArrayList<Photo> listPhotoSelect = new ArrayList<Photo>();

    private ArrayList<Photo> listPhotoAdapterData = new ArrayList<Photo>();

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
    }

    private void getData() {
        Intent intent = getIntent();
        from = intent.getStringExtra("from");
        // 得到item的position
        if(!TextUtils.isEmpty(from) && from.equals("all")) {
            fromPosition = intent.getIntExtra("position", 0);
        }
        listPhotoAll.clear();
        listPhotoAll.addAll(LemageActivity.listPhotoAll);
        listPhotoSelect.clear();
        listPhotoSelect.addAll(LemageActivity.listPhotoSelect);
        listPhotoAdapterData.clear();
        listPhotoAdapterData.addAll(LemageActivity.listPhotoSelect);
    }

    private void initView() {
        getRootLayout();
        getViewPager();
        getNavigationBar();
        getOperationBar();
    }

    private void addView() {
        rootLayout.addView(mViewPager);
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
            if(!TextUtils.isEmpty(from)) {
                if(from.equals("all")) {
                    mImgPagerAdapter = new ImgPagerAdapter(this, listPhotoAll);
                }else {
                    mImgPagerAdapter = new ImgPagerAdapter(this, listPhotoAdapterData);
                }
                mImgPagerAdapter.setImgOnClickListener(imgOnClickListener);
                mViewPager.setAdapter(mImgPagerAdapter);
            }
        }
    }


    private void getNavigationBar() {
        if(mNavigationBar == null) {
            mNavigationBar = new NavigationBar(this, 1);
            if(!TextUtils.isEmpty(from)) {
                if(from.equals("all")) {
                    mNavigationBar.changeText(listPhotoAll.size(), 1);
                }else {
                    mNavigationBar.changeText(listPhotoSelect.size(), 1);
                }
            }
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ScreenUtil.dp2px(this, 56));
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            mNavigationBar.setLayoutParams(layoutParams);
            mNavigationBar.setBackgroundColor(Color.argb(200, 0, 0, 0));
            // 顶部条左侧按钮点击事件
            mNavigationBar.setPreviewLeftViewClickListener(new NavigationBar.PreviewLeftViewClickListener() {
                @Override
                public void leftClickListener(PreviewBarLeftButton view) {
                    List<String> list = new ArrayList<String>();

                    for(Photo photo : listPhotoSelect) {
                        if(photo.getStatus() == 1) {
                            list.add(photo.getPath());
                        }
                    }
                    callback.willClose(list, true, listPhotoSelect);
                    PreviewActivity.this.finish();
                }
            });
            // 顶部条右侧按钮点击事件
            mNavigationBar.setPreviewRightViewClickListener(new NavigationBar.PreviewRightViewClickListener() {
                @Override
                public void rightClickListener(CircleView view) {
                    if(!TextUtils.isEmpty(from)) {
                        if(from.equals("all")) {
                            Photo photo = listPhotoAll.get(currentIndex - 1);
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
                            Photo photo = listPhotoAdapterData.get(currentIndex - 1);
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
                        }

                    }
                }
            });
            // 预览界面顶部条右侧按钮的初始状态（有时是选中状态，有时是未选中状态）,之后的翻页状态会自动刷新
            if(!TextUtils.isEmpty(from)) {
                if(from.equals("all")) {
                    mNavigationBar.changeTextCircle(listPhotoAll.get(fromPosition).getStatus(), listPhotoAll.get(fromPosition).getNumber());
                }else {
                    mNavigationBar.changeTextCircle(listPhotoSelect.get(0).getStatus(), 1);
                }
            }

        }
    }

    /**
     * 获取底部条
     */
    private PreviewOperationBar getOperationBar() {
        if(mPreviewOperationBar == null) {
            mPreviewOperationBar = new PreviewOperationBar(this);
//            mOperationBar.setOperationBarOnClickListener(mOperationBarOnClickListener);
        }
        return mPreviewOperationBar;
    }

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
            if(!TextUtils.isEmpty(from)) {
                if(from.equals("all")) {
                    previewBarLeftButton.changeText(listPhotoAll.size(), currentIndex);
                }else {
                    previewBarLeftButton.changeText(listPhotoAdapterData.size(), currentIndex);
                }
            }
            // 右侧按钮更新
            CircleView mCircleView = mNavigationBar.getCircleView();
            if(!TextUtils.isEmpty(from)) {
                if(from.equals("all")) {
                    // 选中状态
                    Photo photo = listPhotoAll.get(position);
                    if(photo.getStatus() == 1) {
                        mCircleView.changeStatus(1, listPhotoSelect.indexOf(photo) + 1);
                    }else {
                        mCircleView.changeStatus(0, 0);
                    }
                }else {
                    Photo photo = listPhotoAdapterData.get(position);
                    mCircleView.changeStatus(photo.getStatus(), listPhotoSelect.indexOf(photo) + 1);
                }
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

}
