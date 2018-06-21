package cn.lemonit.lemage.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.lemonit.lemage.bean.Album;
import cn.lemonit.lemage.bean.Photo;
import cn.lemonit.lemage.util.ScreenUtil;
import cn.lemonit.lemage.view.PhotoView;

/**
 * 照片选择器的适配器
 *
 * @author LemonIT.CN
 */
public class PhotoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = "PhotoAdapter";

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

    /**
     * 已经选中的图片集合
     */
    private List<Photo> checkPhotoList = new ArrayList<Photo>();

    private PhotoViewOnClickListener mPhotoViewOnClickListener;

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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(getImgWidth(), getImgWidth());
        // 最后一个图片，让其有下边距，使滑动空间变大
        if (position == (getItemCount() - 1)) {
            params.bottomMargin = 200;
        }
        params.leftMargin = 5;
        params.rightMargin = 5;
        if (position < getColumnCount()) {
            params.topMargin = 120;
        } else {
            params.topMargin = 10;
        }
        holder.itemView.setLayoutParams(params);
        // 加载图片
        Glide.with(context).load(currentAlbum.getPhotoList().get(position % currentAlbum.getPhotoList().size()).getPath()).centerCrop().into(((PhotoViewHolder) holder).getPhotoView().getImageView());

        // 根据状态显示样式
        if(currentAlbum.getPhotoList().get(position).getStatus() == 0) {
            notCheckView(((PhotoViewHolder) holder).photoView);
        }else {
            int index = checkPhotoList.indexOf(currentAlbum.getPhotoList().get(position)) + 1;
            checkView(((PhotoViewHolder) holder).photoView, index);
        }
        // 事件
        ((PhotoViewHolder) holder).photoView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    // 触摸点
                    float touchX = event.getX();
                    float touchY = event.getY();
                    // 第二象限边界
                    float borderX = view.getWidth() / 2;
                    float borderY = view.getHeight() / 2;

                    // 触摸点在第二象限, , 否则直接进入预览
                    if(touchX > borderX && touchY < borderY) {
                        switchSelectStatus(position, (PhotoView) view);
                    }else {
                        Log.e(TAG, "position ========= " + position);
                        mPhotoViewOnClickListener.onClickPreviewListener(currentAlbum.getPhotoList(), position);
                    }
                    return false;
                }
                return true;
            }
        });
    }

    /**
     * 切换选中状态
     */
    private void switchSelectStatus(int position, PhotoView view) {
        int status = view.getStatus();
        switch (status) {
            // 未选中变选中
            case 0:
                currentAlbum.getPhotoList().get(position).setStatus(1);
                checkPhotoList.add(currentAlbum.getPhotoList().get(position));
                break;
            // 选中变未选中
            case 1:
                currentAlbum.getPhotoList().get(position).setStatus(0);
                checkPhotoList.remove(currentAlbum.getPhotoList().get(position));
                break;
        }
        notifyDataSetChanged();
        mPhotoViewOnClickListener.onClickSelectListener(checkPhotoList);
//        mPhotoViewOnClickListener.onClickSelectListener(currentAlbum.getPhotoList());
    }

    @Override
    public int getItemCount() {
        if (currentAlbum == null) {
            return 0;
        }
        return currentAlbum.getPhotoList().size();
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

    public interface PhotoViewOnClickListener {
        void onClickSelectListener(List<Photo> list);
        void onClickPreviewListener(List<Photo> list, int position);
    }

    public void setPhotoViewOnClickListener(PhotoViewOnClickListener mPhotoViewOnClickListener) {
        this.mPhotoViewOnClickListener = mPhotoViewOnClickListener;
    }

    /**
     * 选中时点击恢复为未选中
     */
    private void notCheckView(PhotoView mPhotoView) {
        mPhotoView.changeStatus(0, 0);
    }

    /**
     * 未选中时点击变为选中，且传递number
     */
    private void checkView(PhotoView mPhotoView, int number) {
        mPhotoView.changeStatus(1, number);
    }

    public Album getAlbum() {
        return currentAlbum;
    }
}