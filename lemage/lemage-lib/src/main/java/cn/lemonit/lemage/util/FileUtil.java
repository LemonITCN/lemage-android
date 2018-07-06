package cn.lemonit.lemage.util;

import android.content.Context;

import java.io.File;
import java.util.List;

import cn.lemonit.lemage.lemageutil.SystemInfo;

/**
 * 文件类工具
 * @author zhaoguangyang
 */
public class FileUtil {

    private Context mContext;

    private FileUtil(Context mContext) {
        this.mContext = mContext;
        packageName = SystemInfo.getApplicationPackageName(mContext);
    }

    private static FileUtil instance;

    // 根目录
    private static final String baseUrl = "/storage/emulated/0/Android/data/";
    // 网络图片临时文件夹后缀
    private static final String netPhotoSuffixName = "/tmp/photo";
    // 网络视频临时文件夹后缀
    private static final String netVideoSuffixName = "/tmp/video";

    // 本地图片格式化前缀
    private static final String localImgagePrefixName = "lemage://album/localImage";
    // 本地视频格式化前缀
    private static final String localVideoPrefixName = "lemage://album/localVideo";

    // 应用程序包名
    private static String packageName;

    public static FileUtil getInstance(Context context) {
        if(instance == null) {
            instance = new FileUtil(context);
        }
        return instance;
    }

    /**
     * 获取网络图片临时文件夹路径
     */
    public String getNetPhotoFilePath() {
        return baseUrl + packageName + netPhotoSuffixName;
    }

    /**
     * 获取网络视频临时文件夹路径
     * @return
     */
    public String getNetVideoFilePath() {
        return baseUrl + packageName + netVideoSuffixName;
    }

    /**
     * 获取网络图片临时文件夹
     * @return
     */
    public File getNetPhotoFile() {
        File filePhoto = new File(getNetPhotoFilePath());
        if(!filePhoto.exists()) {
            filePhoto.mkdirs();
        }
        return filePhoto;
    }

    /**
     * 获取网络视频临时文件夹
     * @return
     */
    public File getNetVideoFile() {
        File fileVideo = new File(getNetVideoFilePath());
        if(!fileVideo.exists()) {
            fileVideo.mkdirs();
        }
        return fileVideo;
    }

    /**
     * 清空网络临时文件夹（在预览界面关闭的时候）
     */
    public void clearNetFile() {
        deleteFile(getNetPhotoFile());
        deleteFile(getNetVideoFile());
    }

    /**
     * 根据本地图片的路径转换成lemage://格式的
     * @param localPath
     * @return
     */
    public String getLocalImgagePath(String localPath) {
        return localImgagePrefixName + localPath;
    }

    /**
     * 把本地视频的路径转换成lemage://格式
     * @param localPath
     * @return
     */
    public String getLocalVideoPath(String localPath) {
        return localVideoPrefixName + localPath;
    }


    private void deleteFile(File file) {
        if(file == null) return;
        if(!file.isDirectory()) return;
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            f.delete();
        }
    }
}
