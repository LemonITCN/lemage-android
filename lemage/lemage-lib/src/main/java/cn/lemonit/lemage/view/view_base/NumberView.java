package cn.lemonit.lemage.view.view_base;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

/**
 * @author zhaoguangyang
 * @date 2018/11/9
 * Describe: 图片右上方的序号view
 */
public class NumberView extends View {

    protected Context mContext;
    /**
     * 状态flag
     */
    protected int status;
    /**
     * 未选中状态
     */
    public final int STUATUS_SELECTED = 0;
    /**
     * 选中状态
     */
    public final int STUATUS_UNSELECTED = 1;

    protected Path mPath;

    protected Paint mPaint;


    public NumberView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    protected void init() {
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setStrokeWidth(2);
        mPaint.setAntiAlias(true);//抗锯齿
        mPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * 更新选中状态
     * @param mStatus
     */
    public void changeStatus(int mStatus){
        status = mStatus;
        invalidate();
    }

    /**
     * 获取状态
     * @return
     */
    public int getStatus() {
        return status;
    }
}
