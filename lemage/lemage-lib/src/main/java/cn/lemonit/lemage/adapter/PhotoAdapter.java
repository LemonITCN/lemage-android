package cn.lemonit.lemage.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.lemonit.lemage.bean.Album;
import cn.lemonit.lemage.bean.Photo;
import cn.lemonit.lemage.util.ScreenUtil;
import cn.lemonit.lemage.view.PhotoView;

public class PhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * 当前正在显示的相册
     */
    private Album currentAlbum;
    /**
     * 上下文对象
     */
    private Context context;

    public PhotoAdapter(Context context, Album currentAlbum) {
        this.context = context;
        this.currentAlbum = currentAlbum;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhotoViewHolder(new PhotoView(context));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int width = (int) ((ScreenUtil.getScreenWidth(context) - 60) / 4.0);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(width, width);
        holder.itemView.setLayoutParams(params);
        PhotoViewHolder photoViewHolder = (PhotoViewHolder) holder;
        Glide.with(context).load(currentAlbum.getPhotoList().get(position % currentAlbum.getPhotoList().size()).getPath()).centerCrop().into(((PhotoViewHolder) holder).getPhotoView().getImageView());
    }

    @Override
    public int getItemCount() {
        if (currentAlbum == null) {
            return 0;
        }
        return currentAlbum.getPhotoList().size() * 20;
    }

    private class PhotoViewHolder extends RecyclerView.ViewHolder {

        private PhotoView photoView;

        public PhotoViewHolder(PhotoView photoView) {
            super(photoView);
            this.photoView = photoView;
        }

        public PhotoView getPhotoView() {
            return photoView;
        }
    }

}