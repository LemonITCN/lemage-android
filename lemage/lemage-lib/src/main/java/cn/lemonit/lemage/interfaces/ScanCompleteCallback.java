package cn.lemonit.lemage.interfaces;

import java.util.Collection;

import cn.lemonit.lemage.bean.Album;

/**
 * 扫描回调
 * @author: zhaoguangyang
 */
public interface ScanCompleteCallback {
    void scanComplete(Collection<Album> albumList);
}
