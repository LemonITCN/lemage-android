package cn.lemonit.lemage.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import cn.lemonit.lemage.util.ScreenUtil;

/**
 * 完整的视频播放器
 */
public class LmageVideoView extends RelativeLayout {

    private Context mContext;

    private ImageView mImageView;

    public LmageVideoView(Context context) {
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

    private void addVideoView() {
        VideoView mVideoView = new VideoView(mContext);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mVideoView.setLayoutParams(layoutParams);
        this.addView(mVideoView);
    }

    private void addImgView() {
        ImageView mImageView = new ImageView(mContext);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mImageView.setLayoutParams(layoutParams);
        this.addView(mImageView);
        this.mImageView = mImageView;
    }

    private void addStartVideoView() {
        VideoStartImageView mVideoStartImageView = new VideoStartImageView(mContext, ScreenUtil.dp2px(mContext, 40), ScreenUtil.dp2px(mContext, 50), Color.WHITE, Color.WHITE, true, false);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        mVideoStartImageView.setLayoutParams(layoutParams);
        this.addView(mVideoStartImageView);
    }

    private void addControlView() {
        ControlView mControlView = new ControlView(mContext);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, ScreenUtil.dp2px(mContext, 56));
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mControlView.setLayoutParams(layoutParams);
        this.addView(mControlView);
    }

    public ImageView getmImageView() {
        return mImageView;
    }
}
