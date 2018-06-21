package cn.lemonit.lemage.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import cn.lemonit.lemage.R;
import cn.lemonit.lemage.adapter.AlbumAdapter;
import cn.lemonit.lemage.adapter.PhotoAdapter;
import cn.lemonit.lemage.bean.Album;
import cn.lemonit.lemage.bean.Photo;
import cn.lemonit.lemage.core.LemageScanner;
import cn.lemonit.lemage.interfaces.PhotoScanCompleteCallback;
import cn.lemonit.lemage.observer.EventTool;
import cn.lemonit.lemage.observer.Observer;
import cn.lemonit.lemage.util.ScreenUtil;
import cn.lemonit.lemage.view.AlbumSelectButton;
import cn.lemonit.lemage.view.DrawCircleTextButton;
import cn.lemonit.lemage.view.NavigationBar;
import cn.lemonit.lemage.view.OperationBar;
import cn.lemonit.lemage.view.PhotoView;

import static android.view.View.generateViewId;

/**
 * Lemage主界面
 * 图片选择器界面
 *
 * @author liuri
 */
public class LemageActivity extends AppCompatActivity implements Observer {

    private final String TAG = "LemageActivity";

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

    public static ArrayList<Photo> selectListPhoto = new ArrayList<Photo>();

    private List<String> listPath = new ArrayList<String>();
    private List<String> listName = new ArrayList<String>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.support.v7.appcompat.R.style.Theme_AppCompat_DayNight_NoActionBar);
        setContentView(getRootLayout());
        getRootLayout().setBackgroundColor(Color.parseColor("#fafafa"));
        initPhoto();
        EventTool.getInstance().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventTool.getInstance().unRegister(this);
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
//                        Log.e(TAG, "albumList.size === " + albumList.size());
                        if(albumList.size() > 0) {
                            for(Album album : albumList) {
//                                Log.e(TAG, "相册名称 === " + album.getName());
//                                Log.e(TAG, "相册路径 === " + album.getPath());
//                                Log.e(TAG, "相片数量 === " + album.getPhotoList().size());
                            }
                        }
                        changeAlbum((Album) albumList.toArray()[0]);
                        getHorizontal(albumList);
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
     * 显示横向栏
     * @return
     */
    private void getHorizontal(Collection<Album> albumList) {
        if(mAlbumAdapter == null) {
            ArrayList<Album> mAlbumList = new ArrayList<Album>();
            Iterator it = albumList.iterator();
            while (it.hasNext()) {
                Album album = (Album) it.next();
                mAlbumList.add(album);
            }
            // 倒序(手机不同，获取的原始顺序不同)
            if(!mAlbumList.get(0).getName().equals("全部照片")) {
                Collections.reverse(mAlbumList);
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
            phototAdapterData.setPhotoList(album.getPhotoList());
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
        public void onClickSelectListener(List<Photo> list) {
            if(selectListPhoto == null) {
                selectListPhoto = new ArrayList<Photo>();
            }else {
                selectListPhoto.clear();
            }
            // 筛选选中的
//            for(Photo photo : list) {
//                if(photo.getStatus() == 1) {
//                    selectListPhoto.add(photo);
//                }
//            }
            selectListPhoto.addAll(list);
        }

        @Override
        public void onClickPreviewListener(List<Photo> list, int position) {
            Intent intent = new Intent(LemageActivity.this, PreviewActivity.class);
            Bundle bundle = new Bundle();
            List<Photo> list_count = photoAdapter.getAlbum().getPhotoList();
            bundle.putSerializable("photos", (Serializable) list_count);
            listPath.clear();
            listName.clear();
            for(int i = 0; i < list_count.size(); i ++) {
                listPath.add(list_count.get(i).getPath());
                listName.add(list_count.get(i).getName());
            }
            bundle.putStringArrayList("paths", (ArrayList<String>) listPath);
            bundle.putStringArrayList("names", (ArrayList<String>) listName);
//            intent.putExtra("from", "item");
//            intent.putExtra("index", position);
            bundle.putString("from", "item");
            bundle.putInt("position", position);
            intent.putExtras(bundle);
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
            phototAdapterData.setPhotoList(mAlbum.getPhotoList());
            photoAdapter.notifyDataSetChanged();

        }
    };

    /**
     * 底部条点击事件回调(预览)
     */
    private OperationBar.OperationBarOnClickListener mOperationBarOnClickListener = new OperationBar.OperationBarOnClickListener() {
        @Override
        public void leftButtonClick() {
            if(selectListPhoto.size() > 0) {
                // 传值
                Intent intent = new Intent(LemageActivity.this, PreviewActivity.class);
                Bundle bundle = new Bundle();
//                bundle.putSerializable("photos", (Serializable) selectListPhoto);
                listPath.clear();
                listName.clear();
                for(int i = 0; i < selectListPhoto.size(); i ++) {
                    listPath.add(selectListPhoto.get(i).getPath());
                    listName.add(selectListPhoto.get(i).getName());
                }
                bundle.putStringArrayList("paths", (ArrayList<String>) listPath);
                bundle.putStringArrayList("names", (ArrayList<String>) listName);
                intent.putExtras(bundle);
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

    @Override
    public void updata(Object object) {
        ArrayList<Photo> list = (ArrayList<Photo>) object;
        phototAdapterData.setPhotoList(list);
        photoAdapter.notifyDataSetChanged();
//        for(int i = 0; i < list.size(); i ++) {
//            if(list.get(i).)
//        }
    }

}
