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
import java.util.HashMap;
import java.util.Map;

import cn.lemonit.lemage.bean.Album;
import cn.lemonit.lemage.bean.Photo;
import cn.lemonit.lemage.interfaces.PhotoScanCompleteCallback;

public class LemageScanner {

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
//                if (albumName == null) {
//                    // 相册名为空，读取所有照片
                return new CursorLoader(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION, null, null, IMAGE_PROJECTION[2] + " DESC");
//                } else {
//                    return new CursorLoader(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION, IMAGE_PROJECTION[0] + " like '%" + args.getString("path") + "%'", null, IMAGE_PROJECTION[2] + " DESC");
//                }
            }

            @Override
            public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                Map<String, Album> albumMap = new HashMap<>();
                if (data != null) {
                    int count = data.getCount();
                    if (count > 0) {
                        data.moveToFirst();
                        do {
                            String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                            String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                            long time = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                            int size = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[4]));
                            Photo photo = new Photo(name, path, time);
//                            System.out.println("PHOTO PATH = " + photo.getPath());
                            if (size >= minSize) {
                                // 开始存储相册文件夹信息
                                File albumDir = new File(path).getParentFile();
                                // 首先判断是否已经扫描过这个相册的照片，如果扫描过就不用再次创建相册
                                Album album = albumMap.get(albumDir.getName());
                                if (album == null) {
                                    // 没有扫描过这个相册，初始创建
                                    album = new Album(
                                            albumDir.getName(),
                                            albumDir.getAbsolutePath()
                                    );
                                    albumMap.put(albumDir.getName(), album);
                                }
                                album.getPhotoList().add(photo);
                            }
                        } while (data.moveToNext());
                        if (enableAllPhoto) {
                            // 开启全部照片相册
                            Album allPhotoAlbum = new Album("全部照片", "/");
                            for (Album albumItem : albumMap.values()) {
                                for (Photo photo : albumItem.getPhotoList()) {
                                    allPhotoAlbum.getPhotoList().add(photo);
                                }
                            }
                            albumMap.put(allPhotoAlbum.getName(), allPhotoAlbum);
                        }
                        completeCallback.scanComplete(albumMap.values());
                    }
                }
            }

            @Override
            public void onLoaderReset(Loader<Cursor> loader) {

            }
        };
    }

}