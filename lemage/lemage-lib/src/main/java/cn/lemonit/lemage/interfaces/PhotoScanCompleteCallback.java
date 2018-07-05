package cn.lemonit.lemage.interfaces;

import java.util.Collection;

import cn.lemonit.lemage.bean.Album;

/**
 * @author: zhaoguangyang
 */
public interface PhotoScanCompleteCallback {

    /**
     * 照片扫描完毕的回调函数
     *
     * @param albumList 相册列表
     */
    void scanComplete(Collection<Album> albumList);

}
