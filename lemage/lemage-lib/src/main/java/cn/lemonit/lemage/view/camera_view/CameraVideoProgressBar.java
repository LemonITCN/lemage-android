package cn.lemonit.lemage.view.camera_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import java.math.BigDecimal;

import cn.lemonit.lemage.util.ScreenUtil;

/**
 * 录像时的圆形进度条
 * @author zhaoguangyang
 */
public class CameraVideoProgressBar extends View {

    private String TAG = "CameraVideoProgressBar";

    private Context mContext;
    // 外圈画笔
    private Paint mPaintOuter;
    // 内圈画笔
    private Paint mPaintInner;
    // 进度画笔
    private Paint mPaintProgress;
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
     * 外圆半径
     */
    private int radiusOuter;
    /**
     * 外圆线宽
     */
    private int lineWidth;
    /**
     * //用于定义的圆弧的形状和大小的界限
     */
    private RectF mRectF;
    /**
     * 录像总时长(毫秒)
     */
    private long totalTime;
    /**
     * 间隔时间(多久更新一下进度条)
     */
    private int intervalTime;
    /**
     * 比例
     */
    private float scale;
    /**
     * 进度条变化的弧形角度
     */
    private float sweepAngle;
    /**
     * 计时器
     */
    private CountDownTimer mCountDownTimer;
    /**
     * 录像结束回调
     */
    private CameraVideoFinishCallback mCameraVideoFinishCallback;
    /**
     * 如果录制时间小于3秒就录制到3秒，此变量用于到3秒时跳出计时器，结束录制
     */
    public boolean minVideoTimeStop;

    /**
     * 正常录制视频，时间满
     */
    public static int VIDEO_NORMAL = 0;
    /**
     * 正常录制了视频，但是时间未满
     */
    public static int VIDEO_WARN = 1;
    /**
     * 录制视频时间少于3秒，录制失败
     */
    public static int VIDEO_ERROR = 2;

    public CameraVideoProgressBar(Context context) {
        super(context);
        mContext = context;
        init();
    }

    private void init() {
        lineWidth = 12;

        mPaintOuter = new Paint();
        mPaintOuter.setStrokeWidth(lineWidth);
        mPaintOuter.setAntiAlias(true);
//        mPaintOuter.setColor(Color.GRAY);
        mPaintOuter.setColor(Color.parseColor("#B3CF95"));
        mPaintOuter.setStyle(Paint.Style.STROKE);

        mPaintInner = new Paint();
        mPaintInner.setStrokeWidth(lineWidth);
        mPaintInner.setAntiAlias(true);
//        mPaintInner.setColor(Color.WHITE);
        mPaintInner.setColor(Color.parseColor("#D4EABC"));
        mPaintInner.setStyle(Paint.Style.FILL);

        mPaintProgress = new Paint();
        mPaintProgress.setStrokeWidth(lineWidth);
        mPaintProgress.setAntiAlias(true);
//        mPaintProgress.setColor(Color.GREEN);
        mPaintProgress.setColor(Color.parseColor("#5CAB00"));
        mPaintProgress.setStyle(Paint.Style.STROKE);

        intervalTime = 50;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewHeight = ScreenUtil.getScreenHeight(mContext) / 6;
        viewWidth = viewHeight;
        circleX = viewWidth / 2;
        circleY = viewHeight / 2;
        radiusOuter = viewWidth / 2;
        //用于定义的圆弧的形状和大小的界限
        mRectF = new RectF(circleX - radiusOuter + lineWidth, circleY - radiusOuter + lineWidth, circleX + radiusOuter - lineWidth, circleY + radiusOuter - lineWidth);
        setMeasuredDimension(viewWidth, viewHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画外圆
        canvas.drawCircle(circleX, circleY, radiusOuter - lineWidth, mPaintOuter);
        // 画内圆
        canvas.drawCircle(circleX, circleY, radiusOuter - lineWidth - 6, mPaintInner);
        // 画进度
        // 参数1 ：起始角度，0-360   0度是3点钟方向
        // 参数2 ：从起始角度要画多少角度
        // 参数3 ：是否连接圆心
        canvas.drawArc(mRectF, -90, sweepAngle, false, mPaintProgress);  //根据进度画圆弧
    }

    /**
     * 传入的录像时长
     * @param time
     */
    public void setTime(long time) {
        totalTime = time * 1000;
    }

    /**
     * 开始计时录像， 进度条变化
     */
    public void startVideo() {
        if (mCountDownTimer == null) {
            mCountDownTimer = new CountDownTimer(totalTime, intervalTime) {
                @Override
                public void onTick(long millisUntilFinished) {
                    // 调用者发来信号开始判断是否到3秒
                    if(minVideoTimeStop) {
                        if((totalTime - millisUntilFinished) > 2 * 1000 && (totalTime - millisUntilFinished) < 4 * 1000) {
                            // 录像到3秒后回调
                            handler.sendEmptyMessage(VIDEO_NORMAL);
                        }
                    }
                    scale = (float) new BigDecimal((float)(millisUntilFinished) / totalTime).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    scale = 1 - scale;
//                    Log.e(TAG, "scale ================== " + scale);
                    sweepAngle = scale * 360;
                    // 如果不到3秒用户就把手指抬起，继续录制到3秒，但是画面不再更新，而是恢复到初始状态
                    if(minVideoTimeStop) {
                        sweepAngle = 0;
                    }
                    invalidate();
                }

                @Override
                public void onFinish() {
                    Log.e(TAG, "计时结束");
                    sweepAngle = 360;
                    invalidate();
                    // 当计时结束时，其实进度条还差最后一格，让画完，所以稍微延迟一点回调
                    handler.sendEmptyMessageDelayed(VIDEO_WARN, 200);
                }
            };
        }
        mCountDownTimer.start();
    }

    /**
     * 录像结束回调
     */
    public interface CameraVideoFinishCallback {
        // 有3种录像结束，一种是录制时间太短，强制录制到3秒后结束  2    一种是录像时间结束，自然完成， 1      一种是手指中间离开，自动完成  0
        void cameraVideoFinish(int type);
    }

    public void setCameraVideoFinishCallback(CameraVideoFinishCallback mCameraVideoFinishCallback) {
        this.mCameraVideoFinishCallback = mCameraVideoFinishCallback;
    }

    /**
     * 计划录制10秒，但是5秒后手指就抬起了，此时停止录制，调用此方法
     */
    public void stopVideo() {
        if (mCountDownTimer == null) {
            return;
        }
        mCountDownTimer.cancel();
        scale = 0;
        sweepAngle = 0;
        intervalTime = 0;
        minVideoTimeStop = false;
        mCameraVideoFinishCallback.cameraVideoFinish(VIDEO_NORMAL);
    }

    /**
     * 录制到3秒后停止录制
     */
    private void minVideoTimeStop() {
        if (mCountDownTimer == null) {
            return;
        }
        mCountDownTimer.cancel();
        Log.e(TAG, "minVideoTimeStop ============= 3秒停止了");
        scale = 0;
        sweepAngle = 0;
        intervalTime = 0;
        minVideoTimeStop = false;
        mCameraVideoFinishCallback.cameraVideoFinish(VIDEO_ERROR);
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0) {
                minVideoTimeStop();
            }else if(msg.what == 1) {
                scale = 0;
                sweepAngle = 0;
                intervalTime = 0;
                minVideoTimeStop = false;
                mCameraVideoFinishCallback.cameraVideoFinish(VIDEO_WARN);
            }
        }
    };
}
