package cn.lemonit.lemage.interfaces;

import java.util.Collection;
import java.util.List;

import cn.lemonit.lemage.bean.AlbumNew;
import cn.lemonit.lemage.bean.FileObj;
import cn.lemonit.lemage.bean.Video;

/**
 * 扫描视频后的回调
 * @author: zhaoguangyang
 */
public interface VideoScanCompleteCallback {

    void scanComplete(Collection<AlbumNew> albumList);
}
