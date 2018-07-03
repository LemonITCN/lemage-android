package cn.lemonit.lemage.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import cn.lemonit.lemage.bean.FileObj;
import cn.lemonit.lemage.bean.Photo;
import cn.lemonit.lemage.bean.Video;
import cn.lemonit.lemage.view.ControlView;
import cn.lemonit.lemage.view.LemageVideoView;
import cn.lemonit.lemage.view.ScreenVideoView;
import cn.lemonit.lemage.view.VideoStartImageView;
import cn.lemonit.lemage.view.ZoomImageView;

/**
 * 预览viewpager适配器
 */
public class ImgPagerAdapter extends PagerAdapter {

    private final String TAG = "ImgPagerAdapter";

    private Context mContext;
    private ArrayList<FileObj> listFile;

    private ImgOnClickListener mImgOnClickListener;

    // 进度条进度
    private int progress = 0;
    // 视频已经观看的秒数
    private int finishTime = -1;

    public ImgPagerAdapter(Context mContext, ArrayList<FileObj> listFile) {
        this.mContext = mContext;
        this.listFile = listFile;
    }

    @Override
    public int getCount() {
        return listFile.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View view = (View) object;
        container.removeView(view);
    }

    //对显示的资源进行初始化
    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        RelativeLayout view = new RelativeLayout(mContext);
        view.setBackgroundColor(Color.BLACK);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(layoutParams);
        FileObj fileObj = listFile.get(position);
        // 分别显示图片或者视频的对应的样式
        if(fileObj instanceof Photo) {
            showPhotoStyleView(view, position);
        }else if(fileObj instanceof Video) {
            showVideoStyleView(view, position);
        }
        container.addView(view);
        return view;
    }


    /**
     * 事件回调
     */
    public interface ImgOnClickListener {
        void imgOnClick();
    }

    public void setImgOnClickListener(ImgOnClickListener mImgOnClickListener) {
        this.mImgOnClickListener = mImgOnClickListener;
    }


    /**
     * 当item是图片时需要显示的样式
     * @param view
     */
    private void showPhotoStyleView(RelativeLayout view, int position) {
        ZoomImageView imageView = new ZoomImageView(mContext);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImgOnClickListener.imgOnClick();
            }
        });
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(params);
        Glide.with(mContext).load(listFile.get(position).getPath()).into(imageView);
        view.addView(imageView);
    }

    /**
     * 当item是视频时要显示的样式
     * @param view
     */
    private void showVideoStyleView(RelativeLayout view, int position) {
        final Video mVideo = (Video) listFile.get(position);

        LemageVideoView mLemageVideoView = new LemageVideoView(mContext);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mLemageVideoView.setLayoutParams(layoutParams);
        view.addView(mLemageVideoView);

        // 显示静态图片
        MediaMetadataRetriever media = new MediaMetadataRetriever();
        // 视频时长
//        final long duration = mVideo.getDuration();
        media.setDataSource(mVideo.getPath());
        String durationStr = media.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        final long duration = Long.parseLong(durationStr);
        final int time = (int) Math.floor((duration / 1000));   // 秒
        // 视频路径
        final String path = mVideo.getPath();
        media.setDataSource(path);
        Bitmap bitmap = media.getFrameAtTime();
        final ImageView mImageView = mLemageVideoView.getImageView();
        mImageView.setImageBitmap(bitmap);
        // 播放视频控件
        final ScreenVideoView mVideoView = mLemageVideoView.getVideoView();
        // 底部视频控制条
        final ControlView mContralView = mLemageVideoView.getControlView();
        // 进度条
        final ProgressBar mProgressBar = mContralView.getProgressBar();

        final Handler mHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 0) {
                    if(progress < 101) {
                        mProgressBar.setProgress(progress ++);
                    }
                }
            }
        };



        final Thread mThread = new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i = 0; i < 101; i ++) {
                    try {
                        Thread.sleep(duration / 100);
                        mHandler.sendEmptyMessage(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // 显示时间
        final Handler timeHandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what == 1) {
                    Log.e(TAG, "Handler时间更新");
                    finishTime ++;
                    if(finishTime < time + 1) {
                        mContralView.setTimeText(finishTime + " / " + time);
                    }
                }
            }
        };

        final Timer mTimer = new Timer();
        final TimerTask mTimerTask = new TimerTask() {
            @Override
            public void run() {
                Log.e(TAG, "TimerTask时间更新");
                timeHandler.sendEmptyMessage(1);
            }
        };

        // 静态图片中的开始按钮的点击事件
        final VideoStartImageView mVideoStartImageView = mLemageVideoView.getBigVideoStartImageView();
        mVideoStartImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mVideoStartImageView.setVisibility(View.GONE);
                mImageView.setVisibility(View.GONE);
                Uri uri = Uri.parse(path);
                mVideoView.setVideoURI(uri);
                // 不调用控制器，否则系统的进度条没法全部去除
//                MediaController mediaController = new MediaController(mContext);
//                mediaController.setVisibility(View.GONE);
//                mVideoView.setMediaController(mediaController);
                mVideoView.requestFocus();
                mVideoView.start();

                mTimer.schedule(mTimerTask, 0,1000);
                mThread.start();


//                String path = "http://ivi.bupt.edu.cn/hls/cctv1hd.m3u8";   // 中央一台
////                String path = "http://ivi.bupt.edu.cn/hls/cctv5hd.m3u8";   // 中央五台
////                String path = "http://221.228.226.23/6/n/a/y/l/naylspkwvsujoltcqursegarxzowax/hd.yinyuetai.com/C02F015B377EA255563C19FBEF88B071.mp4";
////                String path = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
////                String path = "http://wxsnsdy.tc.qq.com/105/20210/snsdyvideodownload?filekey=30280201010421301f0201690402534804102ca905ce620b1241b726bc41dcff44e00204012882540400&bizid=1023&hy=SH&fileparam=302c020101042530230204136ffd93020457e3c4ff02024ef202031e8d7f02030f42400204045a320a0201000400.mp4";
//                mVideoView.setVideoURI(Uri.parse(path));
//                mVideoView.requestFocus();
//                mVideoView.start();
            }
        });

        // 播放结束监听
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mVideoStartImageView.setVisibility(View.VISIBLE);
                mImageView.setVisibility(View.VISIBLE);
                mTimer.cancel();
                mTimerTask.cancel();
            }
        });
        // 点击时，顶部条和底部条隐藏和显示交替
        mVideoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action) {
                    case MotionEvent.ACTION_UP:
                        if(mContralView == null) return false;
                        if(mContralView.isShow()) {
                            mContralView.setShow(false);
                            mContralView.setVisibility(View.GONE);
                        }else {
                            mContralView.setShow(true);
                            mContralView.setVisibility(View.VISIBLE);
                        }
                        break;
                    case MotionEvent.ACTION_DOWN:
                        return true;
                }
                return false;
            }
        });


        // 底部条开始按钮
        final VideoStartImageView videoStartImageView = mContralView.getVideoStartImageView();
        videoStartImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mVideoView.isPlaying()) {
                    mVideoView.pause();
                    videoStartImageView.setPause(true);
                }else {
                    mVideoView.start();
                    videoStartImageView.setPause(false);
                }
            }
        });
    }
}
