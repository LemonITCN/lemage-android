package cn.lemonit.lemage.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import cn.lemonit.lemage.R;
import cn.lemonit.lemage.adapter.AlbumAdapter;
import cn.lemonit.lemage.adapter.PhotoAdapter;
import cn.lemonit.lemage.bean.Album;
import cn.lemonit.lemage.bean.FileObj;
import cn.lemonit.lemage.core.LemageScanner;
import cn.lemonit.lemage.interfaces.LemageResultCallback;
import cn.lemonit.lemage.interfaces.ScanCompleteCallback;
import cn.lemonit.lemage.util.ScreenUtil;
import cn.lemonit.lemage.view.AlbumSelectButton;
import cn.lemonit.lemage.view.DrawCircleTextButton;
import cn.lemonit.lemage.view.NavigationBar;
import cn.lemonit.lemage.view.OperationBar;

/**
 * Lemage主界面
 * 图片选择器界面
 *
 * @author liuri
 */
public class LemageActivity extends AppCompatActivity {

    private int style;

    private static LemageResultCallback callback;

    public static void setCallback(LemageResultCallback mCallback){
        callback = mCallback;
    }

    private final String TAG = "LemageActivity";
    /**
     * 允许选中最多的图片数量
     */
    private int maxChooseCount;
    /**
     * 是否需要原图按钮
     */
    private boolean needShowOriginalButton;
    /**
     * 主题颜色
     */
    private int themeColor;

    /**
     * 根视图布局
     */
    private RelativeLayout rootLayout;
    /**
     * 导航栏
     */
    private NavigationBar navigationBar;

    /**
     * 图片列表控件
     */
    private RecyclerView imageListView;
    /**
     * 底部操作栏
     */
    private OperationBar operationBar;
    /**
     * 图片列表控件的适配器
     */
    private PhotoAdapter photoAdapter;
    /**
     * 图片列表控件的适配器的数据源
     */
    private Album phototAdapterData;

    /**
     * 横向滑动的图片文件选择栏
     */
    private LinearLayout horizontalLayout;

    private RecyclerView albumRecyclerView;

    private AlbumAdapter mAlbumAdapter;

    /**
     * 底部条是否显示原图按钮, 默认显示
     */
    private int operationBarItemCount = 3;

    /**
     * 总图片量
     */
    public List<FileObj> listPhotoAll = new ArrayList<FileObj>();

    /**
     * 已经选中的图片量
     */
    public List<FileObj> listPhotoSelect = new ArrayList<FileObj>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.support.v7.appcompat.R.style.Theme_AppCompat_DayNight_NoActionBar);
        getData();
        setContentView(getRootLayout());
        getRootLayout().setBackgroundColor(Color.parseColor("#fafafa"));
        showPictureOrVideo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        LemageScannerNew.getInstance(this, style).destroyLoader();
    }

    private void getData() {
        Intent intent = getIntent();
        maxChooseCount = intent.getIntExtra("maxChooseCount", 0);
        needShowOriginalButton = intent.getBooleanExtra("needShowOriginalButton", false);
        themeColor = intent.getIntExtra("themeColor", 0);
        style = intent.getIntExtra("style", 0);
    }

    /**
     * 根据需要扫描图片还是视频并显示
     */
    private void showPictureOrVideo() {
        new LemageScanner(this, style).scanFile(new ScanCompleteCallback() {
            @Override
            public void scanComplete(Collection<Album> albumList) {
                if(albumList.size() > 0) {
                    for(Album album : albumList) {
                        Log.e(TAG, "文件名称 === " + album.getName());
                        Log.e(TAG, "文件路径 === " + album.getPath());
                        Log.e(TAG, "文件数量 === " + album.getFileList().size());
                        for(FileObj fileObj : album.getFileList()) {
                            Log.e(TAG, "子文件路径 ====== " + fileObj.getPath());
                        }
                    }
                }else {
                    Log.e(TAG, "albumList.size() =========== 0");
                }
                List<Album> list = sortList(albumList);
                changeAlbum(list.get(0));
                getHorizontal(list);
            }
        });

    }

    /**
     * 显示表格图片
     * @param album
     */
    private void changeAlbum(Album album) {
        getPhotoAdapter(album);
        getImageListView().setAdapter(photoAdapter);
    }

    /**
     * 给数据源排序，全部文件放在第一位
     */
    private ArrayList<Album> sortList(Collection<Album> albumList) {
        ArrayList<Album> mAlbumList = new ArrayList<Album>();
        Album albumNewAll = null;
        Iterator it = albumList.iterator();
        while (it.hasNext()) {
            Album album = (Album) it.next();
            if(album.getName().equals("全部照片")) {
                albumNewAll = album;
            }
            mAlbumList.add(album);
        }
        // 把全部照片放在第一位
        if(!mAlbumList.get(0).getName().equals("全部照片")) {
            mAlbumList.remove(albumNewAll);
            mAlbumList.add(0, albumNewAll);
        }
        return mAlbumList;
    }

    /**
     * 显示横向栏
     * @return
     */
    private void getHorizontal(Collection<Album> albumList) {
        if(mAlbumAdapter == null) {
            ArrayList<Album> mAlbumList = new ArrayList<Album>();
            Album albumNewAll = null;
            Iterator it = albumList.iterator();
            while (it.hasNext()) {
                Album album = (Album) it.next();
                if(album.getName().equals("全部照片")) {
                    albumNewAll = album;
                }
                mAlbumList.add(album);
            }
            // 把全部照片放在第一位
            if(!mAlbumList.get(0).getName().equals("全部照片")) {
                mAlbumList.remove(albumNewAll);
                mAlbumList.add(0, albumNewAll);
            }
            mAlbumAdapter = new AlbumAdapter(this, mAlbumList);
            mAlbumAdapter.setAlbumItemOnClickListener(mAlbumItemOnClickListener);
        }
        albumRecyclerView.setAdapter(mAlbumAdapter);
    }


    /**
     * 获取整个父布局
     * @return
     */
    public RelativeLayout getRootLayout() {
        if (rootLayout == null) {
            rootLayout = new RelativeLayout(this);
            rootLayout.addView(getImageListView());
//            rootLayout.addView(getNavigationBar());
            navigationBar = getNavigationBar();
            navigationBar.setId(R.id.navigationBar);
            rootLayout.addView(navigationBar);

            // 文件夹横向列表的高度定为屏幕高度的 1 / 7
            int height = ScreenUtil.getScreenHeight(this) / 7;
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
            layoutParams.addRule(RelativeLayout.BELOW, R.id.navigationBar);
            rootLayout.addView(getHorizontalListView(), layoutParams);
            horizontalLayout.setVisibility(View.GONE);

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
            operationBar = getOperationBar();
            operationBar.setLayoutParams(layoutParamsOperation);
            rootLayout.addView(operationBar);
        }
        return rootLayout;
    }

    /**
     * 获取整个的顶部条
     * @return
     */
    public NavigationBar getNavigationBar() {
        if(navigationBar == null) {
            navigationBar = new NavigationBar(this, 0);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    ScreenUtil.dp2px(this, 56)
            );
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
            navigationBar.setLayoutParams(layoutParams);
            navigationBar.setBackgroundColor(Color.argb(200, 0, 0, 0));
            navigationBar.setLeftViewClickListener(new NavigationBar.LeftViewClickListener() {
                @Override
                public void leftClickListener(AlbumSelectButton view) {
                    view.changeArrow();
                    if(horizontalLayout != null) {
                        if(horizontalLayout.isShown()) {
                            horizontalLayout.setVisibility(View.GONE);
                        }else {
                            horizontalLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
            navigationBar.setRightViewClickListener(new NavigationBar.RightViewClickListener() {
                @Override
                public void rightClickListener() {
                    if(operationBar != null) {
                        if(operationBarItemCount == 3) {
                            operationBarItemCount = 2;
                        }else {
                            operationBarItemCount = 3;
                        }
                        operationBar.setCount(operationBarItemCount);
                    }
                }
            });
        }
        return navigationBar;
    }

    /**
     * 横向滑动栏（选择文件夹）
     * @return
     */
    private LinearLayout getHorizontalListView() {
        if(horizontalLayout == null) {
            horizontalLayout = new LinearLayout(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
            horizontalLayout.setLayoutParams(layoutParams);
            horizontalLayout.setBackgroundColor(Color.argb(200, 0, 0, 0));
//            horizontalLayout.setBackgroundColor(Color.YELLOW);
            albumRecyclerView = getAlbumRecyclerView();
//            horizontalLayout.addView(getAlbumRecyclerView());
            horizontalLayout.addView(albumRecyclerView);
        }
        return horizontalLayout;
    }

    /**
     * 获取底部条
     */
    private OperationBar getOperationBar() {
        if(operationBar == null) {
            operationBar = new OperationBar(this, operationBarItemCount);
            operationBar.setOperationBarOnClickListener(mOperationBarOnClickListener);
        }
        return operationBar;
    }

    /**
     * 横向滑动栏里面的listview
     * @return
     */
    public RecyclerView getAlbumRecyclerView() {
        if(albumRecyclerView == null) {
            albumRecyclerView = new RecyclerView(this);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            );
            albumRecyclerView.setLayoutParams(layoutParams);
            LinearLayoutManager ms= new LinearLayoutManager(this);
            ms.setOrientation(LinearLayoutManager.HORIZONTAL);
            albumRecyclerView.setLayoutManager(ms);
        }
        return albumRecyclerView;
    }

    /**
     * 获取表格布局控件
     * @return
     */
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

    /**
     * 获取表格列表适配器
     * @param album
     * @return
     */
    public PhotoAdapter getPhotoAdapter(Album album) {
        if (photoAdapter == null) {
            // 此处不能直接用=赋值，否则是一个对象，item点击事件时会联动，应该new一个新对象，把属性传递过去即可
//            phototAdapterData = album;
            phototAdapterData = new Album(null, null);
            phototAdapterData.setName(album.getName());
            phototAdapterData.setPath(album.getPath());
            phototAdapterData.setFileList(album.getFileList());
//            phototAdapterData.setPhotoList(album.getPhotoList());
            photoAdapter = new PhotoAdapter(this, phototAdapterData);
            photoAdapter.setPhotoViewOnClickListener(mPhotoViewOnClickListener);
        }
        return photoAdapter;
    }

    /**
     * 表格图片列表item点击事件回调
     */
    private PhotoAdapter.PhotoViewOnClickListener mPhotoViewOnClickListener = new PhotoAdapter.PhotoViewOnClickListener() {
        @Override
        public void onClickSelectListener(List<FileObj> list) {
            if(listPhotoSelect == null) {
                listPhotoSelect = new ArrayList<FileObj>();
            }else {
                listPhotoSelect.clear();
            }
            listPhotoSelect.addAll(list);
        }

        /**
         * 点击了item直接进入预览
         * @param list
         * @param position
         */
        @Override
        public void onClickPreviewListener(List<FileObj> list, int position) {

            PreviewActivity.setCallback(callbackToPreview);
            listPhotoAll.clear();
            listPhotoAll.addAll(photoAdapter.getAlbumNew().getFileList());
            //  给预览界面传值，只传递URL
            ArrayList<String> listUrlAll = new ArrayList<String>();  // 全部文件路径
            ArrayList<String> listUrlSelect = new ArrayList<String>(); // 已经选择的文件路径
            for(FileObj fileObj : listPhotoAll) {
                String path = fileObj.getPath();
                if(path.endsWith(".mp4") || path.endsWith(".3gp")) {
                    path = "lemage://album/localVideo" + path;
                }else {
                    path = "lemage://album/localImage" + path;
                }
                listUrlAll.add(path);
                if(fileObj.getStatus() == 1) {
                    listUrlSelect.add(path);
                }
            }
            Intent intent = new Intent(LemageActivity.this, PreviewActivity.class);
            intent.putExtra("from", "all");
            intent.putExtra("position", position);
            intent.putStringArrayListExtra("listAll", listUrlAll);
            intent.putStringArrayListExtra("listSelect", listUrlSelect);
            startActivity(intent);

        }
    };


    /**
     * 图片文件夹横向列表item回调
     */
    private AlbumAdapter.AlbumItemOnClickListener mAlbumItemOnClickListener = new AlbumAdapter.AlbumItemOnClickListener(){
        @Override
        public void constantShow() {
            if(horizontalLayout != null && horizontalLayout.isShown()) {
                horizontalLayout.setVisibility(View.GONE);
                navigationBar.getAlbumSelectButton().changeArrow();
            }
        }

        @Override
        public void notifShow(Album mAlbum) {
            if(horizontalLayout != null && horizontalLayout.isShown()) {
                horizontalLayout.setVisibility(View.GONE);
                navigationBar.getAlbumSelectButton().changeText(mAlbum.getName());
                navigationBar.getAlbumSelectButton().changeArrow();
            }
            phototAdapterData.setName(mAlbum.getName());
            phototAdapterData.setPath(mAlbum.getPath());
            phototAdapterData.setFileList(mAlbum.getFileList());
            // 更改文件夹的时候，选中的都取消
            photoAdapter.clearSelectFile();
            photoAdapter.notifyDataSetChanged();
        }

    };

    /**
     * 底部条点击事件回调(预览)
     */
    private OperationBar.OperationBarOnClickListener mOperationBarOnClickListener = new OperationBar.OperationBarOnClickListener() {
        @Override
        public void leftButtonClick() {
            if(listPhotoSelect.size() > 0) {
                listPhotoAll.clear();
                listPhotoAll.addAll(photoAdapter.getAlbumNew().getFileList());
                // 传值
                ArrayList<String> listUrlAll = new ArrayList<String>();   // 全部文件路径
                ArrayList<String> listUrlSelect = new ArrayList<String>();  // 已经选择文件路径
                for(FileObj fileObj : listPhotoSelect) {
                    String path = fileObj.getPath();
                    if(path.endsWith(".mp4") || path.endsWith(".3gp")) {
                        path = "lemage://album/localVideo" + path;
                    }else {
                        path = "lemage://album/localImage" + path;
                    }
                    listUrlAll.add(path);
                    listUrlSelect.add(path);
                }
                PreviewActivity.setCallback(callbackToPreview);
                Intent intent = new Intent(LemageActivity.this, PreviewActivity.class);
                intent.putStringArrayListExtra("listAll", listUrlAll);
                intent.putStringArrayListExtra("listSelect", listUrlSelect);
                startActivity(intent);
            }else {
                Toast.makeText(LemageActivity.this, "您还没有选中照片，不能预览", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void centerButtonClick() {
//            Toast.makeText(LemageActivity.this, "原图", Toast.LENGTH_SHORT).show();
            DrawCircleTextButton mDrawCircleTextButton = operationBar.getCenterButton();
            if(mDrawCircleTextButton != null) {
                mDrawCircleTextButton.changeStatus();
            }
        }

        @Override
        public void rightButtonClick() {
            Toast.makeText(LemageActivity.this, "完成", Toast.LENGTH_SHORT).show();
        }
    };


    /**
     * 预览界面回调接口, 回来的参数是已经选中的图片路径集合
     */
    private LemageResultCallback callbackToPreview = new LemageResultCallback() {
        @Override
        public void willClose(List<String> imageUrlList, boolean isOriginal, List<FileObj> list) {

            for(int i = 0; i < phototAdapterData.getFileList().size(); i ++) {
                phototAdapterData.getFileList().get(i).setStatus(0);
                phototAdapterData.getFileList().get(i).setNumber(0);
                for(int m = 0; m < imageUrlList.size(); m ++) {
                    if(phototAdapterData.getFileList().get(i).getPath().equals(imageUrlList.get(m).substring("lemage://album/localImage".length()))) {
                        phototAdapterData.getFileList().get(i).setStatus(1);
                        phototAdapterData.getFileList().get(i).setNumber(m + 1);
                    }
                }
            }
            // 预览界面更改了选中图片，要同步数据，告知选择器和选择器中的adapter
            listPhotoSelect.clear();
            listPhotoSelect.addAll(list);
            photoAdapter.changeList(list);
            photoAdapter.notifyDataSetChanged();
        }

        @Override
        public void closed(List<String> imageUrlList, boolean isOriginal) {

        }
    };
}
