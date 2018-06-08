package cn.lemonit.lemage.bean;

/**
 * 照片信息类
 *
 * @author liuri
 */
public class Photo extends FileObj {

    /**
     * 照片生成时间
     */
    private long time;

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

}
