package cn.lemonit.lemage.interfaces;

import java.util.List;

import cn.lemonit.lemage.bean.Video;

/**
 * 扫描视频后的回调
 * @author: zhaoguangyang
 */
public interface VideoScanCompleteCallback {

    void scanComplete(List<Video> list);
}
