package cn.lemonit.lemage.view;

import android.content.Context;
import android.graphics.Color;
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

    public PhotoView(Context context, int width) {
        super(context);
        init(width);
    }

    private void init(int width) {
        imageView = new ImageView(getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width, width);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        imageView.setBackgroundColor(Color.RED);
        imageView.setLayoutParams(params);
        addView(imageView);
    }

    public ImageView getImageView() {
        return imageView;
    }
}
