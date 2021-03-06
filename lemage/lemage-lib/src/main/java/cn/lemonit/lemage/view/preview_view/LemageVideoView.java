package cn.lemonit.lemage.view.preview_view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import java.util.Timer;
import java.util.TimerTask;

import cn.lemonit.lemage.util.ScreenUtil;

/**
 * 完整的视频播放器
 * @author zhaoguangyang
 */
public class LemageVideoView extends RelativeLayout {

    private String TAG = "LemageVideoView";

    private Context mContext;
    // 视频路径
    private String mPath;

    private ImageView mImageView;
    private ScreenVideoView mVideoView;
    private VideoStartImageView mVideoStartImageView;
    private ControlView mControlView;
    // 视频URI
    private Uri mUri;
    // 视频时长(秒)
    private long duration;
    private int durationTime;
    private String durationShow;  // 00:00格式的时间(总时长)

    // 进度条进度
    private int progress = 0;
    // 视频已经观看的秒数
    private int finishTime = -1;

    private Timer mTimer;
    private TimerTask mTimerTask;
    private Thread mThread;
    // 静态图片
    private Bitmap mBitmap;

    // 视频暂停
    private boolean isPause;
    // 视频没开始播放过
    private boolean isStart;

    public LemageVideoView(Context context, String path) {
        super(context);
        mContext = context;
        mPath = path;
        initView();
    }

    private void initView() {
        // 添加视频控件
        addVideoView();
        // 添加第一帧显示
        addImgView();
        // 添加第一帧显示静态图片的开始按钮
        addStartVideoView();
        // 添加进度条等控件
        addControlView();
        // 初始化视频相关参数
        initVideoInfo();
        // 添加各种事件
        setListener();
    }

    /**
     * 根据path得到视频相关参数
     */
    private void initVideoInfo() {
        if(TextUtils.isEmpty(mPath)) {
            return;
        }
        mUri = Uri.parse(mPath);

        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(mPath);
        String durationStr = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        duration = Long.parseLong(durationStr);
        durationTime = (int) Math.floor((duration / 1000));   // 秒

        mBitmap = media.getFrameAtTime();
        mImageView.setImageBitmap(mBitmap);  // 显示静态图片

        durationShow = getTime(durationTime);
        mControlView.setTimeText("00 : 00 / " + durationShow);  // 初始化显示时间
    }

    /**
     * 添加视频控件
     */
    private void addVideoView() {
        ScreenVideoView mVideoView = new ScreenVideoView(mContext, false);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//        layoutParams.setMargins(0, 0, 0, ScreenUtil.dp2px(mContext, 56));
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        mVideoView.setLayoutParams(layoutParams);
        this.addView(mVideoView);
        this.mVideoView = mVideoView;
    }

    /**
     * 添加第一帧显示的静态图片
     */
    private void addImgView() {
        ImageView mImageView = new ImageView(mContext);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mImageView.setLayoutParams(layoutParams);
        this.addView(mImageView);
        this.mImageView = mImageView;
    }

    /**
     * 添加第一帧显示静态图片的开始按钮
     */
    private void addStartVideoView() {
        VideoStartImageView mVideoStartImageView = new VideoStartImageView(mContext, ScreenUtil.dp2px(mContext, 40), ScreenUtil.dp2px(mContext, 50), Color.WHITE, Color.BLUE, true, false);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        mVideoStartImageView.setLayoutParams(layoutParams);
        this.addView(mVideoStartImageView);
        this.mVideoStartImageView = mVideoStartImageView;
    }

    /**
     * 添加进度条等控件
     */
    private void addControlView() {
        ControlView mControlView = new ControlView(mContext);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, ScreenUtil.dp2px(mContext, 56));
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mControlView.setLayoutParams(layoutParams);
        this.addView(mControlView);
        this.mControlView = mControlView;
    }

    /**
     * 给子控件添加事件
     */
    private void setListener() {
        // 添加播放结束监听
        mVideoView.setOnCompletionListener(videoCompletionListener);
        // 开始按钮（大屏幕）事件
        mVideoStartImageView.setOnClickListener(bigStartVideoListener);
        // 底部条开始按钮事件
        mControlView.getVideoStartImageView().setOnClickListener(bottomStartVideoListener);
        // 进度条改变
        mControlView.getProgressBar().setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        // 当视频还没开始播放时，禁止拖动进度条
        mControlView.getProgressBar().setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(isStart) {
                    return false;
                }
                return true;
            }
        });
    }

    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        /**
         * 进度改变
         * @param seekBar
         * @param changePprogress  播放百分比
         * @param fromUser  是否是用户行为
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int changePprogress, boolean fromUser) {
//            Log.e(TAG, "onProgressChanged    progress ============= " + changePprogress);
//            if(fromUser) {
//                progress = changePprogress;
//            }
            Log.e(TAG, "onProgressChanged");
        }

        /**
         * 开始拖动
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mVideoView.pause();
            isPause = true;
        }

        /**
         * 停止拖动
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // 此方法为异步，参数单位毫秒
            mVideoView.seekTo( durationTime * seekBar.getProgress() / 100 * 1000);  // 跳到多少毫秒处
            mVideoView.start();
            progress = seekBar.getProgress();
            finishTime = durationTime * seekBar.getProgress() / 100;
            isPause = false;
            mControlView.getVideoStartImageView().setPause(true);
        }
    };


    /**
     * 大屏幕开始按钮
     */
    private OnClickListener bigStartVideoListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            startVideo();
        }
    };


    /**
     * 底部条开始按钮
     */
    private OnClickListener bottomStartVideoListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            // 如果之前已经按了开始按钮（不管现在是播放状态还是暂停状态）
            if(isStart) {
                if(mVideoView.isPlaying()) {
                    mVideoView.pause();
                    isPause = true;
                    mControlView.getVideoStartImageView().setPause(false);
                }else {
                    mVideoView.start();
                    isPause = false;
                    mControlView.getVideoStartImageView().setPause(true);
                }
            }
            // 如果之前没有按开始按钮，直接开始
            else {
                startVideo();
            }
        }
    };


    /**
     * 更新时间
     */
    Handler timeHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1) {
                finishTime ++;
                if(finishTime < durationTime + 1) {
//                    String time = finishTime < 10 ? "0" + finishTime : String.valueOf(finishTime);
                    mControlView.setTimeText(getTime(finishTime) + " / " + durationShow);
                }
            }
        }
    };


    /**
     * 更新进度
     */
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 0) {
                if(progress < 101) {
                    mControlView.getProgressBar().setProgress(progress ++);
                }
            }
        }
    };


    /**
     * 播放结束监听
     */
    private MediaPlayer.OnCompletionListener videoCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            mVideoStartImageView.setVisibility(View.VISIBLE);
            mImageView.setVisibility(View.VISIBLE);
            mControlView.getVideoStartImageView().setPause(false);
            isPause = true;
            isStart = false;
            if(mTimer != null) {
                mTimer.cancel();
                mTimer.purge();
                mTimer = null;
            }
            if(mTimerTask != null) {
                mTimerTask.cancel();
                mTimerTask = null;
            }
            if(mThread != null) {
                mThread.interrupt();
                mThread = null;
            }
            progress = 0;
            finishTime = -1;
            // 播放结束，进度条回到0的位置
            mControlView.getProgressBar().setProgress(0);
            // 播放结束，时间回到初始
            mControlView.setTimeText("00 : 00 / " + durationShow);
        }
    };


    // 返回播放视频的控件
    public ScreenVideoView getVideoView() {
        return mVideoView;
    }

    /**
     * 返回包含进度条，开始按钮，显示时长等信息的组合控件
     * @return
     */
    public ControlView getControlView() {
        return mControlView;
    }

    /**
     * 返回静态图片开始按钮
     * @return
     */
    public VideoStartImageView getVideoStartImageView() {
        return mVideoStartImageView;
    }

    /**
     * 返回静态图片
     * @return
     */
    public ImageView getImageView() {
        return mImageView;
    }


    /**
     * 根据总时长（秒）显示时间00:00
     * @param second
     * @return
     */
    private String getTime(int second) {
        String show = null;
        int minute = second / 60;
        int overSecond = second % 60;
        String minuteStr = minute < 10 ? "0" + minute : String.valueOf(minute);
        String overSecondStr = overSecond < 10 ? "0" + overSecond : String.valueOf(overSecond);
        show = minuteStr + " : " + overSecondStr;
        return show;
    }

    /**
     * 开始播放视频
     */
    private void startVideo() {
        mVideoStartImageView.setVisibility(View.GONE);
        mImageView.setVisibility(View.GONE);
        mVideoView.setVideoURI(mUri);
        mVideoView.requestFocus();
        mVideoView.start();
        isStart = true;
        isPause = false;
        mControlView.getVideoStartImageView().setPause(true);
        if(mTimer == null) {
            mTimer = new Timer();
        }
        if(mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    if(!isPause) {
                        timeHandler.sendEmptyMessage(1);
                    }
                }
            };
        }
        mTimer.schedule(mTimerTask, 0,1000);

        if(mThread == null) {
            mThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for(int i = 0; i < 101; i ++) {
                        if(isPause) {
                            while (true) {
                                if(!isPause) {
                                    break;
                                }
                            }
                        }
                        try {
                            Thread.sleep(duration / 100);
                            mHandler.sendEmptyMessage(0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
        mThread.start();
    }
}
