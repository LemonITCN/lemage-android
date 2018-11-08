package cn.lemonit.lemage.activity_ui;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import cn.lemonit.lemage.adapter.PhotoAdapter;
import cn.lemonit.lemage.been.Album;
import cn.lemonit.lemage.util.ScreenUtil;
import cn.lemonit.lemage.view.select_view.NavigationBar;
import cn.lemonit.lemage.view.select_view.OperationBar;

/**
 * @author zhaoguangyang
 * @date 2018/11/5
 * Describe:
 */
public class SelectUi {
    /**
     * 主题颜色
     */
//    public static int themeColor;
    /**
     * 底部条是否显示原图按钮, 默认显示
     */
    public static int operationBarItemCount = 3;
    /**
     * 是否需要原图按钮
     */
//    public static boolean needShowOriginalButton;
    /**
     * 允许选中最多的图片数量
     */
//    public static int maxChooseCount;

//    public static int style;

    /**
     * 获取表格布局控件
     * @param context
     * @return
     */
    public static RecyclerView getImageListView(Context context) {
        RecyclerView imageListView = new RecyclerView(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        imageListView.setLayoutParams(layoutParams);
        imageListView.setLayoutManager(new RecyclerView.LayoutManager() {
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return null;
            }
        });
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 4);
        imageListView.setLayoutManager(gridLayoutManager);
        return imageListView;
    }

    /**
     * 获取表格列表适配器
     * @param context
     * @return
     */
    public static PhotoAdapter getPhotoAdapter(Context context, Album data, int themeColor, int maxChooseCount, int style) {
        PhotoAdapter photoAdapter = new PhotoAdapter(context, data, style, themeColor, maxChooseCount);
        return photoAdapter;
    }

    /**
     * 获取顶部条
     * @param context
     * @return
     */
    public static NavigationBar getNavigationBar(Context context, int themeColor) {
        NavigationBar navigationBar = new NavigationBar(context, 0, themeColor);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ScreenUtil.dp2px(context, 56));
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        navigationBar.setLayoutParams(layoutParams);
        navigationBar.setBackgroundColor(Color.argb(200, 0, 0, 0));
        navigationBar.setLeftViewClickListener((NavigationBar.LeftViewClickListener) context);
        navigationBar.setRightViewClickListener((NavigationBar.RightViewClickListener) context);
        return navigationBar;
    }

    /**
     * 横向滑动栏
     * @param context
     * @return
     */
    public static LinearLayout getHorizontalLayout(Context context) {
        LinearLayout horizontalLayout = new LinearLayout(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ScreenUtil.dp2px(context, 100));
        horizontalLayout.setLayoutParams(layoutParams);
        horizontalLayout.setBackgroundColor(Color.argb(200, 0, 0, 0));
        return horizontalLayout;
    }

    /**
     * 获取横向滑动栏里面的横向listview
     * @param context
     * @return
     */
    public static RecyclerView getHorizontalListView(Context context) {
        RecyclerView albumRecyclerView = new RecyclerView(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        albumRecyclerView.setLayoutParams(layoutParams);
        LinearLayoutManager ms = new LinearLayoutManager(context);
        ms.setOrientation(LinearLayoutManager.HORIZONTAL);
        albumRecyclerView.setLayoutManager(ms);
        return albumRecyclerView;
    }

    /**
     * 获取底部条
     * @param context
     * @return
     */
    public static OperationBar getOperationBar(Context context, int themeColor, int operationBarItemCount) {
        OperationBar operationBar = new OperationBar(context, operationBarItemCount, themeColor);
        int operationHeight = ScreenUtil.getScreenHeight(context) / 15;
        RelativeLayout.LayoutParams layoutParamsOperation = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, operationHeight);
        layoutParamsOperation.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        layoutParamsOperation.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        layoutParamsOperation.bottomMargin = operationHeight;
        operationBar.setLayoutParams(layoutParamsOperation);
        operationBar.setOperationBarOnClickListener((OperationBar.OperationBarOnClickListener) context);
        return operationBar;
    }


    /**
     * 给数据源排序，全部文件放在第一位
     */
    public static ArrayList<Album> sortList(Collection<Album> albumList) {
        ArrayList<Album> mAlbumList = new ArrayList<Album>();
        Album albumNewAll = null;
        Iterator it = albumList.iterator();
        while (it.hasNext()) {
            Album album = (Album) it.next();
            if (album.getName().equals("全部照片")) {
                albumNewAll = album;
            }
            mAlbumList.add(album);
        }
        // 把全部照片放在第一位
        if (!mAlbumList.get(0).getName().equals("全部照片")) {
            mAlbumList.remove(albumNewAll);
            mAlbumList.add(0, albumNewAll);
        }
        return mAlbumList;
    }
}
