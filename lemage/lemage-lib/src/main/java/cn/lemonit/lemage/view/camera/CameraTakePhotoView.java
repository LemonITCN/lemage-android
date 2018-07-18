package cn.lemonit.lemage.view.camera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.View;

import cn.lemonit.lemage.util.ScreenUtil;

/**
 * 摄像画面中的圆圈按钮，短按拍照，长按录像
 * @author zhaoguangyang
 */
public class CameraTakePhotoView extends View {

    private Context mContext;

    private Paint mPaintOuter;
    private Paint mPaintInner;

    /**
     * view的长宽
     */
    private int viewWidth;
    private int viewHeight;

    public CameraTakePhotoView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    private void init() {
        mPaintOuter = new Paint();
        mPaintOuter.setStrokeWidth(4);
        mPaintOuter.setAntiAlias(true);
        mPaintOuter.setColor(Color.parseColor("#B3CF95"));
        mPaintOuter.setStyle(Paint.Style.FILL);

        mPaintInner = new Paint();
        mPaintInner.setStrokeWidth(4);
        mPaintInner.setAntiAlias(true);
        mPaintInner.setColor(Color.parseColor("#D4EABC"));
        mPaintInner.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 外圈
        canvas.drawCircle(viewWidth / 2, viewHeight / 2, viewWidth / 2 - 20, mPaintOuter);
        // 内圈
        canvas.drawCircle(viewWidth / 2, viewHeight / 2, viewWidth / 2 - 30, mPaintInner);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewHeight = ScreenUtil.getScreenHeight(mContext) / 6;
        viewWidth = viewHeight;
        setMeasuredDimension(viewWidth, viewHeight);
    }


}
