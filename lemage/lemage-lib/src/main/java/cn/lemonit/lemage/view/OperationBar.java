package cn.lemonit.lemage.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.LinearLayout;

import cn.lemonit.lemage.util.ScreenUtil;

/**
 * 底部条控件
 */
public class OperationBar extends LinearLayout {

    private Context mContext;

    private Paint mPaint;

    /**
     * 底部条分2块或者3块，每块的宽度
     */
    private int itemWidth;

    public OperationBar(Context context) {
        super(context);
        mContext = context;
        this.setBackgroundColor(Color.YELLOW);
        itemWidth = ScreenUtil.dp2px(context, 80);
        mPaint = new Paint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int minimumWidth = getSuggestedMinimumWidth();
        final int minimumHeight = getSuggestedMinimumHeight();
        int width = itemWidth * 3;
        int height = measureHeight(minimumHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
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
                break;
        }
        return defaultHeight;
    }
}
