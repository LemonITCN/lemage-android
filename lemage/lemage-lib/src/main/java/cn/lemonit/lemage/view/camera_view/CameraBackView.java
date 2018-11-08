package cn.lemonit.lemage.view.camera_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.view.View;

import cn.lemonit.lemage.util.ScreenUtil;

/**
 * 拍摄画面返回键(向下箭头)
 * @author zhaoguangyang
 */
public class CameraBackView extends View {

    private Context mContext;

    private Path mPath;
    private Paint mPaint;

    private Point mPointA, mPointB, mPointC;

    /**
     * view的长宽
     */
    private int viewWidth;
    private int viewHeight;

    public CameraBackView(Context context) {
        super(context);
        mContext = context;
        initScale();
        init();
    }

    private void initScale() {
        viewWidth = ScreenUtil.getScreenWidth(mContext) / 10;
        viewHeight = viewWidth;
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStrokeWidth(4);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);

        mPath = new Path();

        mPointA = new Point(0, viewWidth / 4);
        mPointB = new Point(viewWidth / 2, viewWidth / 4 * 3);
        mPointC = new Point(viewWidth, viewWidth / 4);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(mPointA.x, mPointA.y, mPointB.x, mPointB.y, mPaint);
        canvas.drawLine(mPointB.x - 1, mPointB.y + 1, mPointC.x, mPointC.y, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(viewWidth, viewHeight);
    }
}
