package cn.lemonit.lemage.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 相册信息对象
 *
 * @author liuri
 */
public class Album extends FileObj {

    /**
     * 相册中的所有照片信息
     */
    private List<Photo> photoList;

    public Album(String name, String path) {
        this.name = name;
        this.path = path;
        this.photoList = new ArrayList<>();
    }

    public Album(String name, String path, List<Photo> photoList) {
        this.name = name;
        this.path = path;
        this.photoList = photoList;
    }

    public List<Photo> getPhotoList() {
        if (photoList == null) {
            photoList = new ArrayList<>();
        }
        return photoList;
    }

    public void setPhotoList(List<Photo> photoList) {
        this.photoList = photoList;
    }
}
