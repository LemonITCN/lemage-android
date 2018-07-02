package cn.lemonit.lemage.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import cn.lemonit.lemage.util.ScreenUtil;

/**
 * 完整的视频播放器
 */
public class LemageVideoView extends RelativeLayout {

    private Context mContext;

    private ImageView mImageView;
    private ScreenVideoView mVideoView;
    private VideoStartImageView mVideoStartImageView;
    private ControlView mControlView;

    public LemageVideoView(Context context) {
        super(context);
        mContext = context;
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
    }

    /**
     * 添加视频控件
     */
    private void addVideoView() {
        ScreenVideoView mVideoView = new ScreenVideoView(mContext, false);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
//        layoutParams.setMargins(0, 0, 0, ScreenUtil.dp2px(mContext, 56));
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        mVideoView.setLayoutParams(layoutParams);
        this.addView(mVideoView);
        this.mVideoView = mVideoView;
    }

    /**
     * 添加第一帧显示的静态图片
     */
    private void addImgView() {
        ImageView mImageView = new ImageView(mContext);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mImageView.setLayoutParams(layoutParams);
        this.addView(mImageView);
        this.mImageView = mImageView;
    }

    /**
     * 添加第一帧显示静态图片的开始按钮
     */
    private void addStartVideoView() {
        VideoStartImageView mVideoStartImageView = new VideoStartImageView(mContext, ScreenUtil.dp2px(mContext, 40), ScreenUtil.dp2px(mContext, 50), Color.WHITE, Color.WHITE, true, false);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
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
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, ScreenUtil.dp2px(mContext, 56));
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mControlView.setLayoutParams(layoutParams);
        this.addView(mControlView);
        this.mControlView = mControlView;
    }

    // 返回显示静态图片的控件
    public ImageView getImageView() {
        return mImageView;
    }

    // 返回播放视频的控件
    public ScreenVideoView getVideoView() {
        return mVideoView;
    }

    /**
     * 返回静态图片（第一帧的开始按钮）
     * @return
     */
    public VideoStartImageView getBigVideoStartImageView() {
        return mVideoStartImageView;
    }

    /**
     * 返回包含进度条，开始按钮，显示时长等信息的组合控件
     * @return
     */
    public ControlView getControlView() {
        return mControlView;
    }

    /**
     * 返回组合控件中的开始按钮
     * @return
     */
    public VideoStartImageView getSmallVideoStartImageView() {
        return getControlView().getVideoStartImageView();
    }

    /**
     * 返回组合控件中的进度条
     * @return
     */
    public ProgressBar getProgressBar() {
        return getControlView().getProgressBar();
    }
}
