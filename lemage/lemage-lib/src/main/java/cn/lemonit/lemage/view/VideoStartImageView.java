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
    // 三角形三个顶点坐标
    private float pointA_X, pointA_Y;
    private float pointB_X, pointB_Y;
    private float pointC_X, pointC_Y;

    public VideoStartImageView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    private void init() {
        mRadius = ScreenUtil.dp2px(mContext, 50);   // 圆圈半径  20 dp
        sideLength = ScreenUtil.dp2px(mContext, 40); // 三角形边长

        mPath = new Path();
        mPaint = new Paint();
        mPaint.setStrokeWidth(4);
        mPaint.setAntiAlias(true);//抗锯齿
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        // 获取三角形三个顶点坐标
        getTrianglePoint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画白色空心圆圈
        drawCircle(canvas);
        // 画白色实心三角形
        drawTriangle(canvas);
    }

    /**
     * 画白色空心圆圈
     * @param canvas
     */
    private void drawCircle(Canvas canvas) {
        canvas.drawCircle(mRadius + 4, mRadius + 4, mRadius, mPaint);
    }

    /**
     * 画白色实心三角形
     * @param canvas
     */
    private void drawTriangle(Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        mPath.moveTo(pointA_X, pointA_Y);
        mPath.lineTo(pointB_X, pointB_Y);
        mPath.lineTo(pointC_X, pointC_Y);
        mPath.close();
        canvas.drawPath(mPath, mPaint);
    }


    private void getTrianglePoint() {
        double ddd = (sideLength * sideLength) - ((sideLength / 2) * (sideLength / 2));
        // 等边三角形中线的长度
        double centerLine = (int) Math.sqrt(ddd);
        //  等边三角形内心到底边的距离（中线的1 / 3）
        int bottomEdge = (int) (centerLine / 3);
        //  等边三角形内心到顶点的距离 （中线的 2 / 3）
        int topEdge = (int) (centerLine - bottomEdge);
        // 点A坐标（左上）
        pointA_X = mRadius - bottomEdge;
        pointA_Y = mRadius - sideLength / 2;
        // 点B坐标 （左下）
        pointB_X = pointA_X;
        pointB_Y = pointA_Y + sideLength;
        // 点C坐标（右侧）
        pointC_X = mRadius + topEdge;
        pointC_Y = mRadius;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mRadius * 2 + 8, mRadius * 2 + 8);
    }
}
