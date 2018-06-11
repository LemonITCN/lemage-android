package cn.lemonit.lemage.activity;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.Collection;

import cn.lemonit.lemage.adapter.PhotoAdapter;
import cn.lemonit.lemage.bean.Album;
import cn.lemonit.lemage.core.LemageScanner;
import cn.lemonit.lemage.interfaces.PhotoScanCompleteCallback;
import cn.lemonit.lemage.util.ScreenUtil;
import cn.lemonit.lemage.view.AlbumSelectButton;

/**
 * Lemage主界面
 * 图片选择器界面
 *
 * @author liuri
 */
public class LemageActivity extends AppCompatActivity {

    /**
     * 根视图布局
     */
    private RelativeLayout rootLayout;
    /**
     * 导航栏
     */
    private RelativeLayout navigationBar;
    /**
     * 图片列表控件
     */
    private RecyclerView imageListView;
    /**
     * 底部操作栏
     */
    private RelativeLayout operationBar;
    /**
     * 图片列表控件的适配器
     */
    private PhotoAdapter photoAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.support.v7.appcompat.R.style.Theme_AppCompat_DayNight_NoActionBar);
        setContentView(getRootLayout());
        getRootLayout().setBackgroundColor(Color.parseColor("#fafafa"));
        initPhoto();
    }

    /**
     * 初始化配置
     */
    private void initPhoto() {
        // 扫描所有大于5K的图片
        LemageScanner.scanAllPhoto(
                1024 * 5,
                true,
                this, new PhotoScanCompleteCallback() {
                    @Override
                    public void scanComplete(Collection<Album> albumList) {
                        System.out.println("扫描完毕！！！！！！ 相册数量：" + albumList.size());
                        changeAlbum((Album) albumList.toArray()[0]);
                    }
                });
    }

    private void changeAlbum(Album album) {
        getImageListView().setAdapter(new PhotoAdapter(this, album));
    }

    public RelativeLayout getRootLayout() {
        if (rootLayout == null) {
            rootLayout = new RelativeLayout(this);
            rootLayout.addView(getImageListView());
            rootLayout.addView(getNavigationBar());
        }
        return rootLayout;
    }

    public RelativeLayout getNavigationBar() {
        if (navigationBar == null) {
            navigationBar = new RelativeLayout(this);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    ScreenUtil.dp2px(this, 56)
            );
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            navigationBar.setLayoutParams(layoutParams);
            navigationBar.setBackgroundColor(Color.argb(200, 0, 0, 0));
            AlbumSelectButton albumSelectButton = new AlbumSelectButton(this, Color.WHITE);
            RelativeLayout.LayoutParams buttonLayoutParams = new RelativeLayout.LayoutParams(
                    ScreenUtil.dp2px(this, 200), ViewGroup.LayoutParams.MATCH_PARENT
            );
            buttonLayoutParams.addRule(RelativeLayout.ALIGN_LEFT, RelativeLayout.TRUE);
            buttonLayoutParams.leftMargin = ScreenUtil.dp2px(this, 14);
            albumSelectButton.setLayoutParams(buttonLayoutParams);
            navigationBar.addView(albumSelectButton);
        }
        return navigationBar;
    }

    public RecyclerView getImageListView() {
        if (imageListView == null) {
            imageListView = new RecyclerView(this);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            imageListView.setLayoutParams(layoutParams);
            imageListView.setLayoutManager(new RecyclerView.LayoutManager() {
                @Override
                public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                    return null;
                }
            });
            final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
            imageListView.setLayoutManager(gridLayoutManager);
        }
        return imageListView;
    }

    public PhotoAdapter getPhotoAdapter(Album album) {
        if (photoAdapter == null) {
            photoAdapter = new PhotoAdapter(this, album);
        }
        return photoAdapter;
    }
}
