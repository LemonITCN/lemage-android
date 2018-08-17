package cn.lemonit.lemage.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.View;

import cn.lemonit.lemage.util.ScreenUtil;

/**
 * 小圆圈
 * @author zhaoguangyang
 */
public class CircleView extends View {

    private final String TAG = "CircleView";

    private Context mContext;

    // 圆圈半径
    private int mRadius;

    // 画笔
    private Paint mPaint;

    // 状态flag（0未选中状态， 1选中状态里面是数字， 2文件夹选中状态里面是对号）
    private int status;

    // 如果是选中状态里面是数字
    private int number;

    // 对号的路径相关变量
    private Path mPath;

    private int oneStartX;
    private int oneStartY;

    // 第一笔结束的点就是第二笔的起点
    private int oneEndX;
    private int oneEndY;

    private int twoEndX;
    private int twoEndY;

    /**
     * 选中时的高亮颜色
     */
    private int mColor;

    public CircleView(Context context, int radius, int color) {
        super(context);
        mContext = context;
        mRadius = radius;
        mColor = color;
        init();
    }

    private void init() {
        // 初始化对号的路径
        mPath = new Path();
        oneStartX = mRadius / 2;
        oneStartY = mRadius;
        oneEndX = mRadius;
        oneEndY = mRadius / 2 * 3;
        twoEndX = mRadius / 2 * 3;
        twoEndY = mRadius / 2;
    }

    private void initPaint() {
        if(mPaint == null) {
            mPaint = new Paint();
        }
        mPaint.setStrokeWidth(2);
        mPaint.setAntiAlias(true);//抗锯齿
        mPaint.setStyle(Paint.Style.FILL);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mRadius * 2, mRadius * 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (status) {
            case 0:
                drawGray(canvas);
                break;
            case 1:
                drawLightNumber(canvas);
                break;
            case 2:
                drawLightTick(canvas);
                break;
        }
    }

    /**
     * 未选中状态，灰色圆圈
     */
    private void drawGray(Canvas canvas) {
        initPaint();
        mPaint.setColor(Color.GRAY);
        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
    }

    /**
     * 选中状态，高亮，数字
     */
    private void drawLightNumber(Canvas canvas) {
        initPaint();
//        mPaint.setColor(Color.GREEN);
        mPaint.setColor(mColor);
        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
        mPaint.setColor(Color.WHITE);
        int size = ScreenUtil.dp2px(getContext(), 12);
        mPaint.setTextSize(size);
        mPaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText(String.valueOf(number), mRadius, mRadius + size / 3, mPaint);
    }

    /**
     * 文件夹选中状态，高亮，勾
     */
    private void drawLightTick(Canvas canvas) {
        initPaint();
//        mPaint.setColor(Color.GREEN);
        mPaint.setColor(mColor);
        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
        if(mPath == null) {
            mPath = new Path();
        }
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);

        mPath.moveTo(oneStartX, oneStartY);
        mPath.lineTo(oneEndX, oneEndY);
        mPath.lineTo(twoEndX, twoEndY);

        // 如果关闭，画的是区域，不再是线
//        mPath.close();
        canvas.drawPath(mPath, mPaint);
    }

    /**
     * 改变显示的状态
     * @param mStatus
     */
    public void changeStatus(int mStatus, int mNumber) {
        status = mStatus;
        number = mNumber;
        invalidate();
    }

    public int getStatus() {
        return status;
    }
}
