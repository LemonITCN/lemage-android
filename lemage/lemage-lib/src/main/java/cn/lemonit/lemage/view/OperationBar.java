package cn.lemonit.lemage.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.Gravity;
import android.widget.LinearLayout;

import cn.lemonit.lemage.util.ScreenUtil;

/**
 * 底部条控件
 */
public class OperationBar extends LinearLayout {

    private final String TAG = "OperationBar";

    private Context mContext;

    private Paint mPaint;

    /**
     * 底部条有2块还是3块
     */
    private int count;

    /**
     * 每块的宽度
     */
    private int itemWidth;

    // 整个控件的宽高
    private int width;
    private int height;
    /**
     * 三个按钮
     */
    private DrawTextButton leftButton;
    private DrawTextButton rightButton;
    private DrawCircleTextButton centerButton;

    public OperationBar(Context context, int count) {
        super(context);
        mContext = context;
        this.count = count;
        this.setOrientation(LinearLayout.HORIZONTAL);
        init();
        addLeftView();
        if(count == 3) {
            addCenterView();
        }
        addRightView();
    }

    private void init() {
        // 设置透明色，去掉四个方形的角
        this.setBackgroundColor(Color.parseColor("#00000000"));
        itemWidth = ScreenUtil.dp2px(mContext, 100);
        mPaint = new Paint();
        mPaint.setStrokeWidth(2);
        mPaint.setAntiAlias(true);//抗锯齿
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#1E1E1E"));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = itemWidth * count;
        height = ScreenUtil.dp2px(getContext(), 50);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Path mPath = new Path();
        // 圆弧半径，X, Y相同
        float radiuXY = height / 2;
        // 左侧黑色按钮
        float[] radLeft = {radiuXY, radiuXY, 0f, 0f, 0f, 0f, radiuXY, radiuXY};// 每两点代表一个弧度，顺序是左上，右上，右下，左下
        mPath.addRoundRect(new RectF(0, 0, width / count * (count - 1), height), radLeft, Path.Direction.CW);
        canvas.drawPath(mPath, mPaint);

        // 右侧绿色按钮
        mPaint.setColor(Color.GREEN);
        float[] radright = {0f, 0f, radiuXY, radiuXY, radiuXY, radiuXY, 0f, 0f};
        mPath.reset();
        mPath.addRoundRect(new RectF(width / count * (count - 1), 0, width, height), radright, Path.Direction.CW);
        canvas.drawPath(mPath, mPaint);
    }

    private void addLeftView() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
        leftButton = new DrawTextButton(mContext, "预览");
        leftButton.setLayoutParams(layoutParams);
        leftButton.setGravity(Gravity.CENTER);
        this.addView(leftButton);
    }

    private void addCenterView() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
        centerButton = new DrawCircleTextButton(mContext, "原图");
        centerButton.setLayoutParams(layoutParams);
        centerButton.setGravity(Gravity.CENTER);
        this.addView(centerButton);
    }

    private void addRightView() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
        rightButton = new DrawTextButton(mContext, "完成");
        rightButton.setLayoutParams(layoutParams);
        rightButton.setGravity(Gravity.CENTER);
        this.addView(rightButton);
    }

    public void setCount(int count) {
        this.count = count;
    }
}
