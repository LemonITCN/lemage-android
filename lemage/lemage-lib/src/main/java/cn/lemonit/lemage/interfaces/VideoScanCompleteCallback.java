package cn.lemonit.lemage.interfaces;

import java.util.Collection;

import cn.lemonit.lemage.been.Album;

/**
 * 扫描视频后的回调
 * @author: zhaoguangyang
 */
public interface VideoScanCompleteCallback {

    void scanComplete(Collection<Album> albumList);
}
