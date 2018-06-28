package cn.lemonit.lemage.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
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
import cn.lemonit.lemage.bean.AlbumNew;
import cn.lemonit.lemage.bean.FileObj;
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
//    private Album currentAlbum;
    private AlbumNew currentAlbum;
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
    private List<FileObj> checkPhotoList = new ArrayList<FileObj>();

    private PhotoViewOnClickListener mPhotoViewOnClickListener;
    /**
     * 选中的序列号
     */
    private int number;

    public PhotoAdapter(Context context, AlbumNew currentAlbum) {
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
        if(currentAlbum.getFileList().get(position) instanceof Photo) {
            Glide.with(context).load(currentAlbum.getFileList().get(position % currentAlbum.getFileList().size()).getPath()).centerCrop().into(((PhotoViewHolder) holder).getPhotoView().getImageView());
        }else {
            MediaMetadataRetriever media = new MediaMetadataRetriever();
            String videoPath = currentAlbum.getFileList().get(position).getPath();
            media.setDataSource(videoPath);
            Bitmap bitmap = media.getFrameAtTime();
            ((PhotoViewHolder) holder).getPhotoView().getImageView().setImageBitmap(bitmap);
        }

        // 根据状态显示样式
        if(currentAlbum.getFileList().get(position).getStatus() == 0) {
            notCheckView(((PhotoViewHolder) holder).photoView);
        }else {
//            int index = checkPhotoList.indexOf(currentAlbum.getPhotoList().get(position)) + 1;
            checkView(((PhotoViewHolder) holder).photoView, currentAlbum.getFileList().get(position).getNumber());
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
                        mPhotoViewOnClickListener.onClickPreviewListener(currentAlbum.getFileList(), position);
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
            // 未选中变选中(对之前已经选中的没有任何影响)
            case 0:
                number ++;
                currentAlbum.getFileList().get(position).setNumber(number);
                currentAlbum.getFileList().get(position).setStatus(1);
                checkPhotoList.add(currentAlbum.getFileList().get(position));
                break;
            // 选中变未选中（在这之前的没有影响，之后的序列号都减一）
            case 1:
                // 先刷新序列号
                FileObj currentFile = currentAlbum.getFileList().get(position);  // 当前被改变的photo
                for(int i = 0; i < currentAlbum.getFileList().size(); i ++) {
                    int numberOld = currentAlbum.getFileList().get(i).getNumber();
                    if(currentAlbum.getFileList().get(i).getStatus() == 1 && numberOld > currentFile.getNumber()) {
                        currentAlbum.getFileList().get(i).setNumber(numberOld - 1);
                    }
                }
                number --;
                // 更改选中状态
                checkPhotoList.remove(currentFile);
                currentFile.setNumber(0);
                currentFile.setStatus(0);
                break;
        }
        notifyDataSetChanged();
        mPhotoViewOnClickListener.onClickSelectListener(checkPhotoList);
    }

    @Override
    public int getItemCount() {
        if (currentAlbum == null) {
            return 0;
        }
        return currentAlbum.getFileList().size();
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
        void onClickSelectListener(List<FileObj> list);
        void onClickPreviewListener(List<FileObj> list, int position);
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

    public AlbumNew getAlbumNew() {
        return currentAlbum;
    }

    /**
     * 当预览界面更改选中图片时，同步数据
     * @param list
     */
    public void changeList(List<FileObj> list) {
        number = list.size();
        checkPhotoList.clear();
        checkPhotoList.addAll(list);
    }
}