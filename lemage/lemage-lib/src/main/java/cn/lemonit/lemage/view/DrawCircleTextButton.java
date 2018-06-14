package cn.lemonit.lemage.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.widget.CompoundButton;

import cn.lemonit.lemage.util.ScreenUtil;

/**
 * 原图按钮，左边是圆圈，右边是文字
 */
public class DrawCircleTextButton extends CompoundButton {

    private Context mContext;

    private Paint mPaint;
    /**
     * 需要显示的文字
     */
    private String text;
    /**
     * 选中状态，0 未选中，白色空心圆    1 选中，白色实心圆中是黑色对勾
     */
    private int status = 1;
    /**
     * 圆圈半径
     */
    private int radio;
    /**
     * 圆圈和文字的间距
     */
    private int space;
    /**
     * 文字的宽度
     */
    private float widthText;
    /**
     * 文字的高度
     */
    private float heightText;
    /**
     * 文字和圆圈的总宽度
     */
    private float widthContent;
    /**
     * 是否第一次画文字（只画一次）
     */
    private boolean firstDrawText;

    // 对号的路径相关变量
    private Path mPath;

    private int oneStartX;
    private int oneStartY;

    // 第一笔结束的点就是第二笔的起点
    private int oneEndX;
    private int oneEndY;

    private int twoEndX;
    private int twoEndY;

    public DrawCircleTextButton(Context context, String text) {
        super(context);
        mContext = context;
        this.text = text;
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStrokeWidth(2);
        mPaint.setAntiAlias(true);//抗锯齿
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(ScreenUtil.dp2px(getContext(), 16));

        radio = 16;
        space = 10;
//        setBackgroundColor(Color.RED);
        // 文字的宽度
        widthText = mPaint.measureText(text);
        // 文字和圆圈的总宽度
        widthContent = radio * 2 + space + widthText;
        // 文字的Y轴起始绘点
        Rect rect = new Rect(0,0, getWidth(), getHeight());
        Paint.FontMetrics metrics = mPaint.getFontMetrics();
        float top = metrics.top;
        float bottom = metrics.bottom;
        heightText = (int) (rect.centerY() - top/2 - bottom/2);

        firstDrawText = true;

        mPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(firstDrawText) {
            firstDrawText = false;
            drawText(canvas);
        }
        switch (status) {
            case 0:
                drawCircle(canvas);
                break;
            case 1:
                drawTick(canvas);
                break;
        }
    }

    /**
     * 未选中状态
     * @param canvas
     */
    private void drawCircle(Canvas canvas) {
        mPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle((getWidth() - widthContent) / 2 + radio, getHeight() / 2, radio, mPaint);
    }

    /**
     * 选中状态
     * @param canvas
     */
    private void drawTick(Canvas canvas) {
        // 画实心圆
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle((getWidth() - widthContent) / 2 + radio, getHeight() / 2, radio, mPaint);
        // 画对勾
        oneStartX = (int) ((getWidth() - widthContent) / 2 + radio / 2);
        oneStartY = getHeight() / 2;
        oneEndX = (int) ((getWidth() - widthContent) / 2 + radio);
        oneEndY = oneStartY + radio / 2;
        twoEndX = oneEndX + radio / 2;
        twoEndY = oneStartY - radio / 2;
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPath.moveTo(oneStartX, oneStartY);
        mPath.lineTo(oneEndX, oneEndY);
        mPath.lineTo(twoEndX, twoEndY);
        canvas.drawPath(mPath, mPaint);
    }

    /**
     * 画文字（状态变化时文字不变，只画一次）
     */
    private void drawText(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(text, (getWidth() - widthContent) / 2 + radio * 2 + space, heightText + getHeight() / 2 , mPaint);
    }
}
