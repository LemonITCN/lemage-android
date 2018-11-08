package cn.lemonit.lemage.activity_ui;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import cn.lemonit.lemage.activity.PreviewActivity;
import cn.lemonit.lemage.adapter.ImgPagerAdapter;
import cn.lemonit.lemage.been.FileObj;
import cn.lemonit.lemage.util.ScreenUtil;
import cn.lemonit.lemage.view.preview_view.PreviewOperationBar;
import cn.lemonit.lemage.view.select_view.NavigationBar;

/**
 * @author zhaoguangyang
 * @date 2018/11/5
 * Describe:
 */
public class PreviewUi {

    /**
     * 获取顶部条
     * @return
     */
    public static NavigationBar getNavigationBar(Context context, int themeColor, String from, int fromPosition, int maxChooseCount, ArrayList<FileObj> listPhotoAll, ArrayList<FileObj> listPhotoSelect) {
        NavigationBar mNavigationBar = new NavigationBar(context, 1, themeColor);
        if(!TextUtils.isEmpty(from) && from.equals("all")) {
            mNavigationBar.changeText(listPhotoAll.size(), 1);
        }else {
            mNavigationBar.changeText(listPhotoSelect.size(), 1);
        }
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ScreenUtil.dp2px(context, 56));
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        mNavigationBar.setLayoutParams(layoutParams);
        mNavigationBar.setBackgroundColor(Color.argb(200, 0, 0, 0));
        mNavigationBar.setPreviewLeftViewClickListener((NavigationBar.PreviewLeftViewClickListener) context);
        mNavigationBar.setPreviewRightViewClickListener((NavigationBar.PreviewRightViewClickListener) context);
        // 预览界面顶部条右侧按钮的初始状态（有时是选中状态，有时是未选中状态）,之后的翻页状态会自动刷新
        if(!TextUtils.isEmpty(from) && from.equals("all")) {
            int number = listPhotoSelect.indexOf(listPhotoAll.get(fromPosition)) + 1;
            mNavigationBar.changeTextCircle(listPhotoAll.get(fromPosition).getStatus(), number);
        }else {
            mNavigationBar.changeTextCircle(listPhotoSelect.get(0).getStatus(), 1);
        }

        // 允许选择的图片数量，如果传<=0的数，表示关闭选择功能（选择器右上角是否有选择按钮）
        if(maxChooseCount < 1) {
            mNavigationBar.hideSelectButton();
        }
        return mNavigationBar;
    }

    /**
     * 获取底部条
     * @return
     */
    public static PreviewOperationBar getPreviewOperationBar(Context context, int themeColor) {
        PreviewOperationBar mPreviewOperationBar = new PreviewOperationBar(context, themeColor);
        int operationHeight = ScreenUtil.getScreenHeight(context) / 15;
        RelativeLayout.LayoutParams layoutParamsOperation = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, operationHeight);
        layoutParamsOperation.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        layoutParamsOperation.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        layoutParamsOperation.bottomMargin = operationHeight;
        mPreviewOperationBar.setLayoutParams(layoutParamsOperation);
        mPreviewOperationBar.setPreviewOperationBarClickListener((PreviewOperationBar.PreviewOperationBarClickListener) context);
        return mPreviewOperationBar;
    }

    /**
     * 获取翻页器
     * @param context
     * @return
     */
    public static ViewPager getViewPager(Context context) {
        ViewPager mViewPager = new ViewPager(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mViewPager.setLayoutParams(layoutParams);
        return mViewPager;
    }

    /**
     * 获取适配器
     * @param context
     * @return
     */
    public static ImgPagerAdapter getImgPagerAdapter(Context context, String from, ArrayList<FileObj> listPhotoAll, ArrayList<FileObj> listPhotoAdapterData) {
        ImgPagerAdapter mImgPagerAdapter = null;
        if(!TextUtils.isEmpty(from) && from.equals("all")) {
            mImgPagerAdapter = new ImgPagerAdapter(context, listPhotoAll);
        }else {
            mImgPagerAdapter = new ImgPagerAdapter(context, listPhotoAdapterData);
        }
        mImgPagerAdapter.setImgOnClickListener((ImgPagerAdapter.ImgOnClickListener) context);
        return mImgPagerAdapter;
    }

    /**
     * 获取白色覆盖层控件
     * @param context
     * @return
     */
    public static View getWhiteView(Context context) {
        View whiteView = new View(context);
        RelativeLayout.LayoutParams paramsWhite = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        whiteView.setLayoutParams(paramsWhite);
        whiteView.setBackgroundColor(Color.WHITE);
        whiteView.setAlpha(0);
        return whiteView;
    }
}
