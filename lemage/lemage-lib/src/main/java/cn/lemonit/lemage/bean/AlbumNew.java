package cn.lemonit.lemage.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 相册或者视频所在文件夹对象
 * @author: zhaoguangyang
 */
public class AlbumNew extends FileObj {

    private List<FileObj> fileList;

    public AlbumNew(String name, String path) {
        this.name = name;
        this.path = path;
        this.fileList = new ArrayList<>();
    }

    public AlbumNew(String name, String path, List<FileObj> fileList) {
        this.name = name;
        this.path = path;
        this.fileList = fileList;
    }

    public List<FileObj> getFileList() {
        if (fileList == null) {
            fileList = new ArrayList<>();
        }
        return fileList;
    }

    public void setFileList(List<FileObj> fileList) {
        this.fileList = fileList;
    }

}
