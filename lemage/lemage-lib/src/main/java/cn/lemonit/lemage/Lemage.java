package cn.lemonit.lemage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import com.lemorage.file.Lemorage;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.lemonit.lemage.activity.CameraActivity;
import cn.lemonit.lemage.activity.SelectActivity;
import cn.lemonit.lemage.activity.PreviewActivity;
import cn.lemonit.lemage.been.ImageSize;
import cn.lemonit.lemage.been.LemageUsageText;
import cn.lemonit.lemage.interfaces.LemageCameraCallback;
import cn.lemonit.lemage.interfaces.LemageResultCallback;
import cn.lemonit.lemage.util.BitmapUtil;

/**
 * Lemage入口类
 *
 * @author LemonIT.CN - liuri
 */
public class Lemage implements Serializable {

    private static final String TAG = "Lemage";

    /**
     * 本地化字符存储信息对象
     */
    private static LemageUsageText usageText;

    public static LemageUsageText getUsageText() {
        if (usageText == null) {
            usageText = LemageUsageText.enText();
        }
        return usageText;
    }

    public static void setUsageText(LemageUsageText usageText) {
        Lemage.usageText = usageText;
    }

    /**
     * 启动Lemage
     */
    public static void startUp() {
        BitmapDrawable drawable;
    }

    /**
     * 根据bitmap保存并返回沙盒路径
     * @param context
     * @param bitmap
     * @param isLongTerm
     * @return
     */
    public static String generateLemageUrl(Context context, Bitmap bitmap, boolean isLongTerm) {
//        return SendBoxFileManager.getInstance(context).generateLemageUrl(bitmap, longTerm);
        return BitmapUtil.bitmap2Url(context, bitmap, isLongTerm);
    }


    /**
     * 根据LemageURL加载对应的图片的输入流，如果用户传入的LemageURL有误或已过期，会返回null
     * 注意：此方法并不会处理图片的缩放参数，即LemageURL中的width参数和height参数会被忽略，若需要请调用Lemage.loadImage方法
     * 原理：根据LemageURL解析出沙盒对应的文件路径，然后从沙盒读取文件数据转换成FileInputStream对象后返回
     *
     * @param url LemageURL字符串
     * @return 根据LemageURL逆向转换回来的图片FileInputStream对象，如果URL无效会返回null
     */
    public static OutputStream loadImageInputStream(Context context, String url) {
//        return SendBoxFileManager.getInstance(context).loadImageInputStream(url);
        return Lemorage.getWithOutputStream(url, context);
    }

    /**
     * 根据LemageURL加载对应的图片的输入流，如果用户传入的LemageURL有误或已过期，会返回null
     * 注意：此方法并不会处理图片的缩放参数，即LemageURL中的width参数和height参数会被忽略，若需要请调用Lemage.loadImage方法
     * 原理：根据LemageURL解析出沙盒对应的文件路径，然后从沙盒读取文件数据转换成byte[]后返回
     *
     * @param url LemageURL字符串
     * @return 根据LemageURL逆向转换回来的图片byte[]，如果URL无效会返回null
     */
    public static byte[] loadImageData(Context context, String url) {
//        return SendBoxFileManager.getInstance(context).loadImageData(url);
        return Lemorage.getWithByteArr(url, context);
    }

    /**
     * 根据LemageURL加载对应的图片的Bitmap对象，如果用户传入的LemageURL有误或已过期，会返回nil
     * 该函数会解析size参数中的width、height参数，如果没有传这个参数，或参数无效，那么会返回原图
     * 原理：根据LemageURL解析出沙盒对应的文件路径，然后从沙盒读取文件数据转换成FileInputStream后转换成Bitmap对象返回
     *
     * @param url  LemageURL字符串
     * @param size 图片指定大小
     * @return 根据LemageURL逆向转换回来的图片Bitmap对象，如果URL无效会返回null
     */
    public static Bitmap loadImage(Context context, String url, ImageSize size) {
//        return SendBoxFileManager.getInstance(context).loadImage(url, size);
        return BitmapUtil.url2Bitmap(url, size, context);
    }

    /**
     * 根据LemageURL加载对应的图片的Bitmap对象，如果用户传入的LemageURL有误或已过期，会返回nil
     * 该函数会解析LemageURL中的width、height参数，如果LemageURL中不存在这两个参数，那么会返回原图
     * 原理：从LemageURL中解析出width/height参数，并调用loadImage(String url, ImageSize size)方法
     *
     * @param url LemageURL字符串
     * @return 根据LemageURL逆向转换回来的图片Bitmap对象，如果URL无效会返回null
     */
    public static Bitmap loadImage(Context context, String url) {
//        return SendBoxFileManager.getInstance(context).loadImage(url);
        return loadImage(context, url, null);
    }

    /**
     * 让所有长期的LemageURL失效
     * 原理：删除所有本地长期LemageURL对应的沙盒图片文件
     */
    public static void expiredAllLongTermUrl(Context context) {
//        SendBoxFileManager.getInstance(context).expiredAllLongTermUrl();
    }

    /**
     * 让所有短期的LemageURL失效
     * 原理：删除所有本地短期LemageURL对应的沙盒图片文件
     */
    public static void expiredAllShortTermUrl(Context context) {
//        SendBoxFileManager.getInstance(context).expiredAllShortTermUrl();
    }

    /**
     * 强制让指定的LemageURL过期，不区分当前URL是长期还是短期
     * 原理：删除这个LemageURL对应的沙盒图片文件
     *
     * @param url 要使其过期的LemageURL
     */
    public static void expiredUrl(Context context, String url) {
//        SendBoxFileManager.getInstance(context).expiredUrl(url);
    }

    /**
     * 启动图片选择器
     *
     * @param maxChooseCount         允许最多选择的图片张数，支持范围：1-99
     * @param needShowOriginalButton 是否提供【原图】选项按钮，如果不提供，那么选择结果中的【用户是否选择了原图选项】会始终返回true
     * @param themeColor             主题颜色，这个颜色会作为完成按钮、选择顺序标识、相册选择标识的背景色
     * @param style                   用户可选择样式
     * @param callback               结果回调函数，若用户在选择器中点击了取消按钮，那么回调函数中的imageUrlList为null
     */
    public static void startChooser(Context mContext,
                                    Integer maxChooseCount,
                                    boolean needShowOriginalButton,
                                    int themeColor,
                                    int style,
                                    LemageResultCallback callback) {
        Intent intent = new Intent(mContext, SelectActivity.class);
        intent.putExtra("maxChooseCount", maxChooseCount);
        intent.putExtra("needShowOriginalButton", needShowOriginalButton);
        intent.putExtra("themeColor", themeColor);
        intent.putExtra("style", style);
        SelectActivity.setCallback(callback);
        mContext.startActivity(intent);
    }

    /**
     * 启动图片预览器
     *
     * @param imageUrlArr       要预览的图片URL数组，支持lemageURL和http(s)URL如果对象为null或数组为空，那么拒绝显示图片预览器
     * @param chooseImageUrlArr 已经选择的图片Url数组
     * @param allowChooseCount  允许选择的图片数量，如果传<=0的数，表示关闭选择功能（选择器右上角是否有选择按钮），如果允许选择数量大于chooseImageUrlArr数组元素数量，那么会截取choosedImageUrlArr中的数组前allowChooseCount个元素作为已选择图片
     * @param showIndex         进入图片预览器后默认首先展示的图片索引
     * @param themeColor        主题颜色，这个颜色会作为完成按钮、选择顺序标识的背景色
     * @param callback          结果回调函数，若用户在选择器中点击了取消按钮，那么回调函数中的imageUrlList为null
     */
    public static void startPreviewer(Context mContext,
                                      List<String> imageUrlArr,
                                      List<String> chooseImageUrlArr,
                                      int allowChooseCount,
                                      int showIndex,
                                      int themeColor,
                                      LemageResultCallback callback) {

        Intent intent = new Intent(mContext, PreviewActivity.class);
        intent.putStringArrayListExtra("listAll", (ArrayList<String>) imageUrlArr);
        intent.putStringArrayListExtra("listSelect", (ArrayList<String>) chooseImageUrlArr);
        intent.putExtra("maxChooseCount", allowChooseCount);
        intent.putExtra("showIndex", showIndex);
        intent.putExtra("themeColor", themeColor);
        PreviewActivity.setCallback(callback);
        mContext.startActivity(intent);
    }


    /**
     * 拍照视频启动器
     * @param mContext
     * @param videoTime 如果录像，传递的最多录制秒数，如果 =< 0, 录像默认是5秒
     * @param cameraCallback
     */
    public static void startCamera(Context mContext, int videoTime,  LemageCameraCallback cameraCallback) {
        Intent intent = new Intent(mContext, CameraActivity.class);
        intent.putExtra("videoTime", videoTime);
        CameraActivity.setCameraCallback(cameraCallback);
        mContext.startActivity(intent);
    }

}
