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
import cn.lemonit.lemage.bean.NetBeen;
import cn.lemonit.lemage.bean.Photo;
import cn.lemonit.lemage.bean.Video;
import cn.lemonit.lemage.util.PathUtil;
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


    //预加载界面
    @Override
    public Object instantiateItem(final ViewGroup container, final int position) {
        final RelativeLayout view = new RelativeLayout(mContext);
        view.setBackgroundColor(Color.BLACK);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(layoutParams);
        final FileObj fileObj = listFile.get(position);

        PathUtil mPathUtil = new PathUtil(mContext);
        mPathUtil.setDownLoadFileFinishListener(new PathUtil.DownLoadFileFinishListener() {
            @Override
            public void downLoadFileFinish(NetBeen netBeen) {
                if(netBeen == null) return;
                fileObj.setPath(netBeen.getPath());
                if(netBeen.getType() == 0) {
                    showPhotoStyleView(view, position, netBeen);
                }else {
                    showVideoStyleView(view, position, netBeen);
                }
                container.removeAllViews();
                container.addView(view);
            }
        });
        NetBeen mNetBeen = mPathUtil.getNetBeen(fileObj.getPath());
        if(mNetBeen.getType() == 0) {
            showPhotoStyleView(view, position, mNetBeen);
        }else {
            showVideoStyleView(view, position, mNetBeen);
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
    private void showPhotoStyleView(RelativeLayout view, int position, NetBeen mNetBeen) {
        ZoomImageView imageView = new ZoomImageView(mContext);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImgOnClickListener.imgOnClick();
            }
        });
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(params);
        Glide.with(mContext).load(mNetBeen.getPath()).into(imageView);
        view.addView(imageView);
    }


    /**
     * 当item是视频时要显示的样式
     * @param view
     */
    private void showVideoStyleView(RelativeLayout view, int position, NetBeen mNetBeen) {
        final Video mVideo = new Video();
        mVideo.setPath(mNetBeen.getPath());
        mVideo.setStatus(listFile.get(position).getStatus());

        final LemageVideoView mLemageVideoView = new LemageVideoView(mContext, mNetBeen.getPath());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mLemageVideoView.setLayoutParams(layoutParams);
        // 点击视频屏幕，顶部条和底部条显示和隐藏
        mLemageVideoView.getVideoView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action) {
                    case MotionEvent.ACTION_UP:
                        if(mLemageVideoView.getControlView() == null) return false;
                        if(mLemageVideoView.getControlView().isShow()) {
                            mLemageVideoView.getControlView().setShow(false);
                            mLemageVideoView.getControlView().setVisibility(View.GONE);
                        }else {
                            mLemageVideoView.getControlView().setShow(true);
                            mLemageVideoView.getControlView().setVisibility(View.VISIBLE);
                        }
                        break;
                    case MotionEvent.ACTION_DOWN:
                        return true;
                }
                return false;
            }
        });
        view.addView(mLemageVideoView);
    }

}
