package cn.lemonit.lemage.view.camera_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.view.View;

import cn.lemonit.lemage.util.ScreenUtil;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * 手电筒
 * @author zhaoguangyang
 */
public class BrightView extends View {

    private Context mContext;
    private Path mPath;
    private Paint mPaint;
    // 是否打开了手电筒
    private boolean isLight;
    // 控件的宽高
    private int viewWidth, viewHeight;
    // 线宽
    private final int mPathStrokeWidth = 4;
    // 最上面矩形的高
    private int topRectHeight;
    /**
     * 以下是分解控件各个图形顶点
     */
    // 最上面的矩形四个顶点
    private Point mPointA, mPointB, mPointC, mPointD;
    private RectF rectFTop;
    // 半圆形（要先确定半圆弧所在的矩形）
    private RectF rectFArc;
    private Point mPointE, mPointF, mPointG, mPointH;
    // 最下面是一个类似矩形，(G,H是和圆弧相交的上面两个顶点，I,J是类似矩形下面的顶点)画线就可以
    private Point mPointI, mPointJ, mPointK, mPointL;
    private RectF rectFBottom;
    // 圆弧半径
    private int radius;
    // 圆心坐标
    private Point mPointCenter;
    // 角度（此角度是圆弧与下面长条矩形相交部分的右侧交点对于圆弧3点钟方向的偏移量，要计算圆弧与矩形相交的两个点坐标）
    private int angle = 70;
    // 最下面长条矩形的横杆
    private Point mPointM, mPointN;
    // 最下面长条矩形的竖杠
    private Point mPointO, mPointP;

    public BrightView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mPathStrokeWidth);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);

        mPath = new Path();

        // 要考虑线宽
        viewWidth = ScreenUtil.getScreenWidth(mContext) / 12;
        viewHeight = (int) (viewWidth * 2);

        topRectHeight = viewWidth / 6;

        // 最上面矩形(顺序是左上，右上，左下，右下)
        mPointA = new Point(mPathStrokeWidth / 2, mPathStrokeWidth / 2);
        mPointB = new Point(viewWidth - mPathStrokeWidth / 2, mPathStrokeWidth / 2);
        mPointC = new Point(mPathStrokeWidth / 2, topRectHeight + mPathStrokeWidth / 2);
        mPointD = new Point(viewWidth - mPathStrokeWidth / 2, topRectHeight + mPathStrokeWidth / 2);
        rectFTop = new RectF(mPointA.x, mPointA.y, mPointD.x, mPointD.y);
        // 半圆弧所在矩形的顶点(顺序是左上，右上，左下，右下)
        mPointE = new Point(mPathStrokeWidth / 2, topRectHeight+ mPathStrokeWidth / 2 + viewWidth / 2 - viewWidth - mPathStrokeWidth / 2);
        mPointF = new Point(viewWidth - mPathStrokeWidth / 2, topRectHeight+ mPathStrokeWidth / 2 + viewWidth / 2 - viewWidth - mPathStrokeWidth / 2);
        mPointG = new Point(mPathStrokeWidth / 2, topRectHeight+ mPathStrokeWidth / 2 + viewWidth / 2);
        mPointH = new Point(viewWidth - mPathStrokeWidth / 2, topRectHeight+ mPathStrokeWidth / 2 + viewWidth / 2);
        rectFArc = new RectF(mPointE.x, mPointE.y, mPointH.x, mPointH.y);
        // 画最下面长条的矩形（顶端和圆弧重叠）(顺序是左上，右上，左下，右下)
        radius = (viewWidth - mPathStrokeWidth) / 2;
        mPointCenter = new Point(radius + mPathStrokeWidth / 2,topRectHeight + mPathStrokeWidth / 2);

        mPointI = new Point();
        mPointJ = new Point();
        mPointJ.x = (int) (mPointCenter.x + radius * cos(angle * PI / 180));
        mPointJ.y = (int) (mPointCenter.y + radius * sin(angle * PI /180) + mPathStrokeWidth / 2);
        mPointI.y = mPointJ.y;
        mPointI.x = (int) (mPointCenter.x + radius * cos((180 - angle) * PI / 180));

        mPointK = new Point(mPointI.x, viewHeight - mPathStrokeWidth / 2);
        mPointL = new Point(mPointJ.x, viewHeight - mPathStrokeWidth / 2);
        // 最下面长条矩形中的横杆
        mPointM = new Point(mPointK.x, mPointK.y - (mPointL.x - mPointK.x) * 2 / 3);
        mPointN = new Point(mPointL.x, mPointK.y - (mPointL.x - mPointK.x) * 2 / 3);
        // 最下面长条矩形中的竖杠
        mPointO = new Point(mPointCenter.x, ((mPointM.y - radius) - (mPointN.x - mPointM.x)) / 2 + radius + mPathStrokeWidth);
        mPointP = new Point(mPointCenter.x, mPointO.y + (mPointN.x - mPointM.x));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(isLight) {
            mPaint.setColor(Color.GREEN);
        }else {
            mPaint.setColor(Color.WHITE);
        }
        // 画最上面矩形
        canvas.drawRect(rectFTop, mPaint);
        // 画圆弧形
        canvas.drawArc(rectFArc, 0, 360, false, mPaint);
        // 画最下面的长条矩形
        mPath.moveTo(mPointI.x, mPointI.y);
        mPath.lineTo(mPointK.x, mPointK.y);
        mPath.lineTo(mPointL.x, mPointL.y);
        mPath.lineTo(mPointJ.x, mPointJ.y);
        // 画长条矩形横杆
        mPath.moveTo(mPointM.x, mPointM.y);
        mPath.lineTo(mPointN.x, mPointN.y);
        // 画长条矩形竖条
        mPath.moveTo(mPointO.x, mPointO.y);
        mPath.lineTo(mPointP.x, mPointP.y);
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(viewWidth, viewHeight);
    }

    public void setLight(boolean light) {
        isLight = light;
        invalidate();
    }

    public boolean isLight() {
        return isLight;
    }
}
