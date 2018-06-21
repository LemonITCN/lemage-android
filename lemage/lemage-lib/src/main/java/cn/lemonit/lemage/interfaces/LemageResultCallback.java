package cn.lemonit.lemage.interfaces;

import java.util.List;

import cn.lemonit.lemage.bean.Photo;

/**
 * Lemage图片回调
 */
public interface LemageResultCallback {


    /**
     * 页面即将关闭
     * @param imageUrlList 图片的URL路径
     * @param isOriginal 用户是否选中了原图选项
     */
    void willClose(List<String> imageUrlList,boolean isOriginal);


    /**
     * 页面已经关闭
     * @param imageUrlList 图片的URL列表
     * @param isOriginal 用户是否选中了原图选项
     */
    void closed(List<String> imageUrlList,boolean isOriginal);
}
