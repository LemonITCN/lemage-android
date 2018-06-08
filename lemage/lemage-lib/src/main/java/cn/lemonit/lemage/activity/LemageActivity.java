package cn.lemonit.lemage.activity;

import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.print.PrinterId;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.Collection;

import cn.lemonit.lemage.adapter.PhotoAdapter;
import cn.lemonit.lemage.bean.Album;
import cn.lemonit.lemage.interfaces.PhotoScanCompleteCallback;
import cn.lemonit.lemage.core.LemageScanner;

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

    private static final int LOADER_ALL = 0;         // 获取所有图片
    private static final int LOADER_CATEGORY = 1;    // 获取某个文件夹中的所有图片

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
                    dip2px(56)
            );
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            navigationBar.setId(100);
            navigationBar.setLayoutParams(layoutParams);
            navigationBar.setBackgroundColor(Color.parseColor("#444444"));
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
            layoutParams.addRule(RelativeLayout.BELOW, getNavigationBar().getId());
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            imageListView.setLayoutParams(layoutParams);
            imageListView.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                    outRect.left = 8;
                    outRect.top = 10;
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

    public int dip2px(float dpValue) {
        final float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
