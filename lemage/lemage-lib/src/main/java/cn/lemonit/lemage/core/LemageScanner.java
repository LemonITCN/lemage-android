package cn.lemonit.lemage.core;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lemonit.lemage.bean.Album;
import cn.lemonit.lemage.bean.AlbumNew;
import cn.lemonit.lemage.bean.FileObj;
import cn.lemonit.lemage.bean.Photo;
import cn.lemonit.lemage.bean.Video;
import cn.lemonit.lemage.interfaces.PhotoScanCompleteCallback;
import cn.lemonit.lemage.interfaces.ScanCompleteCallback;
import cn.lemonit.lemage.interfaces.VideoScanCompleteCallback;

public class LemageScanner {

    /**
     * 用户选择的样式
     * 0  只选择图片 （列表中只显示图片）
     * 1  只选择视频  （列表中只显示视频）
     * 2  都选择  （列表中同时显示图片和视频，且都可选择）
     * 3  二选一 （列表中显示图片和视频，用户第一个选择的是图片，那么视频变白，不可选择，反之亦然）
     */
    private int style;
    // 只选择图片
    public static final int STYLE_ONLY_PHOTO = 0;
    // 只选视频
    public static final int STYLE_ONLY_VIDEO = 1;
    // 都可选
    public static final int STYLE_ALL = 2;
    // 二选一
    public static final int STYLE_ANYONE = 3;

    /**
     * 扫描本地视频
     */
    public static void scanAllVideo(Integer minSize, boolean enableAllPhoto, FragmentActivity fragmentActivity, VideoScanCompleteCallback completeCallback) {
        fragmentActivity.getSupportLoaderManager().restartLoader(0, null, createVideoReaderLoaderCallback(minSize, enableAllPhoto, fragmentActivity.getApplicationContext(), completeCallback));
    }

    private static LoaderManager.LoaderCallbacks createVideoReaderLoaderCallback(final Integer minSize, final boolean enableAllPhoto,final Context context, final VideoScanCompleteCallback completeCallback) {
        return new LoaderManager.LoaderCallbacks<Cursor>() {

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

            @Override
            public Loader onCreateLoader(int id, Bundle args) {
                return new CursorLoader(context, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Video.Media.DATE_ADDED);
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
                Map<String, AlbumNew> albumMap = new HashMap<>();
                if (cursor != null) {
                    int count = cursor.getCount();
                    if (count > 0) {
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
                        } while (cursor.moveToNext());
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
                        completeCallback.scanComplete(albumMap.values());
                    }
                }


//                List<Video> listVideo = new ArrayList<Video>();
//                if (cursor != null) {
//                    while (cursor.moveToNext()) {
//                        int id = cursor.getInt(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[0]));
//                        String title = cursor.getString(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[1]));
//                        String album = cursor.getString(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[2]));
//                        String artist = cursor.getString(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[3]));
//                        String displayName = cursor.getString(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[4]));
//                        String mimeType = cursor.getString(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[5]));
//                        String path = cursor.getString(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[6]));
//                        long duration = cursor.getInt(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[7]));
//                        long size = cursor.getLong(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[8]));
//                        long time = cursor.getLong(cursor.getColumnIndexOrThrow(VIDEO_PROJECTION[9]));
//
//                        Video mVideo = new Video();
//                        mVideo.setId(id);
//                        mVideo.setTitle(title);
//                        mVideo.setAlbum(album);
//                        mVideo.setArtist(artist);
//                        mVideo.setDisplayName(displayName);
//                        mVideo.setMimeType(mimeType);
//                        mVideo.setPath(path);
//                        mVideo.setDuration(duration);
//                        mVideo.setSize(size);
//                        mVideo.setTime(time);
//
//                        listVideo.add(mVideo);
//                    }
//                }
//                cursor.close();
//                completeCallback.scanComplete(listVideo);
            }

            @Override
            public void onLoaderReset(Loader loader) {

            }
        };
    }

    /**
     * 扫描本地的全部照片
     *
     * @param minSize          设置扫描范围内的最小图片尺寸，单位：byte
     * @param enableAllPhoto   是否开启全部图片相册
     * @param fragmentActivity 依托的fragmentActivity
     * @param completeCallback 扫描完毕的回调
     */
    public static void scanAllPhoto(Integer minSize, boolean enableAllPhoto, FragmentActivity fragmentActivity, PhotoScanCompleteCallback completeCallback) {
        fragmentActivity.getSupportLoaderManager().restartLoader(0, null, createPhotoReaderLoaderCallbacks(minSize, enableAllPhoto, fragmentActivity.getApplicationContext(), completeCallback));
    }

    private static LoaderManager.LoaderCallbacks createPhotoReaderLoaderCallbacks(final Integer minSize, final boolean enableAllPhoto, final Context context, final PhotoScanCompleteCallback completeCallback) {
        return new LoaderManager.LoaderCallbacks<Cursor>() {
            private final String[] IMAGE_PROJECTION = {
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media.DISPLAY_NAME,
                    MediaStore.Images.Media.DATE_ADDED,
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.SIZE
            };

            @Override
            public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//                return new CursorLoader(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION, null, null, IMAGE_PROJECTION[2] + " DESC");
                CursorLoader mCursorLoader = new CursorLoader(context);
                mCursorLoader.setUri(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                mCursorLoader.setProjection(IMAGE_PROJECTION);
                mCursorLoader.setSortOrder(IMAGE_PROJECTION[2] + " DESC");
                mCursorLoader.reset();
                return mCursorLoader;
            }


            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

                Map<String, AlbumNew> albumMap = new HashMap<>();
                if (cursor != null) {
                    int count = cursor.getCount();
                    if (count > 0) {
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
                        } while (cursor.moveToNext());
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
                        completeCallback.scanComplete(albumMap.values());
                    }
                }


//                Map<String, Album> albumMap = new HashMap<>();
//                if (data != null) {
//                    int count = data.getCount();
//                    if (count > 0) {
//                        data.moveToFirst();
//                        do {
//                            String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
//                            String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
//                            long time = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
//                            int size = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[4]));
//                            Photo photo = new Photo(name, path, time);
////                            System.out.println("PHOTO PATH = " + photo.getPath());
//                            if (size >= minSize) {
//                                // 开始存储相册文件夹信息
//                                File albumDir = new File(path).getParentFile();
//                                // 首先判断是否已经扫描过这个相册的照片，如果扫描过就不用再次创建相册
//                                Album album = albumMap.get(albumDir.getName());
//                                if (album == null) {
//                                    // 没有扫描过这个相册，初始创建
//                                    album = new Album(
//                                            albumDir.getName(),
//                                            albumDir.getAbsolutePath()
//                                    );
//                                    albumMap.put(albumDir.getName(), album);
//                                }
//                                album.getPhotoList().add(photo);
//                            }
//                        } while (data.moveToNext());
//                        if (enableAllPhoto) {
//                            // 开启全部照片相册
//                            Album allPhotoAlbum = new Album("全部照片", "/");
//                            for (Album albumItem : albumMap.values()) {
//                                for (Photo photo : albumItem.getPhotoList()) {
//                                    allPhotoAlbum.getPhotoList().add(photo);
//                                }
//                            }
//                            albumMap.put(allPhotoAlbum.getName(), allPhotoAlbum);
//                        }
//                        completeCallback.scanComplete(albumMap.values());
//                    }
//                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        };
    }

}
