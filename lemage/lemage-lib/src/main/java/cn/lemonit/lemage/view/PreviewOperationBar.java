package cn.lemonit.lemage.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.widget.LinearLayout;

import cn.lemonit.lemage.util.ScreenUtil;

/**
 *  预览界面的底部条，只有完成一个按钮
 */
public class PreviewOperationBar extends LinearLayout {

    private Context mContext;

    private Paint mPaint;
    // 整个控件的宽高
    private int width;
    private int height;
    /**
     * 适配屏幕高度
     */
    private int operationHeight;
    private int operationWidth;

    // 按钮
    private DrawTextButton mDrawTextButton;

    public PreviewOperationBar(Context context) {
        super(context);
        mContext = context;
        init();
        addView();
    }

    private void init() {
        // 设置透明色，去掉四个方形的角
        this.setBackgroundColor(Color.parseColor("#00000000"));
        operationHeight = ScreenUtil.getScreenHeight(mContext) / 15;
        operationWidth = ScreenUtil.getScreenWidth(mContext) / 4;
//        itemWidth = ScreenUtil.dp2px(mContext, 100);
        mPaint = new Paint();
        mPaint.setStrokeWidth(2);
        mPaint.setAntiAlias(true);//抗锯齿
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#1E1E1E"));
    }

    private void addView() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mDrawTextButton = new DrawTextButton(mContext, "完成");
        mDrawTextButton.setLayoutParams(layoutParams);
        addView(mDrawTextButton);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = operationWidth;
//        height = ScreenUtil.dp2px(getContext(), 50);
        height = operationHeight;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.GREEN);
        float radiuXY = height / 2;
        float[] radright = {radiuXY, radiuXY, radiuXY, radiuXY, radiuXY, radiuXY, radiuXY, radiuXY};
        Path mPath = new Path();
        mPath.addRoundRect(new RectF(0, 0, width, height), radright, Path.Direction.CW);
        canvas.drawPath(mPath, mPaint);
    }
}
