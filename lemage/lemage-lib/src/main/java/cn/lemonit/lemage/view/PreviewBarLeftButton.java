package cn.lemonit.lemage.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import cn.lemonit.lemage.util.ScreenUtil;

/**
 * 预览界面的顶部条的左侧按钮
 * @author zhaoguangyang
 */
public class PreviewBarLeftButton extends CompoundButton {

    private final String TAG = "PreviewBarLeftButton";

    private Context mContext;
    private Paint mPaint;
    private Path mPath;
    // 图片总数量
    private int count;
    // 当前显示的数量
    private int currentIndex;

    private String text;
    /**
     * 图标和数字之间的间距
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

    public PreviewBarLeftButton(Context context) {
        super(context);
        mContext = context;
        init();
    }

    private void setPaint() {
        mPaint.setStrokeWidth(1);
        mPaint.setAntiAlias(true);//抗锯齿
//        mPaint.setStyle(Paint.Style.STROKE);  // 空心
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(ScreenUtil.dp2px(getContext(), 16));
    }

    private void init() {
        mPaint = new Paint();

        text = currentIndex + " / " + count;

        space = ScreenUtil.dp2px(mContext, 14);
        widthText = mPaint.measureText(text);

        // 文字的Y轴起始绘点
        Rect rect = new Rect(0,0, getWidth(), getHeight());
        Paint.FontMetrics metrics = mPaint.getFontMetrics();
        float top = metrics.top;
        float bottom = metrics.bottom;
//        heightText = (int) (rect.centerY() - top/2 - bottom/2);
        heightText = (int) (rect.centerY() - top - bottom / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mPath == null) {
            mPath = new Path();
        }
        setPaint();
        // 画数字
        drawNumber(canvas);
        // 画返回符号
        drawBackImg(canvas);
    }

    /**
     *
     */
    private void drawBackImg(Canvas canvas) {
//        setPaint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4);
        mPath.moveTo(getHeight() / 6, getHeight() / 3);
        mPath.lineTo(2, getHeight() / 2);
        mPath.lineTo(getHeight() / 6, getHeight() / 3 * 2);
//        mPath.moveTo(15, 30);
//        mPath.lineTo(2, getHeight() / 2);
//        mPath.lineTo(15, getHeight() - 30);
        canvas.drawPath(mPath, mPaint);
    }

    private void drawNumber(Canvas canvas) {
//        mPaint.reset();
//        setPaint();
//        mPaint.setStrokeWidth(4);
        canvas.drawText(text, getHeight() / 6 + space, heightText + getHeight() / 2 , mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int minimumWidth = getSuggestedMinimumWidth();
        final int minimumHeight = getSuggestedMinimumHeight();
        int width = measureWidth(minimumWidth, widthMeasureSpec);
        int height = measureHeight(minimumHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureWidth(int defaultWidth, int measureSpec) {

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);


        switch (specMode) {
            case MeasureSpec.AT_MOST:
                defaultWidth = (int) mPaint.measureText(text) + getPaddingLeft() + getPaddingRight() + getHeight();
                break;
            case MeasureSpec.EXACTLY:
                defaultWidth = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                defaultWidth = Math.max(defaultWidth, specSize);
        }
        return defaultWidth;
    }


    private int measureHeight(int defaultHeight, int measureSpec) {

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.AT_MOST:
                defaultHeight = (int) (-mPaint.ascent() + mPaint.descent()) + getPaddingTop() + getPaddingBottom();
                break;
            case MeasureSpec.EXACTLY:
                defaultHeight = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                defaultHeight = Math.max(defaultHeight, specSize);
//        1.基准点是baseline
//        2.ascent：是baseline之上至字符最高处的距离
//        3.descent：是baseline之下至字符最低处的距离
//        4.leading：是上一行字符的descent到下一行的ascent之间的距离,也就是相邻行间的空白距离
//        5.top：是指的是最高字符到baseline的值,即ascent的最大值
//        6.bottom：是指最低字符到baseline的值,即descent的最大值

                break;
        }
        return defaultHeight;
    }

    public void changeText(int mCount, int corrent) {
        count = mCount;
        currentIndex = corrent;
        text = currentIndex + " / " + count;
        invalidate();
    }
}
