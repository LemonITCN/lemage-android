package cn.lemonit.lemage.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.lemorage.file.LemixFileCommon;
import com.lemorage.file.Lemorage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.lemonit.lemage.been.FileObj;
import cn.lemonit.lemage.been.NetBeen;
import cn.lemonit.lemage.been.Video;
import cn.lemonit.lemage.util.PathUtil;
import cn.lemonit.lemage.view.preview_view.LemageVideoView;
import cn.lemonit.lemage.view.select_view.MyZoomImageView;

/**
 * 预览viewpager适配器
 * @author: zhaoguangyang
 */
public class ImgPagerAdapter extends PagerAdapter {

    private final String TAG = "ImgPagerAdapter";

    private Context mContext;
    private ArrayList<FileObj> listFile;

    private PathUtil mPathUtil;
    private List<PathUtil> listPathUtil = new ArrayList<PathUtil>();

    private ImgOnClickListener mImgOnClickListener;

    private MyZoomImageView imageView;

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

        mPathUtil = new PathUtil(mContext);
        listPathUtil.add(mPathUtil);
        // 如果是网络资源，下载完成回调显示
        mPathUtil.setDownLoadFileFinishListener(new PathUtil.DownLoadFileFinishListener() {
            @Override
            public void downLoadFileFinish(NetBeen netBeen) {
                if(netBeen == null) {
                    return;
                }
                if(netBeen.getType() == 0) {
                    fileObj.setPath(netBeen.getPath());
//                    fileObj.setPath(LemixFileCommon.getBaseUrl(mContext) + File.separator + Lemorage.SEND_BOX_SHORT);
                    showPhotoStyleView(view, position, netBeen);
                }else {
//                    fileObj.setPath(LemixFileCommon.getBaseUrl(mContext) + File.separator + Lemorage.SEND_BOX_SHORT);
                    fileObj.setPath(netBeen.getPath());
                    showVideoStyleView(view, position, netBeen);
                }
//                addWhiteView(view);
            }
        });
        NetBeen mNetBeen = mPathUtil.getNetBeen(fileObj.getPath());
//        Log.e(TAG, "网络视频 ==================== " + mNetBeen.getPath());
        if(mNetBeen != null) {
            if(mNetBeen.getType() == 0) {
                showPhotoStyleView(view, position, mNetBeen);
            }else {
                showVideoStyleView(view, position, mNetBeen);
            }
//            addWhiteView(view);
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
//        ZoomImageView imageView = new ZoomImageView(mContext);
//        imageView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mImgOnClickListener.imgOnClick();
//            }
//        });
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
//        imageView.setLayoutParams(params);
////        imageView.setAdjustViewBounds(true);
////        imageView.setMaxHeight(ScreenUtil.getScreenHeight(mContext));
////        imageView.setMaxWidth(ScreenUtil.getScreenWidth(mContext));
////        params.addRule(RelativeLayout.CENTER_IN_PARENT);
//        Glide.with(mContext).load(mNetBeen.getPath()).into(imageView);
//        imageView.setBackgroundColor(Color.parseColor("#FFcc00"));
//        view.addView(imageView);

//        MyZoomImageView imageView = new MyZoomImageView(mContext);
        imageView = new MyZoomImageView(mContext);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImgOnClickListener.imgOnClick();
            }
        });
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,  RelativeLayout.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(params);
        Glide.with(mContext).load(mNetBeen.getPath()).into(imageView);
        imageView.setBackgroundColor(Color.BLACK);
        view.addView(imageView);
    }

    public MyZoomImageView getImageView() {
        return imageView;
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
                        if(mLemageVideoView.getControlView() == null) {
                            return false;
                        }
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

    public void stopDownLoadTask() {
        for(PathUtil pathUtil : listPathUtil) {
            pathUtil.stopDownLoad();
        }
    }
}
