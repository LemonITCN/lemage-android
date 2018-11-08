package cn.lemonit.lemage.interfaces;

import java.util.List;

import cn.lemonit.lemage.been.FileObj;

/**
 * Lemage通用图片结果回调函数
 *
 * @author LemonITCN - liuri
 */
public interface LemageResultCallback {


    /**
     * 当界面即将被关闭的时候的回调函数
     *
     * @param imageUrlList 选择的图片对应的图片Url列表
     * @param isOriginal   用户是否选择了原图选项，如果该组件关闭或不支持原图按钮选项，那么此值会始终返回true
     */
    void willClose(List<String> imageUrlList, boolean isOriginal, List<FileObj> list);


    /**
     * 当界面已经全部关闭的时候的回调函数
     *
     * @param imageUrlList 选择的图片对应的图片Url列表
     * @param isOriginal   用户是否选择了原图选项，如果该组件关闭或不支持原图按钮选项，那么此值会始终返回true
     */
    void closed(List<String> imageUrlList, boolean isOriginal, List<FileObj> list);
}
