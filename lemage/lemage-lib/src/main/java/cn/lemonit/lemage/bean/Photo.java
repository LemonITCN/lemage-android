package cn.lemonit.lemage.bean;

import java.io.Serializable;

/**
 * 照片信息类
 *
 * @author liuri
 */
public class Photo extends FileObj implements Serializable {

    /**
     * 照片生成时间
     */
    private long time;

    /**
     *  是否选中状态
     *  0 未选中 1 选中
     */
    private int status;

    public Photo() {
    }

    public Photo(String path) {
        this.path = path;
    }

    public Photo(String name, String path, long time) {
        this.name = name;
        this.path = path;
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
