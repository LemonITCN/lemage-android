package cn.lemonit.lemage.activity;

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
import cn.lemonit.lemage.observer.EventTool;
import cn.lemonit.lemage.util.ScreenUtil;
import cn.lemonit.lemage.view.AlbumSelectButton;
import cn.lemonit.lemage.view.CircleView;
import cn.lemonit.lemage.view.NavigationBar;
import cn.lemonit.lemage.view.OperationBar;
import cn.lemonit.lemage.view.PreviewBarLeftButton;
import cn.lemonit.lemage.view.PreviewOperationBar;

public class PreviewActivity extends AppCompatActivity {

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
     * 数据源
     */
    private ArrayList<Photo> listPhoto = new ArrayList<Photo>();
    private List<String> listPath = new ArrayList<String>();
    private List<String> listName = new ArrayList<String>();

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
        from = getIntent().getExtras().getString("from");
        // 得到item的position
        if(!TextUtils.isEmpty(from)) {
            fromPosition = getIntent().getExtras().getInt("position");
        }
        if(listPhoto == null) {
            listPhoto = new ArrayList<Photo>();
        }else {
            listPhoto.clear();
        }
//        listPhoto.addAll((ArrayList<Photo>) getIntent().getExtras().getSerializable("photos"));
        listPhoto.addAll(LemageActivity.selectListPhoto);
        listPath.addAll(getIntent().getExtras().getStringArrayList("paths"));
        listName.addAll(getIntent().getExtras().getStringArrayList("names"));
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
        if(!TextUtils.isEmpty(from) && from.equals("item")) {
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

            mImgPagerAdapter = new ImgPagerAdapter(this, listPhoto, (ArrayList<String>) listPath, (ArrayList<String>) listName);
            mImgPagerAdapter.setImgOnClickListener(imgOnClickListener);
            mViewPager.setAdapter(mImgPagerAdapter);
        }
    }


    private void getNavigationBar() {
        if(mNavigationBar == null) {
            mNavigationBar = new NavigationBar(this, 1);
            mNavigationBar.changeText(listPath.size(), 1);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ScreenUtil.dp2px(this, 56));
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            mNavigationBar.setLayoutParams(layoutParams);
            mNavigationBar.setBackgroundColor(Color.argb(200, 0, 0, 0));
            mNavigationBar.setPreviewLeftViewClickListener(new NavigationBar.PreviewLeftViewClickListener() {
                @Override
                public void leftClickListener(PreviewBarLeftButton view) {
//                    mBackLemageActivityListener.toBack();
                    EventTool.getInstance().post(listPhoto);
                    PreviewActivity.this.finish();
                }
            });
            mNavigationBar.setPreviewRightViewClickListener(new NavigationBar.PreviewRightViewClickListener() {
                @Override
                public void rightClickListener(CircleView view) {
                    int selectCount = 0;  // 这个position之前的item有多少个是选中的
                    for(int i = 0; i < listPhoto.size(); i ++) {
                        if(listPhoto.get(i).getStatus() == 1) {
                            selectCount ++;
                        }
                    }
                    if(view.getStatus() == 0) {
                        view.changeStatus(1, selectCount + 1);
//                        view.changeStatus(1, currentIndex);
                        listPhoto.get(currentIndex - 1).setStatus(1);  // 改变选中状态
                    }else {
                        view.changeStatus(0, selectCount + 1);
//                        view.changeStatus(0, currentIndex);
                        listPhoto.get(currentIndex - 1).setStatus(0);
                    }
                }
            });
            // 预览界面顶部条右侧按钮的初始状态（有时是选中状态，有时是未选中状态）,之后的翻页状态会自动刷新
            mNavigationBar.changeTextCircle(listPhoto.get(0).getStatus());
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
            previewBarLeftButton.changeText(listPath.size(), position + 1);
            currentIndex = position + 1; // 当前的item的position
            // 右侧按钮更新
            CircleView mCircleView = mNavigationBar.getCircleView();
            if(mCircleView == null) return;
//            mCircleView.changeStatus(1, currentIndex);
            int selectCount = 0;  // 这个position之前的item有多少个是选中的

            Log.e(TAG, "selectCount      currentIndex========== " + currentIndex);
            for(int i = 0; i < position; i ++) {
                if(listPhoto.get(i).getStatus() == 1) {
                    selectCount ++;
                }
            }
//            mCircleView.changeStatus(listPhoto.get(position).getStatus(), currentIndex);
            mCircleView.changeStatus(listPhoto.get(position).getStatus(), selectCount + 1);
        }

        /**
         * 滑动状态，  0 没有滑动或者停滞状态   1 正在滑动   2 滑动停止
         * @param state
         */
        @Override
        public void onPageScrollStateChanged(int state) {
            int currentIndex = state % listPath.size();
        }
    };

}
