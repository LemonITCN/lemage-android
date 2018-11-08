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
 * camera拍照完成后显示的确定按钮 （对号）
 * @author zhaoguangyang
 */
public class CameraOKView extends View {

    private Context mContext;

    private Paint mPaintBackGround;
    private Paint mPaintContent;

    private Path mPath;

    private Point mPointA, mPointB, mPointC;

    // 圆半径
    private int radius;
    // 圆心坐标
    private int circleX, circleY;
    /**
     * view的长宽
     */
    private int viewWidth;
    private int viewHeight;

    public CameraOKView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    private void init() {
        mPaintBackGround = new Paint();
        mPaintBackGround.setStrokeWidth(4);
        mPaintBackGround.setAntiAlias(true);
        mPaintBackGround.setColor(Color.GREEN);
        mPaintBackGround.setStyle(Paint.Style.FILL);

        mPaintContent = new Paint();
        mPaintContent.setStrokeWidth(6);
        mPaintContent.setAntiAlias(true);
        mPaintContent.setColor(Color.WHITE);
        mPaintContent.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画圆
        canvas.drawCircle(circleX, circleY, radius, mPaintBackGround);
        // 画对号
        if(mPath == null) {
            mPath = new Path();
        }
        mPath.moveTo(mPointA.x, mPointA.y);
        mPath.lineTo(mPointB.x, mPointB.y);
        mPath.lineTo(mPointC.x, mPointC.y);
        canvas.drawPath(mPath, mPaintContent);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewHeight = ScreenUtil.getScreenHeight(mContext) / 6;
        viewWidth = viewHeight;
        radius = viewWidth / 2 - 20;
        circleX = viewWidth / 2;
        circleY = viewHeight / 2;
        mPointA = new Point(circleX - radius / 2, circleY);
        mPointB = new Point(circleX, circleY + radius / 2);
        mPointC = new Point(circleX + radius / 2, circleY - radius / 2);
        setMeasuredDimension(viewWidth, viewHeight);
    }
}
