package cn.lemonit.lemage.util;

import android.content.Context;

/**
 *  适配距离，宽度，高度等
 */
public class DistanceUtil {

    /**
     * 屏幕宽度，高度
     */
    private int screenWidth;
    private int screenHeight;
    /**
     * activity顶部条高度
     */
    private int navigationBarHeight;

    private static DistanceUtil instance;

    private DistanceUtil(int screenWidth, int screenHeight){
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }

    public DistanceUtil getInstance(Context mContext) {
        if(instance == null) {
            instance = new DistanceUtil(ScreenUtil.getScreenWidth(mContext), ScreenUtil.getScreenHeight(mContext));
        }
        return instance;
    }

    /**
     * activity顶部条高度是屏幕的 1 / 9
     * @return
     */
    public int getNavigationBarHeight() {
        return screenHeight / 9;
    }
}
