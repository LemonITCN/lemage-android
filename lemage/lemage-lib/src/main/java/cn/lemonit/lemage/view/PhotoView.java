package cn.lemonit.lemage.view;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * 照片列表中的照片展示控件
 *
 * @author liuri
 */
public class PhotoView extends RelativeLayout {

    private ImageView imageView;

    public PhotoView(Context context) {
        super(context);
        init();
    }

    private void init() {
        addView(getImageView());
    }

    public ImageView getImageView() {
        if (imageView == null) {
            imageView = new ImageView(getContext());
            imageView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }
        return imageView;
    }
}
