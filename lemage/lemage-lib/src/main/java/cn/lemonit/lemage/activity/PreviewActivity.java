package cn.lemonit.lemage.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.RelativeLayout;

import cn.lemonit.lemage.util.ScreenUtil;
import cn.lemonit.lemage.view.AlbumSelectButton;
import cn.lemonit.lemage.view.NavigationBar;

public class PreviewActivity extends AppCompatActivity {

    /**
     * 根视图布局
     */
    private RelativeLayout rootLayout;
    /**
     * 顶部条
     */
    private NavigationBar mNavigationBar;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.support.v7.appcompat.R.style.Theme_AppCompat_DayNight_NoActionBar);
        initView();
        addView();
        setContentView(rootLayout);
    }

    private void initView() {
        getRootLayout();
        getViewPager();
        getNavigationBar();
    }

    private void addView() {
        rootLayout.addView(mViewPager);
        rootLayout.addView(mNavigationBar);
    }

    private void getRootLayout() {
        if(rootLayout == null) {
            rootLayout = new RelativeLayout(this);
        }
    }

    private void getViewPager() {
        if(mViewPager == null) {
            mViewPager = new ViewPager(this);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            mViewPager.setLayoutParams(layoutParams);
        }
    }

    private void getNavigationBar() {
        if(mNavigationBar == null) {
            mNavigationBar = new NavigationBar(this, 1);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ScreenUtil.dp2px(this, 56));
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            mNavigationBar.setLayoutParams(layoutParams);
            mNavigationBar.setBackgroundColor(Color.argb(200, 0, 0, 0));
            mNavigationBar.setLeftViewClickListener(new NavigationBar.LeftViewClickListener() {
                @Override
                public void leftClickListener(AlbumSelectButton view) {
                    PreviewActivity.this.finish();
                }
            });
        }
    }
}
