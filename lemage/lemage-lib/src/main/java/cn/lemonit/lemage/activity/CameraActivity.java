package cn.lemonit.lemage.activity;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.lemonit.lemage.R;
import cn.lemonit.lemage.interfaces.LemageCameraCallback;
import cn.lemonit.lemage.util.CameraFileUtil;
import cn.lemonit.lemage.util.ScreenUtil;
import cn.lemonit.lemage.view.camera.BrightView;
import cn.lemonit.lemage.view.camera.CameraBackView;
import cn.lemonit.lemage.view.camera.CameraEvertButton;
import cn.lemonit.lemage.view.camera.CameraNOView;
import cn.lemonit.lemage.view.camera.CameraOKView;
import cn.lemonit.lemage.view.camera.CameraVideoProgressBar;
import cn.lemonit.lemage.view.camera.CameraTakePhotoView;

/**
 * 拍照或者录像界面
 * @author zhaoguangyang
 */
public class CameraActivity extends AppCompatActivity {

    private static LemageCameraCallback cameraCallback;

    public static void setCameraCallback(LemageCameraCallback callback) {
        cameraCallback = callback;
    }

    private final String TAG = "CameraActivity";
    /**
     * 根视图布局
     */
    private RelativeLayout rootLayout;

    private SurfaceView mSurfaceView;
    private SurfaceHolder mSurfaceHolder;
    /**
     * 切换前后摄像头
     */
    private CameraEvertButton mEvertCameraButton;
    private static final int FRONT = 1;//前置摄像头标记
    private static final int BACK = 2;//后置摄像头标记
    private int currentCameraType = BACK;//当前打开的摄像头标记(默认是后置摄像头)

    private Camera mCamera;
    /**
     * 闪光灯
     */
    private BrightView mBrightView;
    /**
     * 底部条（用来展示返回，拍照，录像及时长的）
     */
    private RelativeLayout bottomLayout;
    /**
     * 底部条圆圈，拍照和录像的按钮
     */
    private CameraTakePhotoView mCameraTakePhotoView;
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
     * 拍照得到的bitmap
     */
    private Bitmap bitmap;
    /**
     * 判断事件是拍照还是录像（因为长按事件是按下时执行，点击事件是抬起执行）
     * 0 photo   1 video
     */
    private int action;
    /**
     * 录制视频的类
     */
    private MediaRecorder mMediaRecorder;

    private String videoPath;
    /**
     * 给调用者返回的文件路径
     */
    private List<String> listPath = new ArrayList<String>();
    /**
     * 录像时最多录制的秒数，默认是3秒
     */
    private int videoTime = 0;
    /**
     * 录像最短时间，当开始录像和结束录像时间太短时，强制录像到3秒后停止
     */
    private final int minVideoTime = 3;
    /**
     * // 有3种录像结束，一种是录制时间太短，强制录制到3秒后结束  2    一种是录像时间结束，自然完成， 1      一种是手指中间离开，自动完成  0
     */
    private int videoFinishType;
    /**
     * 记录录像时开始和结束时间，如果短于5秒，提示时间过短失败
     */
    private long startVideoTime, stopVideoTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.support.v7.appcompat.R.style.Theme_AppCompat_DayNight_NoActionBar);
        getData();
        initView();
        addView();
        setContentView(rootLayout);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
            Log.d(TAG, "surfaceDestroyed release mRecorder");
        }
    }

    private void getData() {
        videoTime = getIntent().getIntExtra("videoTime", 0);
        // 开始录制视频和结束录制视频间隔不能太短，否则程序崩溃，而且实际上时间太短也没有意义，所以默认最短时间5秒
        videoTime = videoTime <=5 ? 5 : videoTime;
    }

    private void initView() {
        getRootLayout();
        getSurfaceView();
        getEvertCameraButton();
        getBottomLayout();
        getBrightView();
    }

    /**
     * 添加根布局
     */
    private void getRootLayout() {
        rootLayout = new RelativeLayout(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        rootLayout.setLayoutParams(params);
    }

    /**
     * 添加视频显示
     */
    private void getSurfaceView() {
        mSurfaceView = new SurfaceView(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mSurfaceView.setLayoutParams(params);

        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(mSurfaceCallback);
    }

    /**
     * 添加切换前后摄像头按钮
     */
    private void getEvertCameraButton() {
        mEvertCameraButton = new CameraEvertButton(this);
        mEvertCameraButton.setOnClickListener(evertCameraClickListener);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        params.setMargins(60, 0, 0, 0);
        mEvertCameraButton.setLayoutParams(params);
    }

    /**
     * 添加底部条
     */
    private void getBottomLayout() {
        bottomLayout = new RelativeLayout(this);
        bottomLayout.setId(R.id.camera_bottom_layout);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ScreenUtil.getScreenHeight(this) / 6);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        // 距离底部距离
        layoutParams.setMargins(0, 0, 0, ScreenUtil.getScreenHeight(this) / 12);
        bottomLayout.setLayoutParams(layoutParams);
        // 添加中间拍照圆圈
        mCameraTakePhotoView = new CameraTakePhotoView(this);
        RelativeLayout.LayoutParams layoutParamsVideo = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParamsVideo.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        mCameraTakePhotoView.setLayoutParams(layoutParamsVideo);
        mCameraTakePhotoView.setOnClickListener(tackPictureShortClickListener);  // 短点击事件
        mCameraTakePhotoView.setLongClickable(true);
        mCameraTakePhotoView.setOnLongClickListener(tackVideoLongClickListener);  // 长按点击事件
        // 添加返回按钮
        mCameraBackView = new CameraBackView(this);
        RelativeLayout.LayoutParams layoutParamsBack = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsBack.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        layoutParamsBack.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        layoutParamsBack.setMargins(0, 0, 70, 0);
        mCameraBackView.setLayoutParams(layoutParamsBack);
        mCameraBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CameraActivity.this.finish();
            }
        });
        // 拍照完成后的差号
        mCameraNOView = new CameraNOView(this);
        RelativeLayout.LayoutParams layoutParamsNO = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsNO.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        layoutParamsNO.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        layoutParamsNO.setMargins(60, 0, 0, 0);
        mCameraNOView.setLayoutParams(layoutParamsNO);
        mCameraNOView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 拍照后的取消事件
                if(action == 0) {
                    if(bitmap != null) {
                        if(bitmap != null && !bitmap.isRecycled()){
                            bitmap.recycle();
                            bitmap = null;
                        }
                        System.gc();
                    }
                    mCameraTakePhotoView.setVisibility(View.VISIBLE);
                    mCameraBackView.setVisibility(View.VISIBLE);
                    mEvertCameraButton.setVisibility(View.VISIBLE);
                    mCameraOKView.setVisibility(View.GONE);
                    mCameraNOView.setVisibility(View.GONE);
                    mCamera.startPreview();
                }
                // 录像后的取消事件
                if(action == 1) {
                    mCameraTakePhotoView.setVisibility(View.VISIBLE);
                    mCameraBackView.setVisibility(View.VISIBLE);
                    mEvertCameraButton.setVisibility(View.VISIBLE);
                    mCameraOKView.setVisibility(View.GONE);
                    mCameraNOView.setVisibility(View.GONE);
                    mCamera.startPreview();
                }
                // 恢复初始状态，默认是拍照,只有长按时才变成录像
                action = 0;
            }
        });

        // 拍照完成后的对号
        mCameraOKView = new CameraOKView(this);
        RelativeLayout.LayoutParams layoutParamsOK = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParamsOK.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        layoutParamsOK.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        layoutParamsOK.setMargins(0, 0, 60, 0);
        mCameraOKView.setLayoutParams(layoutParamsOK);
        mCameraOKView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 拍照后的确定事件
                if(action == 0) {
                    if(bitmap != null) {
                        backActivity(bitmap);
                    }else {
                        Log.e(TAG, "bitmap == null");
                    }
                }
                // 录像后的确定事件
                if(action == 1) {
                    if(mCamera != null) {
                        mCamera.release();
                        mCamera = null;
                    }
                    listPath.clear();
                    listPath.add(videoPath);
                    cameraCallback.cameraActionFinish(listPath);
                    CameraActivity.this.finish();
                }
            }
        });

        // 录像时的圆形进度条
        mCameraVideoProgressBar = new CameraVideoProgressBar(this);
        RelativeLayout.LayoutParams layoutParamsProgressBar = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        layoutParamsProgressBar.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        mCameraVideoProgressBar.setLayoutParams(layoutParamsProgressBar);
        mCameraVideoProgressBar.setCameraVideoFinishCallback(mCameraVideoFinishCallback);

        bottomLayout.addView(mEvertCameraButton);
        bottomLayout.addView(mCameraTakePhotoView);
        bottomLayout.addView(mCameraBackView);
        bottomLayout.addView(mCameraNOView);
        bottomLayout.addView(mCameraOKView);
        bottomLayout.addView(mCameraVideoProgressBar);

        mCameraOKView.setVisibility(View.GONE);
        mCameraNOView.setVisibility(View.GONE);
        mCameraVideoProgressBar.setVisibility(View.GONE);
    }

    private void getBrightView() {
        mBrightView = new BrightView(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ABOVE, bottomLayout.getId());
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        layoutParams.setMargins(0, 0, 0, 15);
        mBrightView.setLayoutParams(layoutParams);
        mBrightView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openOrCloseLight();
            }
        });
    }

    private void addView() {
        // 没有摄像头就关闭
        if(!checkCamera()) {
            this.finish();
        }
        rootLayout.addView(mSurfaceView);
//        rootLayout.addView(mEvertCameraButton);
        rootLayout.addView(bottomLayout);
        rootLayout.addView(mBrightView);
    }


    private SurfaceHolder.Callback mSurfaceCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mSurfaceHolder = holder;
            openCamera(currentCameraType);
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            mSurfaceHolder = holder;
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
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
                Log.d(TAG, "surfaceDestroyed release mRecorder");
            }
        }
    };


    /**
     * 打开相机
     * @param type
     */
    private void openCamera(int type) {
        releaseCamera();
        if(mCamera == null) {
            if(type == FRONT) {
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
            }else if(type == BACK) {
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
//        mCamera = Camera.open();
        setCameraDisplayOrientation(this, Camera.CameraInfo.CAMERA_FACING_BACK, mCamera);
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.startPreview();
    }

    /**
     * 释放相机资源
     */
    private synchronized void releaseCamera() {
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

    /**
     * 显示方向设置
     * @param activity
     * @param cameraId
     * @param camera
     */
    private void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
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
     * 切换摄像头事件
     */
    private View.OnClickListener evertCameraClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(getNumberOfCameras() == 1) {
                return;
            }
            changeCamera();
        }
    };

    /**
     * 拍照
     */
    private View.OnClickListener tackPictureShortClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // 拍照
            if(action == 0) {
                if(mCamera != null) {
                    mCamera.takePicture(null,null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
                            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                            if (bitmap == null) return;
                            mCameraTakePhotoView.setVisibility(View.GONE);
                            mCameraBackView.setVisibility(View.GONE);
                            mEvertCameraButton.setVisibility(View.GONE);
                            mCameraOKView.setVisibility(View.VISIBLE);
                            mCameraNOView.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
            // 录像
            else if(action == 1) {
                // 此时非自然录像结束，属于手指抬起时的结束，先判断录像时间是否短于5秒
                if(videoFinishType == 0) {
                    stopVideoTime = System.currentTimeMillis();
                    // 录像时间短于3秒，继续录制到3秒，但是删除该视频文件，提示时间太短，录像失败
                    if((stopVideoTime - startVideoTime) < minVideoTime * 1000) {
                        mCameraVideoProgressBar.minVideoTimeStop = true;
                        Toast.makeText(CameraActivity.this, "录像时间小于3秒录像失败，请稍后重新录制", Toast.LENGTH_SHORT).show();
                        return;
                    }
//                    Toast.makeText(CameraActivity.this, "录像结束了", Toast.LENGTH_SHORT).show();
                    stopMediaRecorder();
                    mCameraVideoProgressBar.stopVideo();
                }
            }
        }
    };

    /**
     * 开始录像
     */
    private View.OnLongClickListener tackVideoLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Log.e(TAG, "开始录像了");
            // 记录按下时间
            startVideoTime = System.currentTimeMillis();
            action = 1;
            mCameraVideoProgressBar.setVisibility(View.VISIBLE);
            mCameraTakePhotoView.setVisibility(View.GONE);
            // 开始计时进度条
            mCameraVideoProgressBar.setTime(videoTime);
            mCameraVideoProgressBar.startVideo();
            mCamera.unlock();
            initMediaRecorder();
            startMediaRecorder();
            return false;
        }
    };

    /**
     * 检查是否有摄像头
     * @return
     */
    private boolean checkCamera(){
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    private int getNumberOfCameras() {
        return Camera.getNumberOfCameras();
    }

    /**
     * 切换摄像头
     */
    private void changeCamera() {
        if(currentCameraType == BACK) {
            currentCameraType = FRONT;
        }else {
            currentCameraType = BACK;
        }
        openCamera(currentCameraType);
    }


    /**
     * 给调用者(Activity)传值 -- 路径
     * @param bitmap
     */
    private void backActivity(Bitmap bitmap) {
        // 把图片转成竖屏的
        Matrix matrix = new Matrix();
        matrix.setRotate(90);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        String path = CameraFileUtil.getInstance(CameraActivity.this).getPhotoPath(bitmap);
        if(bitmap != null && !bitmap.isRecycled()){
            bitmap.recycle();
            bitmap = null;
        }
        System.gc();
        Log.e(TAG, "path ================ " + path);
//        Intent intent = new Intent();
//        intent.putExtra("bitmapPath", path);
//        setResult(1, intent);
        listPath.clear();
        listPath.add(path);
        cameraCallback.cameraActionFinish(listPath);

        CameraActivity.this.finish();
    }

    /**
     * 录像结束回调
     */
    private CameraVideoProgressBar.CameraVideoFinishCallback mCameraVideoFinishCallback = new CameraVideoProgressBar.CameraVideoFinishCallback() {
        @Override
        public void cameraVideoFinish(int type) {
            videoFinishType = type;
            stopMediaRecorder();
            if(videoFinishType != 2) {
                mCameraVideoProgressBar.setVisibility(View.GONE);
                mCameraBackView.setVisibility(View.GONE);
                mEvertCameraButton.setVisibility(View.GONE);
                mCameraNOView.setVisibility(View.VISIBLE);
                mCameraOKView.setVisibility(View.VISIBLE);
            }else {
                videoFinishType = 0;
                action = 0;
                mCameraVideoProgressBar.setVisibility(View.GONE);
                mCameraTakePhotoView.setVisibility(View.VISIBLE);
            }
            videoFinishType = 0;
            action = 0;
            mCamera.startPreview();
            // 如果录像时间太短，就删除此录像文件（暂时不用删除，因为整个文件夹都是临时文件）
//            if(videoFinishType == 2) {
//                videoPath
//
//            }
        }
    };


    /**
     * 初始化视频配置
     */
    private void initMediaRecorder() {
        if(mMediaRecorder == null) {
            mMediaRecorder = new MediaRecorder();
        }
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
        String path = CameraFileUtil.getInstance(this).getPath(action);
        videoPath = path;
        mMediaRecorder.setOutputFile(path);
    }



    /**
     * 开始录像
     */
    private void startMediaRecorder() {
        if(mMediaRecorder == null) return;
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
    private void stopMediaRecorder() {
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

    private boolean lightOpen;

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
//        String flashMode = parameters.getFlashMode();
//        if(flashMode == null) return;
//        if (flashMode.equals("torch")) {
//            parameters.setFlashMode("off");
//            mBrightView.setLight(false);
//        } else {
//            parameters.setFlashMode("torch");
//            mBrightView.setLight(true);
//        }
//        mCamera.setParameters(parameters);
    }
}
