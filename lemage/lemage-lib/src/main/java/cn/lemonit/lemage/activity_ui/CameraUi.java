package cn.lemonit.lemage.activity_ui;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lemorage.file.LemixFileCommon;
import com.lemorage.file.Lemorage;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import cn.lemonit.lemage.activity.CameraActivity;
import cn.lemonit.lemage.util.ScreenUtil;
import cn.lemonit.lemage.view.camera_view.BrightView;
import cn.lemonit.lemage.view.camera_view.CameraBackView;
import cn.lemonit.lemage.view.camera_view.CameraEvertButton;
import cn.lemonit.lemage.view.camera_view.CameraNOView;
import cn.lemonit.lemage.view.camera_view.CameraOKView;
import cn.lemonit.lemage.view.camera_view.CameraTakePhotoView;
import cn.lemonit.lemage.view.camera_view.CameraVideoProgressBar;

/**
 * @author zhaoguangyang
 * @date 2018/11/2
 * Describe:
 */
public class CameraUi {

    private String TAG = "CameraUi";

    public static final int FRONT = 1;//前置摄像头标记
    public static final int BACK = 2;//后置摄像头标记
    public static int currentCameraType = BACK;//当前打开的摄像头标记(默认是后置摄像头)
    /**
     * 判断事件是拍照还是录像（因为长按事件是按下时执行，点击事件是抬起执行）
     * 0 photo   1 video
     */
    public final static int ACTION_PHOTO = 0;
    public final static int ACTION_VIDEO = 1;
    /**
     * 视频沙盒路径
     */
    public static String boxVideoPath = null;

    /**
     * 图像控件
     * @param context
     * @return
     */
    public static SurfaceView getSurfaceView(Context context) {
        SurfaceView mSurfaceView = new SurfaceView(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mSurfaceView.setLayoutParams(params);
        return mSurfaceView;
    }

    /**
     * 图像控件holder
     * @param context
     * @param mSurfaceView
     * @return
     */
    public static SurfaceHolder getSurfaceHolder(Context context, SurfaceView mSurfaceView) {
        SurfaceHolder mSurfaceHolder = mSurfaceView.getHolder();
        return mSurfaceHolder;
    }

    /**
     * 底部条
     * @param context
     * @return
     */
    public static RelativeLayout getBottomLayout(Context context) {
        RelativeLayout bottomLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ScreenUtil.getScreenHeight(context) / 6);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        // 距离底部距离
        layoutParams.setMargins(0, 0, 0, ScreenUtil.getScreenHeight(context) / 12);
        bottomLayout.setLayoutParams(layoutParams);
        return bottomLayout;
    }

    /**
     * 拍照中间圆圈
     * @param context
     * @return
     */
    public static CameraTakePhotoView getCameraTakePhotoView(Context context) {
        CameraTakePhotoView mCameraTakePhotoView = new CameraTakePhotoView(context);
        RelativeLayout.LayoutParams layoutParamsVideo = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParamsVideo.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        mCameraTakePhotoView.setLayoutParams(layoutParamsVideo);
//        mCameraTakePhotoView.setOnClickListener(tackPictureShortClickListener);  // 短点击事件
        mCameraTakePhotoView.setOnClickListener((View.OnClickListener) context);
        mCameraTakePhotoView.setLongClickable(true);
//        mCameraTakePhotoView.setOnLongClickListener(tackVideoLongClickListener);  // 长按点击事件
        mCameraTakePhotoView.setOnLongClickListener((View.OnLongClickListener) context);
        return mCameraTakePhotoView;
    }

    /**
     * 左下角切换前后摄像头按钮
     */
    public static CameraEvertButton getCameraEvertButton(Context context) {
        CameraEvertButton mCameraEvertButton = new CameraEvertButton(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        params.setMargins(60, 0, 0, 0);
        mCameraEvertButton.setLayoutParams(params);
        mCameraEvertButton.setOnClickListener((View.OnClickListener) context);
        return mCameraEvertButton;
    }

    /**
     * 底部条右侧返回按钮
     * @param context
     * @return
     */
    public static CameraBackView getmCameraBackView(Context context) {
        CameraBackView mCameraBackView = new CameraBackView(context);
        RelativeLayout.LayoutParams layoutParamsBack = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsBack.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        layoutParamsBack.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        layoutParamsBack.setMargins(0, 0, 70, 0);
        mCameraBackView.setLayoutParams(layoutParamsBack);
        mCameraBackView.setOnClickListener((View.OnClickListener) context);
        return mCameraBackView;
    }

    /**
     * 拍照完成后取消按钮
     * @param context
     * @return
     */
    public static CameraNOView getCameraNOView(Context context) {
        CameraNOView mCameraNOView = new CameraNOView(context);
        RelativeLayout.LayoutParams layoutParamsNO = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsNO.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        layoutParamsNO.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        layoutParamsNO.setMargins(60, 0, 0, 0);
        mCameraNOView.setLayoutParams(layoutParamsNO);
        mCameraNOView.setOnClickListener((View.OnClickListener) context);
        return mCameraNOView;
    }

    /**
     * 拍照完成后确认按钮
     * @param context
     * @return
     */
    public static CameraOKView getCameraOKView(Context context) {
        CameraOKView mCameraOKView = new CameraOKView(context);
        RelativeLayout.LayoutParams layoutParamsOK = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsOK.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        layoutParamsOK.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        layoutParamsOK.setMargins(0, 0, 60, 0);
        mCameraOKView.setLayoutParams(layoutParamsOK);
        mCameraOKView.setOnClickListener((View.OnClickListener) context);
        return mCameraOKView;
    }

    /**
     * 录像时的圆形进度条
     * @param context
     * @return
     */
    public static CameraVideoProgressBar getCameraVideoProgressBar(Context context) {
        CameraVideoProgressBar mCameraVideoProgressBar = new CameraVideoProgressBar(context);
        RelativeLayout.LayoutParams layoutParamsProgressBar = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParamsProgressBar.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        mCameraVideoProgressBar.setLayoutParams(layoutParamsProgressBar);
        mCameraVideoProgressBar.setCameraVideoFinishCallback((CameraVideoProgressBar.CameraVideoFinishCallback) context);
        return mCameraVideoProgressBar;
    }

    /**
     * 闪光灯
     * @param context
     * @return
     */
    public static BrightView getBrightView(Context context, int id) {
        BrightView mBrightView = new BrightView(context);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ABOVE, id);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        layoutParams.setMargins(0, 0, 0, 15);
        mBrightView.setLayoutParams(layoutParams);
        mBrightView.setOnClickListener((View.OnClickListener) context);
        return mBrightView;
    }

    /**
     * 视频采集
     * @return
     */
    public static MediaRecorder getMediaRecorder(Camera mCamera) {
        MediaRecorder mMediaRecorder = new MediaRecorder();
        mMediaRecorder.reset();
        mMediaRecorder.setCamera(mCamera);
        // 选择角度(视频结果)
        mMediaRecorder.setOrientationHint(90);
        // 设置采集图像
        mMediaRecorder.setVideoSource(Camera.CameraInfo.CAMERA_FACING_BACK);
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 设置视频的输出格式
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        // 设置视频编码格式
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        // 设置视频质量
        CamcorderProfile profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
        // 设置高质量录制， 改变码率
        mMediaRecorder.setVideoEncodingBitRate(5 * 1024 * 1024);
        // 设置分辨率
        mMediaRecorder.setVideoSize(profile.videoFrameWidth, profile.videoFrameHeight);
        // 设置帧率
        mMediaRecorder.setVideoFrameRate(60);
        return mMediaRecorder;
    }

    /**
     * 设置拍摄视频路径
     * @return
     */
    public static String getVideoPath(Context context) {
        String path = null;
        String uuid = UUID.randomUUID().toString();
        path = LemixFileCommon.getBaseUrl(context) + File.separator + Lemorage.getSendBoxShort() + File.separator + uuid + ".mp4";
        boxVideoPath = Lemorage.getSendBoxHead() + Lemorage.getSendBoxShort() + uuid + ".mp4";
        return path;
    }

    /**
     * 开始录像
     */
    public static void startMediaRecorder(MediaRecorder mMediaRecorder) {
        if(mMediaRecorder == null) {
            return;
        }
        try {
            // 准备录制
            mMediaRecorder.prepare();
            mMediaRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止录像
     */
    public static void stopMediaRecorder(MediaRecorder mMediaRecorder, Camera mCamera) {
        if (mMediaRecorder != null) {
            mMediaRecorder.setOnErrorListener(null);
            mMediaRecorder.setPreviewDisplay(null);
            // 停止录制
            try {
                mMediaRecorder.stop();
                mMediaRecorder.reset();
                // 释放资源
                mMediaRecorder.release();
                mCamera.lock();
                mCamera.stopPreview();
                mMediaRecorder = null;
            }catch (Exception e) {
                if(mCamera != null) {
                    mCamera.stopPreview();
                    mCamera.release();
                    mCamera = null;
                }
            }
        }
    }

    /**
     * 检查是否有摄像头
     * @param context
     * @return
     */
    public static boolean checkCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    /**
     * 获取摄像头个数
     * @return
     */
    public static int getNumberOfCameras() {
        return Camera.getNumberOfCameras();
    }

//    /**
//     * 切换摄像头
//     */
//    public static void changeCamera(Camera mCamera, int type, Activity activity, SurfaceHolder mSurfaceHolder) {
//        if(currentCameraType == BACK) {
//            currentCameraType = FRONT;
//        }else {
//            currentCameraType = BACK;
//        }
//        openCamera(mCamera, currentCameraType, activity, mSurfaceHolder);
//    }

//    /**
//     * 获取相机
//     * @param type
//     * @param activity
//     * @return
//     */
//    public static Camera getCamera(int type, Activity activity) {
//        Camera mCamera = null;
//        try {
//            if(type == FRONT) {
//                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
//            }else if(type == BACK) {
//                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
//            }
//        }catch (Exception e) {
//            if(mCamera != null) {
//                mCamera.setPreviewCallback(null) ;
//                mCamera.stopPreview();
//                mCamera.release();
//                mCamera = null;
//            }
//        }
//        if(mCamera == null) {
//            activity.finish();
//            Toast.makeText(activity, "相机发生错误", Toast.LENGTH_SHORT).show();
//            return null;
//        }
//        return mCamera;
//    }

//    /**
//     * 打开相机
//     */
//    public static void openCamera(Camera mCamera, Activity activity, SurfaceHolder mSurfaceHolder) {
//        releaseCamera(mCamera);
//        setCameraDisplayOrientation(activity, Camera.CameraInfo.CAMERA_FACING_BACK, mCamera);
//        try {
//            mCamera.setPreviewDisplay(mSurfaceHolder);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mCamera.startPreview();
//    }

//    /**
//     * 是否全部多媒体，包括相机，录像等对象（onDestroy()时调用）
//     */
//    public static void releaseMedia(Camera mCamera, SurfaceView mSurfaceView, SurfaceHolder mSurfaceHolder, MediaRecorder mMediaRecorder) {
//        if (mCamera != null) {
//            mCamera.stopPreview();
//            mCamera.release();//释放相机资源
//            mCamera = null;
//        }
//        mSurfaceView = null;
//        mSurfaceHolder = null;
//        if (mMediaRecorder != null) {
//            mMediaRecorder.release();
//            mMediaRecorder = null;
//        }
//    }

//    /**
//     * 释放相机资源
//     */
//    public static synchronized void releaseCamera(Camera mCamera) {
//        if (mCamera != null) {
//            try {
//                mCamera.setPreviewCallback(null);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            try {
//                mCamera.stopPreview();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            try {
//                mCamera.release();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            mCamera = null;
//        }
//    }

//    /**
//     * 设置显示方向
//     */
//    public static void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
//        Camera.CameraInfo info = new Camera.CameraInfo();
//        Camera.getCameraInfo(cameraId, info);
//        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
//        int degrees = 0;
//        switch (rotation) {
//            case Surface.ROTATION_0:
//                degrees = 0;
//                break;
//            case Surface.ROTATION_90:
//                degrees = 90;
//                break;
//            case Surface.ROTATION_180:
//                degrees = 180;
//                break;
//            case Surface.ROTATION_270:
//                degrees = 270;
//                break;
//        }
//        int displayDegree;
//        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//            displayDegree = (info.orientation + degrees) % 360;
//            displayDegree = (360 - displayDegree) % 360;  // compensate the mirror
//        } else {
//            displayDegree = (info.orientation - degrees + 360) % 360;
//        }
//        camera.setDisplayOrientation(displayDegree);
//    }
}
