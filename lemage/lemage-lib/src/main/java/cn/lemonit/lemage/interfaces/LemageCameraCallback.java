package cn.lemonit.lemage.interfaces;

import java.util.List;

/**
 * 拍照或者录制视频后的回调接口
 * @author zhaoguangyang
 */
public interface LemageCameraCallback {
    /**
     * 给调用者返回一个存储文件路径的list
     */
    void cameraActionFinish(List<String> list);
}
