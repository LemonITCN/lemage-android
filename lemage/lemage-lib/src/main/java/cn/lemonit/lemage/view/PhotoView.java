package cn.lemonit.lemage.view;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
    // 圆圈半径
    private int mRadius;
    /**
     * 选中状态
     * 0 未选中，1 选中
     */
    private int status;

    public PhotoView(Context context, int width) {
        super(context);
        mContext = context;
        init(width);
    }

    private void init(int width) {
        imageView = new ImageView(getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
//        imageView.setBackgroundColor(Color.RED);
        imageView.setLayoutParams(params);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        addView(imageView);

        mRadius = ScreenUtil.dp2px(mContext, 10);
        RelativeLayout.LayoutParams paramsCircle = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        paramsCircle.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        paramsCircle.topMargin = mRadius / 2;
        paramsCircle.rightMargin = mRadius / 2;
        mCircleView = new CircleView(mContext, mRadius);
        mCircleView.setLayoutParams(paramsCircle);
        addView(mCircleView);
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
