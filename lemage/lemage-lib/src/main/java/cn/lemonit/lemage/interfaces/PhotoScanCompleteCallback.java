package cn.lemonit.lemage.interfaces;

import java.util.Collection;
import java.util.List;

import cn.lemonit.lemage.bean.Album;
import cn.lemonit.lemage.bean.AlbumNew;

public interface PhotoScanCompleteCallback {

    /**
     * 照片扫描完毕的回调函数
     *
     * @param albumList 相册列表
     */
    void scanComplete(Collection<AlbumNew> albumList);

}
