package cn.lemonit.lemage.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import cn.lemonit.lemage.util.ScreenUtil;

/**
 * 视频缩略图的开始按钮
 * @author: zhaoguangyang
 */
public class VideoStartImageView extends View {

    private String TAG = "VideoStartImageView";

    private Context mContext;
    private Paint mPaint;
    private Path mPath;
    // 圆圈半径
    private int mRadius;
    // 三角形边长
    private int sideLength;
    // 圆形颜色
    private int circleColor;
    // 三角形颜色
    private int triangleColor;
    // 圆形是否空心
    private boolean circleStroke;
    // 三角形是否空心
    private boolean triangleStroke;
    // 三角形三个顶点坐标
    private float pointA_X, pointA_Y;
    private float pointB_X, pointB_Y;
    private float pointC_X, pointC_Y;

    // 是否暂停
    private boolean isPause;

    public VideoStartImageView(Context context, int radius, int sideLength, int circleColor, int triangleColor, boolean circleStroke, boolean triangleStroke) {
        super(context);
        mContext = context;
        mRadius = radius;
        this.sideLength = sideLength;
        this.circleColor = circleColor;
        this.triangleColor = triangleColor;
        this.circleStroke = circleStroke;
        this.triangleStroke = triangleStroke;
        init();
    }

    private void init() {
        mPath = new Path();
        mPaint = new Paint();
        // 获取三角形三个顶点坐标
        getTrianglePoint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画白色实心圆圈
        drawCircle(canvas);
        if(isPause) {
            // 画暂停按钮（竖着的等号）
            drawVerticalBar(canvas);
        }else {
            // 画黑色实心三角形
            drawTriangle(canvas);
        }
    }

    /**
     * 画白色圆圈
     * @param canvas
     */
    private void drawCircle(Canvas canvas) {
        mPaint.setStrokeWidth(4);
        mPaint.setAntiAlias(true);//抗锯齿
        if(circleStroke) {
            mPaint.setStyle(Paint.Style.STROKE);
        }else {
            mPaint.setStyle(Paint.Style.FILL);
        }
//        mPaint.setColor(Color.WHITE);
        mPaint.setColor(circleColor);
//        mRadius = mRadius + 4;
//        canvas.drawCircle(mRadius, mRadius, mRadius, mPaint);
        canvas.drawCircle(mRadius + 4, mRadius + 4, mRadius, mPaint);
    }

    /**
     * 画黑色实心三角形
     * @param canvas
     */
    private void drawTriangle(Canvas canvas) {
        mPaint.reset();
//        mPaint.setStyle(Paint.Style.FILL);
        if(triangleStroke) {
            mPaint.setStyle(Paint.Style.STROKE);
        }else {
            mPaint.setStyle(Paint.Style.FILL);
        }
        mPaint.setStrokeWidth(4);
        mPaint.setAntiAlias(true);
//        mPaint.setColor(Color.BLACK);
        mPaint.setColor(triangleColor);
        mPath.moveTo(pointA_X, pointA_Y);
        mPath.lineTo(pointB_X, pointB_Y);
        mPath.lineTo(pointC_X, pointC_Y);
        mPath.close();
        canvas.drawPath(mPath, mPaint);
    }

    /**
     * 画暂停按钮(三角形右边的顶点变成左边的边长就是两个竖杠)
     * @param canvas
     */
    private void drawVerticalBar(Canvas canvas) {
        mPaint.reset();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(4);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.BLACK);
        mPath.reset();
        mPath.moveTo(pointA_X, pointA_Y);
        mPath.lineTo(pointB_X, pointB_Y);
        mPath.moveTo(mRadius * 2 - pointA_X + 8, pointA_Y);
        mPath.lineTo(mRadius * 2 - pointA_X + 8, pointB_Y);
//        mPath.close();
        canvas.drawPath(mPath, mPaint);
    }


    /**
     * 求顶点时，要考虑线宽，所以要修正偏移量
     */
    private void getTrianglePoint() {
        double ddd = (sideLength * sideLength) - ((sideLength / 2) * (sideLength / 2));
        // 等边三角形中线的长度
        double centerLine = (int) Math.sqrt(ddd);
        //  等边三角形内心到底边的距离（中线的1 / 3）
        int bottomEdge = (int) (centerLine / 3);
        //  等边三角形内心到顶点的距离 （中线的 2 / 3）
        int topEdge = (int) (centerLine / 3 * 2);
        // 点A坐标（左上）
        pointA_X = mRadius - bottomEdge + 4;
        pointA_Y = mRadius - sideLength / 2 + 4;
        // 点B坐标 （左下）
        pointB_X = pointA_X;
        pointB_Y = mRadius + sideLength / 2 + 4;
        // 点C坐标（右侧）
        pointC_X = mRadius + topEdge + 4;
        pointC_Y = mRadius + 4;

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mRadius * 2 + 8, mRadius * 2 + 8);
//        setMeasuredDimension(mRadius * 2, mRadius * 2);
    }

    /**
     * 设置暂停
     */
    public void setPause(boolean pause) {
        isPause = pause;
        invalidate();
    }
}
