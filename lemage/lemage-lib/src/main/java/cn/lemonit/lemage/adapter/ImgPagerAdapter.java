package cn.lemonit.lemage.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import cn.lemonit.lemage.bean.Photo;
import cn.lemonit.lemage.view.ZoomImageView;

/**
 * 预览viewpager适配器
 */
public class ImgPagerAdapter extends PagerAdapter {

    private final String TAG = "ImgPagerAdapter";

    private Context mContext;
    private ArrayList<Photo> listPhoto;
    private ArrayList<String> listPath;
    private ArrayList<String> listName;

    private ImgOnClickListener mImgOnClickListener;

//    private LinkedList<View> viewLinkedList = new LinkedList<>();  ;
//
////    private List<Bitmap> listBitmap;
//    /**
//     * 图片缓存技术的核心类，用于缓存所有下载好的图片，在程序内存达到设定值时会将最少最近使用的图片移除掉。
//     */
//    private LruCache<String, Bitmap> mMemoryCache;
//
//    // 缓存view
//    private List<View> mViewCache;//缓存view

    public ImgPagerAdapter(Context mContext, ArrayList<Photo> listPhoto, ArrayList<String> listPath, ArrayList<String> listName) {
        this.mContext = mContext;
        this.listPhoto = listPhoto;
        this.listPath = listPath;
        this.listName = listName;
    }

    @Override
    public int getCount() {
        return listPhoto.size();
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
        ZoomImageView imageView = new ZoomImageView(mContext);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mImgOnClickListener.imgOnClick();
            }
        });
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        imageView.setLayoutParams(params);
        Glide.with(mContext).load(listPath.get(position)).into(imageView);

        view.addView(imageView);
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
}
