package cn.lemonit.lemage.been;

import java.io.Serializable;

/**
 * 视频类
 * @author: zhaoguangyang
 */
public class Video extends FileObj implements Serializable {

    private int id;
    private String title;
    private String album;
    private String artist;
    private String displayName;
    private String mimeType;
    private String path;
    private long duration;
    private long size;
    private long time;

    /**
     *  是否选中状态
     *  0 未选中 1 选中
     */
    private int status;

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getPath() {
        return path;
    }

    public long getDuration() {
        return duration;
    }

    public long getSize() {
        return size;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
