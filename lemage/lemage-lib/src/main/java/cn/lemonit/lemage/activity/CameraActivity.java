package cn.lemonit.lemage.activity;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lemorage.file.Lemorage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.lemonit.lemage.R;
import cn.lemonit.lemage.activity_ui.CameraUi;
import cn.lemonit.lemage.base.MediaBaseActivity;
import cn.lemonit.lemage.interfaces.LemageCameraCallback;
import cn.lemonit.lemage.util.BitmapUtil;
import cn.lemonit.lemage.view.camera_view.BrightView;
import cn.lemonit.lemage.view.camera_view.CameraBackView;
import cn.lemonit.lemage.view.camera_view.CameraEvertButton;
import cn.lemonit.lemage.view.camera_view.CameraNOView;
import cn.lemonit.lemage.view.camera_view.CameraOKView;
import cn.lemonit.lemage.view.camera_view.CameraTakePhotoView;
import cn.lemonit.lemage.view.camera_view.CameraVideoProgressBar;

import static cn.lemonit.lemage.activity_ui.CameraUi.ACTION_VIDEO;

/**
 * @author zhaoguangyang
 * @date 2018/11/2
 * Describe:
 */
public class CameraActivity extends MediaBaseActivity implements View.OnClickListener, View.OnLongClickListener, CameraVideoProgressBar.CameraVideoFinishCallback {

    private final String TAG = "CameraActivity";

    private static LemageCameraCallback cameraCallback;

    public static void setCameraCallback(LemageCameraCallback callback) {
        cameraCallback = callback;
    }
    /**
     * 给调用者返回的文件路径(沙盒)
     */
    private List<String> listPath = new ArrayList<String>();
    /**
     * 图像控件
     */
    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    /**
     * 切换前后摄像头控件
     */
    private CameraEvertButton mCameraEvertButton;
    /**
     * 录制视频的类
     */
    private MediaRecorder mMediaRecorder;
    /**
     * 底部条（用来展示返回，拍照，录像及时长的）
     */
    private RelativeLayout bottomLayout;
    /**
     * 底部条圆圈，拍照和录像的按钮
     */
    private CameraTakePhotoView mCameraTakePhotoView;
    /**
     * 闪光灯
     */
    private BrightView mBrightView;
    /**
     * 底部条箭头，返回按钮
     */
    private CameraBackView mCameraBackView;
    /**
     * 底部条中，拍照完成后的取消（差号）按钮
     */
    private CameraNOView mCameraNOView;
    /**
     * 底部条中，拍照完成后的确定（对号）按钮
     */
    private CameraOKView mCameraOKView;
    /**
     * 底部条中，录像时的圆形进度条
     */
    private CameraVideoProgressBar mCameraVideoProgressBar;

    /**
     * 判断事件是拍照还是录像（因为长按事件是按下时执行，点击事件是抬起执行）
     * 0 photo   1 video
     */
    private int action;
    /**
     * 记录录像时开始和结束时间，如果短于5秒，提示时间过短失败
     */
    private long startVideoTime, stopVideoTime;
    /**
     * 录像最短时间，当开始录像和结束录像时间太短时，强制录像到3秒后停止
     */
    private final int minVideoTime = 3;
    /**
     * 拍摄视频的结束方式
     */
    private int videoFinishType;
    /**
     * 录像时最多录制的秒数，默认是3秒
     */
    private int videoTime = 0;
    /**
     * 拍摄视频路径
     */
//    private String videoPath;
    /**
     * 拍照得到的bitmap
     */
    private Bitmap bitmap;
    /**
     * 是否打开闪光灯
     */
    private boolean lightOpen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void getData() {
        super.getData();
        videoTime = intent.getIntExtra("videoTime", 0);
        // 开始录制视频和结束录制视频间隔不能太短，否则程序崩溃，而且实际上时间太短也没有意义，所以默认最短时间5秒
        videoTime = videoTime <=5 ? 5 : videoTime;
    }

    @Override
    protected void init() {
        super.init();
        mSurfaceView = CameraUi.getSurfaceView(this);
        mSurfaceHolder = CameraUi.getSurfaceHolder(this, mSurfaceView);
        mSurfaceHolder.addCallback(callback);
        // 底部条
        bottomLayout = CameraUi.getBottomLayout(this);
        bottomLayout.setId(R.id.camera_bottom_layout);
        // 切换摄像头按钮
        mCameraEvertButton = CameraUi.getCameraEvertButton(this);
        // 拍照中间圆圈
        mCameraTakePhotoView = CameraUi.getCameraTakePhotoView(this);
        // 底部条右侧返回按钮
        mCameraBackView = CameraUi.getmCameraBackView(this);
        // 拍照录像完成后的取消按钮
        mCameraNOView = CameraUi.getCameraNOView(this);
        // 确定按钮
        mCameraOKView = CameraUi.getCameraOKView(this);
        // 进度条
        mCameraVideoProgressBar = CameraUi.getCameraVideoProgressBar(this);
        // 闪光灯
        mBrightView = CameraUi.getBrightView(this, bottomLayout.getId());
    }

    @Override
    protected void addView(RelativeLayout rootLayout) {
        super.addView(rootLayout);
        // 没有摄像头就关闭
        if(!CameraUi.checkCamera(this)) {
            this.finish();
        }
        rootLayout.addView(mSurfaceView);
        bottomLayout.addView(mCameraEvertButton);
        bottomLayout.addView(mCameraTakePhotoView);
        bottomLayout.addView(mCameraBackView);
        bottomLayout.addView(mCameraNOView);
        bottomLayout.addView(mCameraOKView);
        bottomLayout.addView(mCameraVideoProgressBar);

        mCameraOKView.setVisibility(View.GONE);
        mCameraNOView.setVisibility(View.GONE);
        mCameraVideoProgressBar.setVisibility(View.GONE);

        rootLayout.addView(bottomLayout);
        rootLayout.addView(mBrightView);
    }


    private SurfaceHolder.Callback callback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mSurfaceHolder = holder;
            openCamera(CameraUi.currentCameraType);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            mSurfaceHolder = holder;
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // 释放全部资源
            releaseMedia();
        }
    };

    /**
     * 打开相机
     * @param type
     */
    private void openCamera(int type) {
        releaseCamera();
        if(mCamera == null) {
            if(type == CameraUi.FRONT) {
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            }else if(type == CameraUi.BACK) {
                try {
                    mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                }catch (Exception e) {
                    if(mCamera != null) {
                        mCamera.setPreviewCallback(null) ;
                        mCamera.stopPreview();
                        mCamera.release();
                        mCamera = null;
                    }
                }
            }
        }
        if(mCamera == null) {
            CameraActivity.this.finish();
            Toast.makeText(this, "相机发生错误", Toast.LENGTH_SHORT).show();
            return;
        }
//        CameraUi.setCameraDisplayOrientation(this, Camera.CameraInfo.CAMERA_FACING_BACK, mCamera);
        setCameraDisplayOrientation(CameraActivity.this, Camera.CameraInfo.CAMERA_FACING_BACK, mCamera);
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
    }

    @Override
    public void onClick(View v) {
        // 切换摄像头
        if(v == mCameraEvertButton) {
            changeCamera();
        }
        // 中间圆圈
        if(v == mCameraTakePhotoView) {
            tackPhoto();
        }
        // 返回
        if(v == mCameraBackView) {
            CameraActivity.this.finish();
        }
        // 取消
        if(v == mCameraNOView) {
            cancelAction();
        }
        // 确定
        if(v == mCameraOKView) {
            definiteAction();
        }
        // 闪光灯
        if(v == mBrightView) {
            openOrCloseLight();
        }
    }

    @Override
    public boolean onLongClick(View v) {
        // 长按中间圆圈，录像
        startVideoTime = System.currentTimeMillis();
        action = CameraUi.ACTION_VIDEO;
        mCameraVideoProgressBar.setVisibility(View.VISIBLE);
        mCameraTakePhotoView.setVisibility(View.GONE);
        // 开始计时进度条
        mCameraVideoProgressBar.setTime(videoTime);
        mCameraVideoProgressBar.startVideo();
        mCamera.unlock();
        mMediaRecorder = CameraUi.getMediaRecorder(mCamera);
        // 设置视频路径
        mMediaRecorder.setOutputFile(CameraUi.getVideoPath(this));
        // 开始录像
        CameraUi.startMediaRecorder(mMediaRecorder);
        return false;
    }

    /**
     * 录像结束回调
     * @param type
     */
    @Override
    public void cameraVideoFinish(int type) {
        videoFinish(type);
    }

    /**
     * 切换摄像头事件
     */
    private void changeCamera() {
        if(CameraUi.getNumberOfCameras() == 1) {
            return;
        }
        if(CameraUi.currentCameraType == CameraUi.BACK) {
            CameraUi.currentCameraType = CameraUi.FRONT;
        }else {
            CameraUi.currentCameraType = CameraUi.BACK;
        }
        openCamera(CameraUi.currentCameraType);
    }

    /**
     * 中间圆圈拍照事件(手指抬起时都会执行此方法，所以区分拍照还是视频)
     */
    private void tackPhoto() {
        if(action == CameraUi.ACTION_PHOTO) {
            if(mCamera != null) {
                mCamera.takePicture(null, null, new Camera.PictureCallback() {
                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {
                        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        if (bitmap == null) {
                            return;
                        }
                        mCameraTakePhotoView.setVisibility(View.GONE);
                        mCameraBackView.setVisibility(View.GONE);
                        mCameraEvertButton.setVisibility(View.GONE);
                        mCameraOKView.setVisibility(View.VISIBLE);
                        mCameraNOView.setVisibility(View.VISIBLE);
                    }
                });
            }
        }else if(action == CameraUi.ACTION_VIDEO) {
//            Toast.makeText(this, "录像", Toast.LENGTH_SHORT).show();
            // 此时非自然录像结束，属于手指抬起时的结束，先判断录像时间是否短于5秒
            if(videoFinishType == CameraVideoProgressBar.VIDEO_NORMAL) {
                stopVideoTime = System.currentTimeMillis();
                // 录像时间短于3秒，继续录制到3秒，但是删除该视频文件，提示时间太短，录像失败
                if((stopVideoTime - startVideoTime) < minVideoTime * 1000) {
                    mCameraVideoProgressBar.minVideoTimeStop = true;
                    Toast.makeText(CameraActivity.this, "录像时间小于3秒录像失败，请稍后重新录制", Toast.LENGTH_SHORT).show();
                    return;
                }
                mCameraVideoProgressBar.stopVideo();
            }
        }
    }

    /**
     * 取消按钮事件
     */
    private void cancelAction() {
        if(action == CameraUi.ACTION_PHOTO) {
            if(bitmap != null) {
                if(bitmap != null && !bitmap.isRecycled()){
                    bitmap.recycle();
                    bitmap = null;
                }
                System.gc();
            }
            mCameraTakePhotoView.setVisibility(View.VISIBLE);
            mCameraBackView.setVisibility(View.VISIBLE);
            mCameraEvertButton.setVisibility(View.VISIBLE);
            mCameraOKView.setVisibility(View.GONE);
            mCameraNOView.setVisibility(View.GONE);
            mCamera.startPreview();
        }else if(action == CameraUi.ACTION_VIDEO) {
            mCameraTakePhotoView.setVisibility(View.VISIBLE);
            mCameraBackView.setVisibility(View.VISIBLE);
            mCameraEvertButton.setVisibility(View.VISIBLE);
            mCameraOKView.setVisibility(View.GONE);
            mCameraNOView.setVisibility(View.GONE);
            mCamera.startPreview();
            // 删除文件
            File file = Lemorage.getWithFile(CameraUi.boxVideoPath, CameraActivity.this);
            if(file.exists()) {
                file.delete();
            }
        }
        // 恢复初始状态，默认是拍照,只有长按时才变成录像
        action = CameraUi.ACTION_PHOTO;
    }

    /**
     * 确定按钮事件
     */
    private void definiteAction() {
        if(action == CameraUi.ACTION_PHOTO) {
            if(bitmap != null) {
                backActivity(bitmap);
            }
        }else if(action == CameraUi.ACTION_VIDEO) {
            if(mCamera != null) {
                mCamera.release();
                mCamera = null;
            }
            listPath.clear();
            listPath.add(CameraUi.boxVideoPath);
            cameraCallback.cameraActionFinish(listPath);
            CameraActivity.this.finish();
        }
    }

    /**
     * 给调用者(Activity)传值 -- 路径
     * @param bitmap
     */
    private void backActivity(Bitmap bitmap) {
        // 把图片转成竖屏的
        Matrix matrix = new Matrix();
        matrix.setRotate(90);
        try {
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//            String path = CameraFileUtil.getInstance().getPhotoPath(bitmap);
            String path = BitmapUtil.bitmap2Url(this, bitmap, false);
            if(bitmap != null && !bitmap.isRecycled()){
                bitmap.recycle();
                bitmap = null;
            }
            System.gc();
            Log.e(TAG, "path ================ " + path);
            listPath.clear();
            listPath.add(path);
        }catch (Exception e) {
            e.printStackTrace();
        }
        cameraCallback.cameraActionFinish(listPath);
        CameraActivity.this.finish();
    }

    /**
     * 闪光灯
     */
    private void openOrCloseLight() {
        if(!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            Toast.makeText(this, "你的手机没有闪光灯!", Toast.LENGTH_LONG).show();
            return;
        }
        Camera.Parameters parameters = mCamera.getParameters();
        if(!lightOpen) {
            lightOpen = true;
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);//开启
            mCamera.setParameters(parameters);
        }else {
            lightOpen = false;
            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);//关闭
            mCamera.setParameters(parameters);
        }
    }

    /**
     * 录像结束
     */
    private void videoFinish(int type) {
        videoFinishType = type;
        CameraUi.stopMediaRecorder(mMediaRecorder, mCamera);
        if(videoFinishType != CameraVideoProgressBar.VIDEO_ERROR) {
            mCameraVideoProgressBar.setVisibility(View.GONE);
            mCameraBackView.setVisibility(View.GONE);
            mCameraEvertButton.setVisibility(View.GONE);
            mCameraNOView.setVisibility(View.VISIBLE);
            mCameraOKView.setVisibility(View.VISIBLE);
        }else {
            videoFinishType = CameraVideoProgressBar.VIDEO_NORMAL;
            action = CameraUi.ACTION_PHOTO;
            mCameraVideoProgressBar.setVisibility(View.GONE);
            mCameraTakePhotoView.setVisibility(View.VISIBLE);
            mCamera.startPreview();
            // 删除这个时间小于3秒的视频
            File file = Lemorage.getWithFile(CameraUi.boxVideoPath, CameraActivity.this);
            if(file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * 显示方向设置
     * @param activity
     * @param cameraId
     * @param camera
     */
    private void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int displayDegree;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            displayDegree = (info.orientation + degrees) % 360;
            displayDegree = (360 - displayDegree) % 360;  // compensate the mirror
        } else {
            displayDegree = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(displayDegree);
    }

    /**
     * 释放相机资源
     */
    public synchronized void releaseCamera() {
        if (mCamera != null) {
            try {
                mCamera.setPreviewCallback(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mCamera.stopPreview();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                mCamera.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mCamera = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放全部资源
        releaseMedia();
    }

    /**
     * 销毁所有多媒体相关
     */
    private void releaseMedia() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();//释放相机资源
            mCamera = null;
        }
        mSurfaceView = null;
        mSurfaceHolder = null;
        if (mMediaRecorder != null) {
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

}
