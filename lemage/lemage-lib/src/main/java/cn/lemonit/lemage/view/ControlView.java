package cn.lemonit.lemage.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import cn.lemonit.lemage.util.ScreenUtil;

/**
 * VideoView的进度条，暂停按键，显示时间等信息的组合控件
 * @author zhaoguangyang
 */
public class ControlView extends RelativeLayout {

    final private String TAG = "ControlView";

    private Context mContext;
    private Paint mPaint;
    private VideoStartImageView mVideoStartImageView;
    private ProgressBar mProgressBar;
    private String timeText = "";
    // 是否显示状态
    private boolean isShow = true;

    public ControlView(Context context) {
        super(context);
        mContext = context;
        this.setBackgroundColor(Color.BLUE);  // 设置黑色背景
//        this.setAlpha(0.5f);
        // 添加左侧视频开始的按钮
        addStartVideoView();
        // 添加进度条
        addProgressBar();
        initPaint();
    }

    /**
     * 添加左侧视频开始的按钮
     */
    private void addStartVideoView() {
        VideoStartImageView mVideoStartImageView = new VideoStartImageView(mContext, ScreenUtil.dp2px(mContext, 20), ScreenUtil.dp2px(mContext, 25), Color.WHITE, Color.BLACK, false, false);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(20, 0, 0, 0);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        mVideoStartImageView.setLayoutParams(layoutParams);
        this.addView(mVideoStartImageView);
        this.mVideoStartImageView = mVideoStartImageView;
    }

    /**
     * 添加进度条
     */
    private void addProgressBar() {
        ProgressBar mProgressBar = new ProgressBar(mContext, null,android.R.attr.progressBarStyleHorizontal);
//        mProgressBar.setBackgroundColor(Color.YELLOW);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        layoutParams.setMargins(20 + 20 + ScreenUtil.dp2px(mContext, 20) * 2, 0, 20, ScreenUtil.dp2px(mContext, 5));
        mProgressBar.setLayoutParams(layoutParams);
        this.addView(mProgressBar);
        this.mProgressBar = mProgressBar;
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setStrokeWidth(2);
        mPaint.setAntiAlias(true);//抗锯齿
        mPaint.setColor(Color.WHITE);
        mPaint.setTextAlign(Paint.Align.LEFT);
        mPaint.setTextSize(20);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画时长
        drawAllTime(canvas);
    }

    private void drawAllTime(Canvas canvas) {
        canvas.drawText(timeText, 20 + 20 + ScreenUtil.dp2px(mContext, 20) * 2, ScreenUtil.dp2px(mContext, 20), mPaint);
    }

    /**
     * 留给外部接口用来刷新时长
     * @param timeText
     */
    public void setTimeText(String timeText) {
        this.timeText = timeText;
        invalidate();
    }

    public ProgressBar getProgressBar() {
        return mProgressBar;
    }

    public VideoStartImageView getVideoStartImageView() {
        return mVideoStartImageView;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }


    /**
     * 手势滑动进度条时，事件停止继续传递给viewpager
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }

    /**
     * 手指离开底部条时，事件停止，不在传递给viewpager
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
