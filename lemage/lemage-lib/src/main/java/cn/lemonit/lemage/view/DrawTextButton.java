package cn.lemonit.lemage.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;

import cn.lemonit.lemage.util.ScreenUtil;

/**
 * 预览按钮
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
            Rect rect = new Rect(0,0,getWidth(),getHeight());
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            float top = fontMetrics.top;//为基线到字体上边框的距离,即上图中的top
            float bottom = fontMetrics.bottom;//为基线到字体下边框的距离,即上图中的bottom

            int baseLineY = (int) (rect.centerY() - top/2 - bottom/2);//基线中间点的y轴计算公式
            mPaint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(text, rect.centerX(), baseLineY, mPaint);
        }
    }
}
