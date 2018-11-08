package cn.lemonit.lemage.view.preview_view;

import android.content.Context;
import android.widget.VideoView;

/**
 * 可以全屏的VideoView
 * @author zhaoguangyang
 */
public class ScreenVideoView extends VideoView {

    /**
     * 是否全屏
     */
    private boolean isFullScreen = false;

    public ScreenVideoView(Context context, boolean isFullScreen) {
        super(context);
        this.isFullScreen = isFullScreen;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if(isFullScreen) {
            int width = getDefaultSize(0, widthMeasureSpec);
            int height = getDefaultSize(0, heightMeasureSpec);
            setMeasuredDimension(width, height);
        }
    }
}
