package cn.lemonit.lemage.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import cn.lemonit.lemage.bean.Album;
import cn.lemonit.lemage.util.ScreenUtil;
import cn.lemonit.lemage.view.PhotoView;

/**
 * 照片选择器的适配器
 *
 * @author LemonIT.CN
 */
public class PhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /**
     * 当前正在显示的相册
     */
    private Album currentAlbum;
    /**
     * 上下文对象
     */
    private Context context;
    /**
     * 每个图片的宽度
     */
    private int imgWidth = 0;
    /**
     * 列数量
     */
    private int columnCount = 0;

    public PhotoAdapter(Context context, Album currentAlbum) {
        this.context = context;
        this.currentAlbum = currentAlbum;
    }

    /**
     * 获取图片选择器中的每个图片选项的宽度
     *
     * @return 宽度数值
     */
    private int getImgWidth() {
        if (imgWidth == 0) {
            imgWidth = (ScreenUtil.getScreenWidth(context) - (getColumnCount() + 1) * 10) / getColumnCount();
        }
        return imgWidth;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PhotoViewHolder(new PhotoView(context, getImgWidth()));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(getImgWidth(), getImgWidth());
        // 最后一个图片，让其有下边距，使滑动空间变大
        if (position == (getItemCount() - 1)) {
            params.bottomMargin = 200;
        }
        params.leftMargin = 10;
        if (position < getColumnCount()) {
            params.topMargin = 120;
        } else {
            params.topMargin = 10;
        }
        holder.itemView.setLayoutParams(params);
        // 加载图片
        Glide.with(context).load(currentAlbum.getPhotoList().get(position % currentAlbum.getPhotoList().size()).getPath()).centerCrop().into(((PhotoViewHolder) holder).getPhotoView().getImageView());
    }

    @Override
    public int getItemCount() {
        if (currentAlbum == null) {
            return 0;
        }
        return currentAlbum.getPhotoList().size() * 19 + 1;
    }

    public int getColumnCount() {
        if (columnCount == 0) {
            columnCount = 4;
        }
        return columnCount;
    }

    /**
     * 照片选择项视图的ViewHolder
     *
     * @author LemonIT.CN
     */
    private class PhotoViewHolder extends RecyclerView.ViewHolder {

        private PhotoView photoView;

        PhotoViewHolder(PhotoView photoView) {
            super(photoView);
            this.photoView = photoView;
        }

        PhotoView getPhotoView() {
            return photoView;
        }
    }

}