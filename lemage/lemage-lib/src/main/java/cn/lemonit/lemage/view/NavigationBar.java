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

    private RightViewClickListener mRightViewClickListener;
    private LeftViewClickListener mLeftViewClickListener;

    private AlbumSelectButton albumSelectButton;

    public NavigationBar(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context){
        addLeftView(context);
        addRightView(context);
    }

    private void addLeftView(Context context) {
        albumSelectButton = new AlbumSelectButton(context, Color.WHITE);
        RelativeLayout.LayoutParams buttonLayoutParams = new RelativeLayout.LayoutParams(
                ScreenUtil.dp2px(context, 200), ViewGroup.LayoutParams.MATCH_PARENT
        );
        buttonLayoutParams.addRule(RelativeLayout.ALIGN_LEFT, RelativeLayout.TRUE);
        buttonLayoutParams.leftMargin = ScreenUtil.dp2px(context, 14);
        albumSelectButton.setLayoutParams(buttonLayoutParams);
        setLeftViewClickListener(albumSelectButton, context);
        this.addView(albumSelectButton);
    }

    private void addRightView(Context context) {
        RelativeLayout.LayoutParams textLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        textLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        TextView cancelTextView = new TextView(context);
        cancelTextView.setTextColor(Color.WHITE);
        cancelTextView.setTextSize(ScreenUtil.dp2px(context, 10));
        cancelTextView.setText("取消");
        cancelTextView.setGravity(Gravity.CENTER_VERTICAL);
        cancelTextView.setPadding(ScreenUtil.dp2px(context, 14), 0, ScreenUtil.dp2px(context, 14), 0);
        cancelTextView.setLayoutParams(textLayoutParams);
        setRightViewClickListener(cancelTextView, context);
        this.addView(cancelTextView);
    }

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

    private void setRightViewClickListener(final TextView view, final Context context) {
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        view.setTextColor(Color.RED);
                        break;
                    case MotionEvent.ACTION_UP:
                        view.setTextColor(Color.WHITE);
                        mRightViewClickListener.rightClickListener();
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        view.setTextColor(Color.WHITE);
                        break;
                }
                return true;
            }
        });
    }

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
}
