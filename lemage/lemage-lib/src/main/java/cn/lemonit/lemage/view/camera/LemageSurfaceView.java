package cn.lemonit.lemage.view.camera;

import android.content.Context;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @author zhaoguangyang
 */
public class LemageSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Context mContext;

    public LemageSurfaceView(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
