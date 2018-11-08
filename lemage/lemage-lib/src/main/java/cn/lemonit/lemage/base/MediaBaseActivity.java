package cn.lemonit.lemage.base;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * @author zhaoguangyang
 * @date 2018/11/2
 * Describe:  多媒体类BaseActivity
 */
public class MediaBaseActivity extends AppCompatActivity {

    /**
     * 根视图布局
     */
    private RelativeLayout rootLayout;

    protected Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.support.v7.appcompat.R.style.Theme_AppCompat_DayNight_NoActionBar);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        getData();
        initRootView();
        init();
        addView(rootLayout);
        setContentView(rootLayout);
    }

    private void initRootView() {
        rootLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        rootLayout.setLayoutParams(params);
    }

    protected void init(){}

    protected void addView(RelativeLayout rootLayout){

    }

    protected void getData() {
        intent = getIntent();
    }
}
