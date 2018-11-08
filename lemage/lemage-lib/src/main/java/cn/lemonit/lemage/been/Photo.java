package cn.lemonit.lemage.been;

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

    /**
     * 如果被选中的序号
     */
    private int number;

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

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }
}
