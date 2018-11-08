package cn.lemonit.lemage.view.select_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.Log;
import android.widget.CompoundButton;

import cn.lemonit.lemage.util.ScreenUtil;

/**
 * 预览按钮
 * @author zhaoguangyang
 */
public class DrawTextButton extends CompoundButton {

    private Context mContext;
    /**
     * 画笔
     */
    private Paint mPaint;

    /**
     * 需要显示的文字
     */
    private String text;

    private int width, height;

    public DrawTextButton(Context context, String text) {
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
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mPaint != null && !TextUtils.isEmpty(text)) {
            Rect rect = new Rect(0,0, getWidth(), getHeight());
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
            float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom

            int baseLineY = (int) (rect.centerY() - top/2 - bottom/2);//基线中间点的y轴计算公式
            mPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(text, rect.centerX(), baseLineY, mPaint);
        }
    }

    public void changeTextColor(int colorNew) {
        mPaint.setColor(colorNew);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int minimumWidth = getSuggestedMinimumWidth();
        final int minimumHeight = getSuggestedMinimumHeight();
        Log.e("YView", "---minimumWidth = " + minimumWidth + "");
        Log.e("YView", "---minimumHeight = " + minimumHeight + "");
        int width = measureWidth(minimumWidth, widthMeasureSpec);
        int height = measureHeight(minimumHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    private int measureWidth(int defaultWidth, int measureSpec) {

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);


        switch (specMode) {
            case MeasureSpec.AT_MOST:
                defaultWidth = (int) mPaint.measureText(text) + getPaddingLeft() + getPaddingRight();
                break;
            case MeasureSpec.EXACTLY:
                Log.e("YViewWidth", "---speMode = EXACTLY");
                defaultWidth = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                Log.e("YViewWidth", "---speMode = UNSPECIFIED");
                defaultWidth = Math.max(defaultWidth, specSize);
        }
        return defaultWidth;
    }


    private int measureHeight(int defaultHeight, int measureSpec) {

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        Log.e("YViewHeight", "---speSize = " + specSize + "");

        switch (specMode) {
            case MeasureSpec.AT_MOST:
                defaultHeight = (int) (-mPaint.ascent() + mPaint.descent()) + getPaddingTop() + getPaddingBottom();
                Log.e("YViewHeight", "---speMode = AT_MOST");
                break;
            case MeasureSpec.EXACTLY:
                defaultHeight = specSize;
                Log.e("YViewHeight", "---speSize = EXACTLY");
                break;
            case MeasureSpec.UNSPECIFIED:
                defaultHeight = Math.max(defaultHeight, specSize);
                Log.e("YViewHeight", "---speSize = UNSPECIFIED");
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
}
