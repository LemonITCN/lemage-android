package cn.lemonit.lemage.interfaces;

import java.util.Collection;
import java.util.List;

import cn.lemonit.lemage.bean.AlbumNew;
import cn.lemonit.lemage.bean.FileObj;

/**
 * 扫描回调
 * @author: zhaoguangyang
 */
public interface ScanCompleteCallback {
    void scanComplete(Collection<AlbumNew> albumList);
}
