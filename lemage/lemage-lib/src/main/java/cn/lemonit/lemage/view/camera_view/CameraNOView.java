package cn.lemonit.lemage.view.camera_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

import cn.lemonit.lemage.util.ScreenUtil;

/**
 * camera拍照完成后显示的取消按钮  差号
 * @author zhaoguangyang
 */
public class CameraNOView extends View {

    private Context mContext;

    private Paint mPaintBackGround;
    private Paint mPaintContent;
    /**
     * view的长宽
     */
    private int viewWidth;
    private int viewHeight;
    /**
     * 圆心坐标
     */
    private int circleX, circleY;
    /**
     * 圆半径
     */
    private int radius;

    public CameraNOView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    private void init() {
        mPaintBackGround = new Paint();
        mPaintBackGround.setStrokeWidth(4);
        mPaintBackGround.setAntiAlias(true);
        mPaintBackGround.setColor(Color.WHITE);
        mPaintBackGround.setStyle(Paint.Style.FILL);

        mPaintContent = new Paint();
        mPaintContent.setStrokeWidth(6);
        mPaintContent.setAntiAlias(true);
        mPaintContent.setColor(Color.RED);
        mPaintContent.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画圆
        canvas.drawCircle(circleX, circleY, radius, mPaintBackGround);
        // 画差号
        canvas.drawLine(circleX - 20, circleY - 20, circleX + 20, circleY + 20, mPaintContent);
        canvas.drawLine(circleX - 20, circleY + 20, circleX + 20, circleY - 20, mPaintContent);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewHeight = ScreenUtil.getScreenHeight(mContext) / 6;
        viewWidth = viewHeight;
        circleX = viewWidth / 2;
        circleY = viewHeight / 2;
        radius = viewWidth / 2 - 20;
        setMeasuredDimension(viewWidth, viewHeight);
    }
}
