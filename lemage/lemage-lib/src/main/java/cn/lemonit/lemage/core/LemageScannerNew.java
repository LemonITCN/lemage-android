package cn.lemonit.lemage.core;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lemonit.lemage.bean.AlbumNew;
import cn.lemonit.lemage.bean.FileObj;
import cn.lemonit.lemage.bean.Photo;
import cn.lemonit.lemage.bean.Video;
import cn.lemonit.lemage.interfaces.ScanCompleteCallback;

/**
 * 扫描图片或者视频类
 * @author: zhaoguangyang
 */
public class LemageScannerNew {

    /**
     * 用户选择的样式
     * 0  只选择图片 （列表中只显示图片）
     * 1  只选择视频  （列表中只显示视频）
     * 2  都选择  （列表中同时显示图片和视频，且都可选择）
     * 3  二选一 （列表中显示图片和视频，用户第一个选择的是图片，那么视频变白，不可选择，反之亦然）
     */
    private static int mStyle;
    // 只选择图片
    public static final int STYLE_ONLY_PHOTO = 0;
    // 只选视频
    public static final int STYLE_ONLY_VIDEO = 1;
    // 都可选
    public static final int STYLE_ALL = 2;
    // 二选一
    public static final int STYLE_ANYONE = 3;
    /**
     * 具体执行的是扫描图片还是扫描视频 （扫描图片0， 扫描视频1）
     */
    private static int scanTarget;
    /**
     * 如果是扫描2次，取的数据
     */

    /**
     * 加载器
     */
    private CursorLoader mCursorLoader;
    /**
     * 扫描图片的属性信息
     */
    private final String[] IMAGE_PROJECTION = {
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED,
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.SIZE
    };

    /**
     * 扫描视频的属性信息
     */
    private final String[] VIDEO_PROJECTION = {
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.ALBUM,
            MediaStore.Video.Media.ARTIST,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.MIME_TYPE,
            MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_ADDED
    };

    /**
     * 扫描图片条件
     */
    private int minPhotoSize;   // 设置扫描范围内的最小图片尺寸，单位：byte
    private boolean enableAllPhoto = true;  // 是否开启全部图片相册
    /**
     * 扫描视频条件
     */
    private long minVideoSize; // 设置扫描范围内的最小视频大小
    private boolean enableAllVideo; // 是否开启全部视频
    private long minVideoTime;  // 设置扫描范围内播放时长最短视频

    /**
     * 扫描结果的数据源
     */
    private Map<String, AlbumNew> albumMap = new HashMap<>();
    /**
     * 扫描次数，如果是第二次扫描，把视频和图片按时间顺序混排
     */
    private boolean scanNumber;

    private FragmentActivity mContext;

    private static LemageScannerNew instance;

    public LemageScannerNew(FragmentActivity context, int style) {
        mContext = context;
        mStyle = style;
        if(mStyle < 0 || mStyle > 3) mStyle = STYLE_ONLY_PHOTO;  // 传值超出范围默认只扫描图片
        // 如果扫描的是图片或者视频，那么只进行一次扫描
        if(mStyle == STYLE_ONLY_PHOTO || mStyle == STYLE_ONLY_VIDEO) {
            scanTarget = mStyle;
        }
        // 如果图片和视频都扫描，那么先扫描图片，再扫描视频
        else if(mStyle == STYLE_ALL || mStyle == STYLE_ANYONE) {
            scanTarget = STYLE_ONLY_PHOTO;
        }
    }

//    public static synchronized LemageScannerNew getInstance(FragmentActivity context, int style) {
//        if(instance == null) {
//            instance = new LemageScannerNew(context, style);
//        }
//        if(mStyle < 0 || mStyle > 3) mStyle = STYLE_ONLY_PHOTO;  // 传值超出范围默认只扫描图片
//        // 如果扫描的是图片或者视频，那么只进行一次扫描
//        if(mStyle == STYLE_ONLY_PHOTO || mStyle == STYLE_ONLY_VIDEO) {
//            scanTarget = mStyle;
//        }
//        // 如果图片和视频都扫描，那么先扫描图片，再扫描视频
//        else if(mStyle == STYLE_ALL || mStyle == STYLE_ANYONE) {
//            scanTarget = STYLE_ONLY_PHOTO;
//        }
//        return instance;
//    }

    /**
     * 扫描文件（图片或者视频）
     * @param mScanCompleteCallback  扫描后的回调接口，把扫描的图片或者视频返回给调用者
     */
    public void scanFile(ScanCompleteCallback mScanCompleteCallback) {
        mContext.getSupportLoaderManager().restartLoader(0,  null, getLoaderCallback(mScanCompleteCallback));
    }

    private LoaderManager.LoaderCallbacks getLoaderCallback(final ScanCompleteCallback mScanCompleteCallback) {
        LoaderManager.LoaderCallbacks mLoaderCallbacks = new LoaderManager.LoaderCallbacks<Cursor>() {
            @Override
            public Loader onCreateLoader(int id, Bundle args) {
                return getCursorLoader();
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                loadFinished(cursor, mScanCompleteCallback);
            }

            @Override
            public void onLoaderReset(Loader loader) {

            }
        };
        return mLoaderCallbacks;
    }

    /**
     * 根据需求设置扫描图片还是扫描视频
     * @return
     */
    private CursorLoader getCursorLoader() {
//        if(mCursorLoader == null) {
//            mCursorLoader = new CursorLoader(mContext);
//        }
//        mCursorLoader.reset();
//        // 设置Uri
//        Uri mUri = null;
//        // 设置排序
//        String sortOrder = null;
//        // 扫描图片
//        if(scanTarget == STYLE_ONLY_PHOTO) {
//            mUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//            sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";
//            mCursorLoader.setProjection(IMAGE_PROJECTION);
//        }
//        // 扫描视频
//        else if(scanTarget == STYLE_ONLY_VIDEO) {
//            mUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//            sortOrder = MediaStore.Video.Media.DATE_ADDED  + " DESC";
//            mCursorLoader.setProjection(VIDEO_PROJECTION);
//        }
//        mCursorLoader.setUri(mUri);
//        mCursorLoader.setSortOrder(sortOrder);
//        return mCursorLoader;
        if(scanTarget == STYLE_ONLY_PHOTO) {
            CursorLoader photoCursorLoader = new CursorLoader(mContext);
            photoCursorLoader.setUri(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            photoCursorLoader.setSortOrder(MediaStore.Images.Media.DATE_ADDED + " DESC");
            photoCursorLoader.setProjection(IMAGE_PROJECTION);
            return photoCursorLoader;
        }else {
            CursorLoader videoCursorLoader = new CursorLoader(mContext);
            videoCursorLoader.setUri(MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            videoCursorLoader.setSortOrder(MediaStore.Video.Media.DATE_ADDED  + " DESC");
            videoCursorLoader.setProjection(VIDEO_PROJECTION);
            return videoCursorLoader;
        }
    }

    /**
     * 扫描结果要执行的方法
     * @param cursor
     * @param mScanCompleteCallback
     */
    private void loadFinished(Cursor cursor, ScanCompleteCallback mScanCompleteCallback) {
        if (cursor != null) {
            int count = cursor.getCount();
            if (count > 0) {
                getFileObj(cursor, mScanCompleteCallback);
            }
        }
    }

    /**
     * 把扫描出来的数据加进数据集
     * @param cursor
     * @param mScanCompleteCallback
     */
    private void getFileObj(Cursor cursor, ScanCompleteCallback mScanCompleteCallback) {
        // 扫描图片并得到数据源
        if(scanTarget == STYLE_ONLY_PHOTO) {
            cursor.moveToFirst();
            do {
                String path = cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                long time = cursor.getLong(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                int size = cursor.getInt(cursor.getColumnIndexOrThrow(IMAGE_PROJECTION[4]));
                Photo photo = new Photo(name, path, time);
                if (size >= 0) {
                    // 开始存储视频文件夹信息
                    File albumDir = new File(path).getParentFile();
                    // 首先判断是否已经扫描过这个相册的照片，如果扫描过就不用再次创建相册
                    AlbumNew albumNew = albumMap.get(albumDir.getName());
                    if (albumNew == null) {
                        // 没有扫描过这个相册，初始创建
                        albumNew = new AlbumNew(
                                albumDir.getName(),
                                albumDir.getAbsolutePath()
                        );
                        albumMap.put(albumDir.getName(), albumNew);
                    }
                    albumNew.getFileList().add(photo);
                }
            }while (cursor.moveToNext());
            if (enableAllPhoto) {
                // 开启全部照片相册
                AlbumNew allPhotoAlbum = new AlbumNew("全部照片", "/");
                for (AlbumNew albumItem : albumMap.values()) {
                    for (FileObj photo : albumItem.getFileList()) {
                        allPhotoAlbum.getFileList().add(photo);
                    }
                }
                albumMap.put(allPhotoAlbum.getName(), allPhotoAlbum);
            }
        }
        // 扫描视频并得到数据源
        else if(scanTarget == STYLE_ONLY_VIDEO) {
            cursor.moveToFirst();
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[0]));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[1]));
                String album = cursor.getString(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[2]));
                String artist = cursor.getString(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[3]));
                String displayName = cursor.getString(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[4]));
                String mimeType = cursor.getString(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[5]));
                String path = cursor.getString(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[6]));
                long duration = cursor.getInt(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[7]));
                long size = cursor.getLong(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[8]));
                long time = cursor.getLong(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[9]));

                Video mVideo = new Video();
                mVideo.setId(id);
                mVideo.setTitle(title);
                mVideo.setAlbum(album);
                mVideo.setArtist(artist);
                mVideo.setDisplayName(displayName);
                mVideo.setMimeType(mimeType);
                mVideo.setPath(path);
                mVideo.setDuration(duration);
                mVideo.setSize(size);
                mVideo.setTime(time);
                if (size >= 0) {
                    // 开始存储视频文件夹信息
                    File albumDir = new File(path).getParentFile();
                    // 首先判断是否已经扫描过这个相册的照片，如果扫描过就不用再次创建相册
                    AlbumNew albumNew = albumMap.get(albumDir.getName());
                    if (albumNew == null) {
                        // 没有扫描过这个相册，初始创建
                        albumNew = new AlbumNew(
                                albumDir.getName(),
                                albumDir.getAbsolutePath()
                        );
                        albumMap.put(albumDir.getName(), albumNew);
                    }
                    albumNew.getFileList().add(mVideo);
                }
            }while (cursor.moveToNext());
            if (enableAllPhoto) {
                // 开启全部照片相册
                AlbumNew allPhotoAlbum = new AlbumNew("全部照片", "/");
                for (AlbumNew albumItem : albumMap.values()) {
                    // 此时，不能添加全部照片里面的file, 否则会重复
                    if(!albumItem.getName().equals("全部照片")) {
                        for (FileObj fileObj : albumItem.getFileList()) {
                            allPhotoAlbum.getFileList().add(fileObj);
                        }
                    }
                }
                albumMap.put(allPhotoAlbum.getName(), allPhotoAlbum);
            }
        }
        // 如果只扫描一次，就直接返回结果
        if(mStyle == STYLE_ONLY_PHOTO || mStyle == STYLE_ONLY_VIDEO) {
            if(scanNumber) {
                // 把图片和视频混合按时间排序
                timeSortFile(albumMap.values());
            }
            mScanCompleteCallback.scanComplete(albumMap.values());
        }
        // 如果扫描2次就再扫描一次返回结果(第一次扫描的是图片，所以第二次扫描的是视频)
        else if(mStyle == STYLE_ALL || mStyle == STYLE_ANYONE) {
            mStyle = STYLE_ONLY_VIDEO;
            scanTarget = STYLE_ONLY_VIDEO;
            scanNumber = true;
            scanFile(mScanCompleteCallback);
        }
    }

    /**
     * 把图片和视频混合按时间排序
     * @param albumList
     */
    private void timeSortFile(Collection<AlbumNew> albumList) {
        if(albumList.size() > 0) {
            for(AlbumNew album : albumList) {
                Collections.sort(album.getFileList());
            }
        }
    }

}
