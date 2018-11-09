package cn.lemonit.lemage.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lemorage.file.LemixFileCommon;
import com.lemorage.file.Lemorage;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import cn.lemonit.lemage.activity_ui.SelectUi;
import cn.lemonit.lemage.adapter.AlbumAdapter;
import cn.lemonit.lemage.adapter.PhotoAdapter;
import cn.lemonit.lemage.base.MediaBaseActivity;
import cn.lemonit.lemage.been.Album;
import cn.lemonit.lemage.been.FileObj;
import cn.lemonit.lemage.core.LemageScanner;
import cn.lemonit.lemage.interfaces.LemageResultCallback;
import cn.lemonit.lemage.interfaces.ScanCompleteCallback;
import cn.lemonit.lemage.view.select_view.AlbumSelectButton;
import cn.lemonit.lemage.view.select_view.DrawCircleTextButton;
import cn.lemonit.lemage.view.select_view.NavigationBar;
import cn.lemonit.lemage.view.select_view.OperationBar;

/**
 * @author zhaoguangyang
 * @date 2018/11/2
 * Describe: 选择器
 */
public class SelectActivity extends MediaBaseActivity implements NavigationBar.LeftViewClickListener, NavigationBar.RightViewClickListener,
        OperationBar.OperationBarOnClickListener, AlbumAdapter.AlbumItemOnClickListener{

    /**
     * 此接口实例是应用层调用者实现的实例，跳转时传递过来的
     */
    private static LemageResultCallback callback;

    public static void setCallback(LemageResultCallback mCallback) {
        callback = mCallback;
    }

    private final String TAG = "SelectActivity";

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

    private int style;
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
    private LinearLayout topHorizontalLayout;

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
        showPictureOrVideo();
    }

    @Override
    protected void getData() {
        super.getData();
        maxChooseCount = intent.getIntExtra("maxChooseCount", 0);
        needShowOriginalButton = intent.getBooleanExtra("needShowOriginalButton", false);
        operationBarItemCount = needShowOriginalButton ? 3 : 2;
        themeColor = intent.getIntExtra("themeColor", 0);
        style = intent.getIntExtra("style", 0);
    }

    @Override
    protected void init() {
        super.init();
        imageListView = SelectUi.getImageListView(this);
        navigationBar = SelectUi.getNavigationBar(this, themeColor);
        topHorizontalLayout = SelectUi.getHorizontalLayout(this);
        albumRecyclerView = SelectUi.getHorizontalListView(this);
        operationBar = SelectUi.getOperationBar(this, themeColor, operationBarItemCount);
    }

    @Override
    protected void addView(RelativeLayout rootLayout) {
        super.addView(rootLayout);
        rootLayout.addView(imageListView);
        rootLayout.addView(navigationBar);
        topHorizontalLayout.addView(albumRecyclerView);
        rootLayout.addView(topHorizontalLayout);
        topHorizontalLayout.setVisibility(View.GONE);
        rootLayout.addView(operationBar);
    }

    /**
     * 根据需要扫描图片还是视频并显示
     */
    private void showPictureOrVideo() {
        new LemageScanner(this, style).scanFile(new ScanCompleteCallback() {
            @Override
            public void scanComplete(Collection<Album> albumList) {
                List<Album> list = SelectUi.sortList(albumList);
                changeAlbum(list.get(0));
                getHorizontal(list);
            }
        });

    }

    /**
     * 显示横向栏
     *
     * @return
     */
    private void getHorizontal(Collection<Album> albumList) {
        if (mAlbumAdapter == null) {
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
            mAlbumAdapter = new AlbumAdapter(this, mAlbumList, themeColor);
            mAlbumAdapter.setAlbumItemOnClickListener(this);
        }
        albumRecyclerView.setAdapter(mAlbumAdapter);
    }

    /**
     * 显示表格图片
     *
     * @param album
     */
    private void changeAlbum(Album album) {
        if(photoAdapter == null) {
            phototAdapterData = new Album(null, null);
            phototAdapterData.setName(album.getName());
            phototAdapterData.setPath(album.getPath());
            phototAdapterData.setFileList(album.getFileList());
            photoAdapter = SelectUi.getPhotoAdapter(this, phototAdapterData, themeColor, maxChooseCount, style);
            photoAdapter.setPhotoViewOnClickListener(mPhotoViewOnClickListener);
            imageListView.setAdapter(photoAdapter);
        }
    }

    /**
     * 顶部条右侧按钮事件(取消)
     */
    @Override
    public void rightClickListener() {
        if (operationBar != null) {
            for (FileObj fileObj : listPhotoAll) {
                fileObj.setStatus(0);
            }
            listPhotoSelect.clear();
            List<FileObj> list = phototAdapterData.getFileList();
            for (FileObj fileObj : phototAdapterData.getFileList()) {
                fileObj.setStatus(0);
            }
            photoAdapter.clearSelectFile();
            photoAdapter.notifyDataSetChanged();

            callback.willClose(null, true, null);
            SelectActivity.this.finish();
        }
    }

    /**
     * 顶部条左侧按钮事件(显示文件夹)
     * @param view
     */
    @Override
    public void leftClickListener(AlbumSelectButton view) {
        view.changeArrow();
        if (topHorizontalLayout != null) {
            if (topHorizontalLayout.isShown()) {
                topHorizontalLayout.setVisibility(View.GONE);
            } else {
                topHorizontalLayout.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 底部条左侧按钮事件（进入预览）
     */
    @Override
    public void leftButtonClick() {
        if (listPhotoSelect.size() > 0) {
            // 按number排序,保证是序号的顺序预览
            number(listPhotoSelect);
            listPhotoAll.clear();
            listPhotoAll.addAll(photoAdapter.getAlbumNew().getFileList());
            // 传值
            ArrayList<String> listUrlAll = new ArrayList<String>();   // 全部文件路径
            ArrayList<String> listUrlSelect = new ArrayList<String>();  // 已经选择文件路径
            for (FileObj fileObj : listPhotoSelect) {
                String path = fileObj.getPath();
                listUrlAll.add(path);
                listUrlSelect.add(path);
            }
            PreviewActivity.setCallback(callbackToPreview);
            Intent intent = new Intent(SelectActivity.this, PreviewActivity.class);
            intent.putExtra("themeColor", themeColor);
            intent.putStringArrayListExtra("listAll", listUrlAll);
            intent.putStringArrayListExtra("listSelect", listUrlSelect);
            intent.putExtra("maxChooseCount", maxChooseCount);
            startActivity(intent);
        } else {
            Toast.makeText(SelectActivity.this, "您还没有选中照片，不能预览", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 底部条中间按钮事件
     */
    @Override
    public void centerButtonClick() {
        DrawCircleTextButton mDrawCircleTextButton = operationBar.getCenterButton();
        if (mDrawCircleTextButton != null) {
            mDrawCircleTextButton.changeStatus();
        }
    }

    /**
     * 底部条右侧按钮事件(完成按钮)
     */
    @Override
    public void rightButtonClick() {
        ArrayList<String> list = new ArrayList<String>();
        for (FileObj fileObj : listPhotoSelect) {
            if (fileObj.getStatus() == 1) {
                list.add(Lemorage.put(new File(fileObj.getPath()), false, SelectActivity.this));
            }
        }
        callback.willClose(list, true, null);
        SelectActivity.this.finish();
    }


    /**
     * 图片文件夹横向列表item回调
     */
    @Override
    public void constantShow() {
        if (topHorizontalLayout != null && topHorizontalLayout.isShown()) {
            topHorizontalLayout.setVisibility(View.GONE);
            navigationBar.getAlbumSelectButton().changeArrow();
        }
    }

    /**
     * 图片文件夹横向列表item回调
     */
    @Override
    public void notifShow(Album mAlbum) {
        if (topHorizontalLayout != null && topHorizontalLayout.isShown()) {
            topHorizontalLayout.setVisibility(View.GONE);
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

    /**
     * 排序
     * @param plist
     */
    private void number(List<FileObj> plist) {
        /*
         * int compare(Person p1, Person p2) 返回一个基本类型的整型，
         * 返回负数表示：p1 小于p2，
         * 返回0 表示：p1和p2相等，
         * 返回正数表示：p1大于p2
         */
        Collections.sort(plist, new Comparator<FileObj>() {
            @Override
            public int compare(FileObj o1, FileObj o2) {
                //按照Person的年龄进行升序排列
                if (o1.getNumber() > o2.getNumber()) {
                    return 1;
                }
                if (o1.getNumber() == o2.getNumber()) {
                    return 0;
                }
                return -1;
            }
        });
    }

    /**
     * 预览界面回调接口, 回来的参数是已经选中的图片路径集合
     */
    private LemageResultCallback callbackToPreview = new LemageResultCallback() {
        @Override
        public void willClose(List<String> imageUrlList, boolean isOriginal, List<FileObj> list) {

            listPhotoSelect.clear();
            for (int i = 0; i < phototAdapterData.getFileList().size(); i++) {
                phototAdapterData.getFileList().get(i).setStatus(0);
                phototAdapterData.getFileList().get(i).setNumber(0);
                for (int m = 0; m < imageUrlList.size(); m++) {
                    if(phototAdapterData.getFileList().get(i).getPath().equals(imageUrlList.get(m))) {
                        phototAdapterData.getFileList().get(i).setStatus(1);
                        phototAdapterData.getFileList().get(i).setNumber(m + 1);
                    }
                }
            }
            for (FileObj fileObj : list) {
                if (fileObj.getStatus() == 1) {
                    listPhotoSelect.add(fileObj);
                }
            }
            photoAdapter.changeList(list);
            photoAdapter.notifyDataSetChanged();
//            photoAdapter.notifyItemChanged(19);
        }

        @Override
        public void closed(List<String> imageUrlList, boolean isOriginal, List<FileObj> list) {
            listPhotoSelect.clear();
            for (FileObj fileObj : list) {
                if (fileObj.getStatus() == 1) {
                    listPhotoSelect.add(fileObj);
                }
            }
            ArrayList<String> list_ = new ArrayList<String>();
            for (FileObj fileObj : listPhotoSelect) {
                if (fileObj.getStatus() == 1) {
                    list_.add(Lemorage.put(new File(fileObj.getPath()), false, SelectActivity.this));
                }
            }
            callback.willClose(list_, true, null);
            SelectActivity.this.finish();
        }
    };

    /**
     * 表格图片列表item选中事件回调
     */
    private PhotoAdapter.PhotoViewOnClickListener mPhotoViewOnClickListener = new PhotoAdapter.PhotoViewOnClickListener() {
        @Override
        public void onClickSelectListener(List<FileObj> list) {
            if (listPhotoSelect == null) {
                listPhotoSelect = new ArrayList<FileObj>();
            } else {
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
            // 添加全部
            for (FileObj fileObj : listPhotoAll) {
                String path = fileObj.getPath();
                listUrlAll.add(path);
            }
            // 添加选中（此时listPhotoSelect是按照选中的序号顺序）
            for (FileObj fileObj : listPhotoSelect) {
                String path = fileObj.getPath();
                listUrlSelect.add(path);
            }
            Intent intent = new Intent(SelectActivity.this, PreviewActivity.class);
            intent.putExtra("from", "all");
            intent.putExtra("position", position);
            intent.putExtra("themeColor", themeColor);
            intent.putStringArrayListExtra("listAll", listUrlAll);
            intent.putStringArrayListExtra("listSelect", listUrlSelect);
            intent.putExtra("maxChooseCount", maxChooseCount);
            startActivity(intent);

        }
    };
}
