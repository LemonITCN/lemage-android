package cn.lemonit.lemage.view;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.lemonit.lemage.util.ScreenUtil;

/**
 * 导航条控件
 *
 * @author LemonIT.CN
 */
public class NavigationBar extends RelativeLayout {

    private final String TAG = "NavigationBar";
    /**
     * 不同的activit的顶部条里面的控件不同，0 选择器顶部条，1 预览界面顶部条
     */
    private int mStyle;

    private RightViewClickListener mRightViewClickListener;
    private LeftViewClickListener mLeftViewClickListener;

    private PreviewLeftViewClickListener mPreviewLeftViewClickListener;
    private PreviewRightViewClickListener mPreviewRightViewClickListener;

    /**
     * 选择器的左侧按钮
     */
    private AlbumSelectButton albumSelectButton;
    /**
     * 预览时的左侧按钮
     */
    private PreviewBarLeftButton mPreviewBarLeftButton;
    /**
     * 预览时的右侧按钮
     */
    private CircleView mCircleView;

    private int mColor;

    public NavigationBar(Context context, int style, int color) {
        super(context);
        mStyle = style;
        mColor = color;
        init(context);
    }

    private void init(Context context){
        addLeftView(context);
        addRightView(context);
    }

    private void addLeftView(Context context) {
        if(mStyle == 0) {
            albumSelectButton = new AlbumSelectButton(context, Color.WHITE);
            RelativeLayout.LayoutParams buttonLayoutParams = new RelativeLayout.LayoutParams(
                    ScreenUtil.dp2px(context, 120), ViewGroup.LayoutParams.MATCH_PARENT
            );
            buttonLayoutParams.addRule(RelativeLayout.ALIGN_LEFT, RelativeLayout.TRUE);
            buttonLayoutParams.leftMargin = ScreenUtil.dp2px(context, 14);
            albumSelectButton.setLayoutParams(buttonLayoutParams);
            setLeftViewClickListener(albumSelectButton, context);
            this.addView(albumSelectButton);
        }else {
            mPreviewBarLeftButton = new PreviewBarLeftButton(context);
            RelativeLayout.LayoutParams previewLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            previewLayoutParams.addRule(RelativeLayout.ALIGN_LEFT, RelativeLayout.TRUE);
            previewLayoutParams.leftMargin = ScreenUtil.dp2px(context, 14);
            mPreviewBarLeftButton.setLayoutParams(previewLayoutParams);
            mPreviewBarLeftButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPreviewLeftViewClickListener.leftClickListener(mPreviewBarLeftButton);
                }
            });
            this.addView(mPreviewBarLeftButton);
        }
    }

    private void addRightView(Context context) {
        if(mStyle == 0) {
            RelativeLayout.LayoutParams textLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
            textLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            DrawTextButton cancelButton = new DrawTextButton(context, "取消");
            cancelButton.setPadding(ScreenUtil.dp2px(context, 14), 0, ScreenUtil.dp2px(context, 14), 0);
            cancelButton.setLayoutParams(textLayoutParams);
            setRightViewClickListener(cancelButton, context);
            this.addView(cancelButton);
        }else {
            int mRadius = ScreenUtil.dp2px(context, 16);
            RelativeLayout.LayoutParams paramsCircle = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            paramsCircle.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
            paramsCircle.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            paramsCircle.topMargin = mRadius / 2;
            paramsCircle.rightMargin = ScreenUtil.dp2px(context, 14);
            mCircleView = new CircleView(context, mRadius, mColor);
            // 初始状态
//            mCircleView.changeStatus(1, 1);
            mCircleView.setLayoutParams(paramsCircle);
            mCircleView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPreviewRightViewClickListener.rightClickListener(mCircleView);
                }
            });
            this.addView(mCircleView);
        }
    }

    /**
     * 选择器顶部条事件
     * @param view
     * @param context
     */
    private void setLeftViewClickListener(final AlbumSelectButton view, final Context context) {
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        view.changeTextColor(Color.RED);
                        break;
                    case MotionEvent.ACTION_UP:
                        view.changeTextColor(Color.WHITE);
                        mLeftViewClickListener.leftClickListener(view);
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        view.changeTextColor(Color.WHITE);
                        break;
                }
                return true;
            }
        });
    }

    private void setRightViewClickListener(final DrawTextButton view, final Context context) {
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        view.changeTextColor(Color.RED);
                        break;
                    case MotionEvent.ACTION_UP:
                        view.changeTextColor(Color.WHITE);
                        mRightViewClickListener.rightClickListener();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        view.changeTextColor(Color.WHITE);
                        break;
                }
                return true;
            }
        });
    }


    /**
     * 选择器顶部条点击事件
     */
    public interface RightViewClickListener {
        void rightClickListener();
    }

    public interface LeftViewClickListener {
        void leftClickListener(AlbumSelectButton view);
    }

    public void setRightViewClickListener(RightViewClickListener mRightViewClickListener) {
        this.mRightViewClickListener = mRightViewClickListener;
    }

    public void setLeftViewClickListener(LeftViewClickListener mLeftViewClickListener) {
        this.mLeftViewClickListener = mLeftViewClickListener;
    }

    public AlbumSelectButton getAlbumSelectButton() {
        return albumSelectButton;
    }

    public interface PreviewRightViewClickListener {
        void rightClickListener(CircleView view);
    }

    public interface PreviewLeftViewClickListener {
        void leftClickListener(PreviewBarLeftButton view);
    }

    public void setPreviewLeftViewClickListener(PreviewLeftViewClickListener mPreviewLeftViewClickListener) {
        this.mPreviewLeftViewClickListener = mPreviewLeftViewClickListener;
    }

    public void setPreviewRightViewClickListener(PreviewRightViewClickListener mPreviewRightViewClickListener) {
        this.mPreviewRightViewClickListener = mPreviewRightViewClickListener;
    }

    public PreviewBarLeftButton getPreviewBarLeftButton() {
        if(mStyle == 1) {
            return mPreviewBarLeftButton;
        }
        return null;
    }

    public CircleView getCircleView() {
        if(mStyle == 1) {
            return mCircleView;
        }
        return null;
    }

    public void changeText(int mCount, int corrent) {
        mPreviewBarLeftButton.changeText(mCount, corrent);
    }

    /**
     *
     * @param mStatus   状态：0 灰色   1 号码高亮  2 对号
     * @param number  状态是1的时候显示的号码
     */
    public void changeTextCircle(int mStatus, int number) {
        mCircleView.changeStatus(mStatus, number);
    }
}
