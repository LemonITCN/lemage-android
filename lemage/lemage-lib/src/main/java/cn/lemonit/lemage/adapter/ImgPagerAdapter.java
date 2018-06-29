package cn.lemonit.lemage.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.support.v4.view.PagerAdapter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cn.lemonit.lemage.R;
import cn.lemonit.lemage.bean.FileObj;
import cn.lemonit.lemage.bean.Photo;
import cn.lemonit.lemage.bean.Video;
import cn.lemonit.lemage.view.LmageVideoView;
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

    //对显示的资源进行初始化
    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        RelativeLayout view = new RelativeLayout(mContext);
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
        LmageVideoView mLmageVideoView = new LmageVideoView(mContext);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        mLmageVideoView.setLayoutParams(layoutParams);
        view.addView(mLmageVideoView);

        MediaMetadataRetriever media = new MediaMetadataRetriever();
        media.setDataSource(listFile.get(position).getPath());
        Bitmap bitmap = media.getFrameAtTime();
        mLmageVideoView.getmImageView().setImageBitmap(bitmap);
    }
}
