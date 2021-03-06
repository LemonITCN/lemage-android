package cn.lemonit.lemage.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.lemonit.lemage.been.Album;
import cn.lemonit.lemage.util.ScreenUtil;
import cn.lemonit.lemage.view.select_view.PhotoView;

/**
 * 图片文件夹横向移动栏适配器
 * @author: zhaoguangyang
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private final String TAG = "AlbumAdapter";

    private Context mContext;

    private ArrayList<Album> mAlbumList;

    private int imgWidth = 0;
    private int columnCount = 0;
    /**
     * 主题颜色
     */
    private int mColor;

    /**
     * 因为图片是同一个对象，所以用PhotoView来记录选中状态
     */
    private List<PhotoView> mListPhotoView;

    private AlbumItemOnClickListener mAlbumItemOnClickListener;

    public AlbumAdapter(Context context, ArrayList<Album> albumList, int mColor) {
        mContext = context;
        mAlbumList = albumList;
        this.mColor = mColor;
        init();
    }

    private void init() {
        if(mListPhotoView == null) {
            mListPhotoView = new ArrayList<PhotoView>();
        }
        int count = mAlbumList.size();
        for(int i = 0; i < count; i ++) {
            PhotoView mPhotoView = new PhotoView(mContext, 0, mColor);
            if(i == 0) {
                mPhotoView.setStatus(1);
            }else {
                mPhotoView.setStatus(0);
            }
            mListPhotoView.add(mPhotoView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // 文字是屏幕高度的 1 / 10
//        int textHeight = ScreenUtil.getScreenHeight(mContext) / 15;

        int height = ScreenUtil.getScreenHeight(mContext) / 6 + ScreenUtil.dp2px(mContext, 20);

        LinearLayout view = new LinearLayout(mContext);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getImgWidth(), LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getImgWidth(), height);
        view.setLayoutParams(layoutParams);
        view.setOrientation(LinearLayout.VERTICAL);
        view.setPadding(10,10,10,10);

        // 图片高度和文字高度比为 5 ： 1
        PhotoView mPhotoView = new PhotoView(mContext, getImgWidth(), mColor);
        LinearLayout.LayoutParams layoutParamsImg = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 5.0f);
//        LinearLayout.LayoutParams layoutParamsImg = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getImgWidth() - textHeight);
//        layoutParamsImg.setMargins(0,0,0, 30);
        mPhotoView.setLayoutParams(layoutParamsImg);
        view.addView(mPhotoView);

        TextView mTextView = new TextView(mContext);
        LinearLayout.LayoutParams layoutParamsText = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0f);
//        LinearLayout.LayoutParams layoutParamsText = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, textHeight);
//        layoutParamsText.setMargins(0,5,0,0);

//        mTextView.setTextSize(ScreenUtil.dp2px(mContext, 8));
        // 暂时以1280的屏幕高度设置为8的字体为标准字体
        mTextView.setTextSize(ScreenUtil.dp2px(mContext, 8 * ScreenUtil.getScreenHeight(mContext) / 1280));
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setTextColor(Color.WHITE);
        mTextView.setLayoutParams(layoutParamsText);
        mTextView.setBackgroundColor(Color.BLACK);
        view.addView(mTextView);

        ViewHolder mViewHolder = new ViewHolder(view);
        return mViewHolder;
    }

    private int getImgWidth() {
        if (imgWidth == 0) {
            imgWidth = (ScreenUtil.getScreenWidth(mContext) - (getColumnCount() + 1) * 10) / getColumnCount();
        }
        return imgWidth;
    }

    public int getColumnCount() {
        if (columnCount == 0) {
            columnCount = 4;
        }
        return columnCount;
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        if(mAlbumList.get(position).getFileList().size() > 0) {
            Glide.with(mContext).load(mAlbumList.get(position).getFileList().get(0).getPath()).into(holder.mPhototView.getImageView());
        }
        holder.mTextView.setText(mAlbumList.get(position).getName());

        // item是选中状态就打勾
        if(mListPhotoView.get(position).getStatus() == 1) {
            checkView(holder.mPhototView);
        }else {
            notCheckView(holder.mPhototView);
        }
        // 事件
        holder.mPhototView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int status = mListPhotoView.get(position).getStatus();
                switch (status) {
                    case 0:
                        // 未选中变选中
                        mListPhotoView.get(position).setStatus(1);
                        // 把其他所有都变成未选中
                        for (int i = 0; i < mListPhotoView.size(); i ++) {
                            if(position != i) {
                                mListPhotoView.get(i).setStatus(0);
                            }
                        }
                        mAlbumItemOnClickListener.notifShow(mAlbumList.get(position));
                        break;
                    case 1:
                        mAlbumItemOnClickListener.constantShow();
                        break;
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mAlbumList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private PhotoView mPhototView;
        private TextView mTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mPhototView = (PhotoView) ((ViewGroup)itemView).getChildAt(0);
            mTextView = (TextView) ((ViewGroup)itemView).getChildAt(1);
        }
    }

    /**
     * 选中item时是对勾
     * @param mPhotoView
     */
    private void checkView(PhotoView mPhotoView) {
        mPhotoView.changeStatus(2, 0);
    }

    private void notCheckView(PhotoView mPhotoView) {
        mPhotoView.changeStatus(0, 0);
    }

    /**
     * 事件回调
     */
    public interface AlbumItemOnClickListener {
        void constantShow();
        void notifShow(Album mAlbum);
    }

    public void setAlbumItemOnClickListener(AlbumItemOnClickListener mAlbumItemOnClickListener) {
        this.mAlbumItemOnClickListener = mAlbumItemOnClickListener;
    }
}
