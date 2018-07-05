package cn.lemonit.lemage.bean;

import java.io.Serializable;

/**
 * 用户传递的是网络地址时经过处理后返回的类
 * @author zhaoguangyang
 */
public class NetBeen implements Serializable {

    /**
     * 经过处理后返回的路径
     */
    private String path;
    /**
     * 用户传递的网络地址具体是什么类型   图片0   视频1
     */
    private int type;

    public void setPath(String path) {
        this.path = path;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public int getType() {
        return type;
    }
}
