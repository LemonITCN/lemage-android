package cn.lemonit.lemage.view.select_view;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import cn.lemonit.lemage.util.ScreenUtil;

/**
 * 照片列表中的照片展示控件
 *
 * @author liuri
 */
public class PhotoView extends RelativeLayout {

    private final String TAG = "PhotoView";

    private Context mContext;
    private ImageView imageView;
    private CircleView mCircleView;
    private View whiteView;  // 白色透明覆盖层
    // 圆圈半径
    private int mRadius;
    /**
     * 选中状态
     * 0 未选中，1 选中
     */
    private int status;
    /**
     * 主题颜色
     */
    private int mColor;

    public PhotoView(Context context, int width, int color) {
        super(context);
        mContext = context;
        mColor = color;
        init(width);
    }

    private void init(int width) {
        imageView = new ImageView(getContext());
        LayoutParams params = new LayoutParams(width, width);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
//        imageView.setBackgroundColor(Color.RED);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        addView(imageView);

        mRadius = ScreenUtil.dp2px(mContext, 10);
        LayoutParams paramsCircle = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsCircle.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        paramsCircle.topMargin = mRadius / 2;
        paramsCircle.rightMargin = mRadius / 2;
        mCircleView = new CircleView(mContext, mRadius, mColor);
        mCircleView.setLayoutParams(paramsCircle);
        addView(mCircleView);

        // 添加覆盖层，不可点击时需要显示白色半透明
        whiteView = new View(mContext);
        LayoutParams paramsWhite = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        whiteView.setLayoutParams(paramsWhite);
        whiteView.setBackgroundColor(Color.WHITE);
        whiteView.setAlpha(0);
        addView(whiteView);
    }

    public ImageView getImageView() {
        return imageView;
    }

    /**
     * 改变显示的状态
     * @param mStatus
     */
    public void changeStatus(int mStatus, int number) {
        setStatus(mStatus);
        if(mCircleView != null) {
            mCircleView.changeStatus(mStatus, number);
        }
    }

    /**
     * 设置白色覆盖层透明度
     * @param alpha
     */
    public void setWhiteAlpha(float alpha) {
        whiteView.setAlpha(alpha);
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
