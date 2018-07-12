package cn.lemonit.lemage.view.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.view.View;

import java.math.BigDecimal;

import cn.lemonit.lemage.util.ScreenUtil;

/**
 * 右上角切换前后摄像头的view
 * @author zhaoguangyang
 */
public class CameraEvertButton extends View {

    private Context mContext;

    private Path mPath;
    private Path mPathLine;

    private Paint mPaint;
    private Paint mPaintLine;

    /**
     * 左侧坐标点
     */
    private Point mPointLeftA;
    private Point mPointLeftB;
    private Point mPointLeftC;
    private Point mPointLeftD;
    private Point mPointLeftE;
    private Point mPointLeftF;
    private Point mPointLeftG;
    private Point mPointControlAFOne;
    private Point mPointControlAFTwo;
    private Point mPointControlBDOne;
    /**
     * 右侧坐标点
     */
    private Point mPointRightA;
    private Point mPointRightB;
    private Point mPointRightC;
    private Point mPointRightD;
    private Point mPointRightE;
    private Point mPointRightF;
    private Point mPointRightG;
    private Point mPointControlBGOne;
    private Point mPointControlBGTwo;
    private Point mPointControlDFOne;

    /**
     * view的长宽
     */
    private int viewWidth;
    private int viewHeight;

    /**
     * 基准长宽
     */
    private int normWidth = 280;
    private int normHeight = 190;

    /**
     * 和基准尺寸的比例（自适应屏幕）
     */
    private float scaleDistance;

    public CameraEvertButton(Context context) {
        super(context);
        mContext = context;
        initScale();
        initPaint();
        initPoint();
    }

    /**
     * 以屏幕宽度的 1 / 8 适配屏幕
     */
    private void initScale() {
        viewWidth = ScreenUtil.getScreenWidth(mContext) / 8;   // 480
        viewHeight = 190 * viewWidth / 280;
        // 缩放比例，保留小数点后2位
        scaleDistance = (float) new BigDecimal((float)normWidth / viewWidth).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 初始化画笔和路径
     */
    private void initPaint() {
        if(mPaint == null) {
            mPaint = new Paint();
        }
        mPaint = new Paint();
        mPaint.setStrokeWidth(4);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);

        if(mPath == null) {
            mPath = new Path();
        }


        if(mPaintLine == null) {
            mPaintLine = new Paint();
        }
        mPaintLine = new Paint();
        mPaintLine.setStrokeWidth(4);
        mPaintLine.setAntiAlias(true);
        mPaintLine.setColor(Color.BLACK);
        mPaintLine.setStyle(Paint.Style.FILL);

        if(mPathLine == null) {
            mPathLine = new Path();
        }
    }

    /**
     * 初始化各个点坐标
     */
    private void initPoint() {
        // 左侧箭头外线
        mPointLeftA = getPoint(115, 35);
        mPointLeftF = getPoint(115, 165);
        mPointControlAFOne = getPoint(-30, 50);
        mPointControlAFTwo = getPoint(-30, 150);
        // 左侧箭头内线
        mPointLeftB = getPoint(115, 40);
        mPointLeftD = getPoint(115, 115);
        mPointControlBDOne = getPoint(0, 95);
        // 左侧箭头的三角形
        mPointLeftC = getPoint(115, 95);
        mPointLeftE = getPoint(155, 140);
        mPointLeftG = getPoint(115, 185);
        // 右侧箭头的外线
        mPointRightB = getPoint(170, 20);
        mPointRightG = getPoint(170, 150);
        mPointControlBGOne = getPoint(315, 35);
        mPointControlBGTwo = getPoint(315, 115);
        // 右侧箭头内线
        mPointRightD = getPoint(170, 70);
        mPointRightF = getPoint(170, 145);
        mPointControlDFOne = getPoint(285, 90);

        // 右侧箭头
        mPointRightA = getPoint(170, 1);
        mPointRightC = getPoint(125, 46);
        mPointRightE = getPoint(170, 91);
    }

    /**
     * 根据原始图的个点坐标适配屏幕的个点坐标
     */
    private Point getPoint(int width, int height) {
        Point point = new Point();
        point.x = (int) (width / scaleDistance);
        point.y = (int) (height / scaleDistance);
        return point;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // 画左侧曲线外线
        mPath.moveTo(mPointLeftA.x, mPointLeftA.y);
        mPath.cubicTo(mPointControlAFOne.x, mPointControlAFOne.y, mPointControlAFTwo.x, mPointControlAFTwo.y, mPointLeftF.x, mPointLeftF.y);

        // 画左侧下封闭
        mPath.lineTo(mPointLeftD.x, mPointLeftD.y);

        // 画左侧曲线内线
        mPath.quadTo(mPointControlBDOne.x, mPointControlBDOne.y, mPointLeftB.x, mPointLeftB.y);

        // close的作用的封闭路径，如果连接最后一个点和最初一个点任然无法形成闭合的区域，那么close什么也不做
        mPath.close();
        canvas.drawPath(mPath, mPaint);

        // 画左侧箭头
        mPath.reset();
        mPath.moveTo(mPointLeftC.x, mPointLeftC.y);
        mPath.lineTo(mPointLeftG.x, mPointLeftG.y);
        mPath.lineTo(mPointLeftE.x, mPointLeftE.y);
        mPath.close();
        canvas.drawPath(mPath, mPaint);

        // 画右侧曲线外线
        mPath.moveTo(mPointRightB.x, mPointRightB.y);
        mPath.cubicTo(mPointControlBGOne.x, mPointControlBGOne.y, mPointControlBGTwo.x, mPointControlBGTwo.y, mPointRightG.x, mPointRightG.y);

        // 画右侧曲线下封闭
        mPath.lineTo(mPointRightF.x, mPointRightF.y);

        // 画右侧箭头内线
//        mPath.quadTo(mPointControlDFOne.x, mPointControlDFOne.y, mPointRightD.x, mPointRightD.y);
        mPath.quadTo(mPointControlDFOne.x, mPointControlDFOne.y, mPointRightD.x, mPointRightD.y);
        mPath.close();
        canvas.drawPath(mPath, mPaint);

        // 画右侧箭头
        mPath.moveTo(mPointRightA.x, mPointRightA.y);
        mPath.lineTo(mPointRightE.x, mPointRightE.y);
        mPath.lineTo(mPointRightC.x, mPointRightC.y);
        mPath.close();
        canvas.drawPath(mPath, mPaint);

//        // 画两个箭头线
//        mPathLine.moveTo(mPointLeftD.x, mPointLeftD.y);
//        mPathLine.lineTo(mPointLeftF.x, mPointLeftF.y);
//        canvas.drawPath(mPathLine, mPaintLine);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(viewWidth, viewHeight);
//        Log.e("EvertCameraButton", "宽度 ========= " + viewWidth);    // 96
//        Log.e("EvertCameraButton", "高度 ========= " + viewHeight);  // 80
    }
}
