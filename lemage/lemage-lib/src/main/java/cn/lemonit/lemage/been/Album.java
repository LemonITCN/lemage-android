package cn.lemonit.lemage.been;

import java.util.ArrayList;
import java.util.List;

/**
 * 相册或者视频所在文件夹对象
 * @author: liuri
 */
public class Album extends FileObj {

    private List<FileObj> fileList;

    public Album(String name, String path) {
        this.name = name;
        this.path = path;
        this.fileList = new ArrayList<>();
    }

    public Album(String name, String path, List<FileObj> fileList) {
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
