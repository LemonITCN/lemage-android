package cn.lemonit.lemage.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.widget.CompoundButton;

import cn.lemonit.lemage.util.ScreenUtil;

/**
 * 左上角相册选择按钮
 *
 * @author LemonIT.CN
 */
public class AlbumSelectButton extends CompoundButton {

    /**
     * 按钮的文本文字
     */
    private String text;
    /**
     * 控件主体颜色
     */
    private int color;
    /**
     * 图标画笔
     */
    private Paint defaultPaint;
    /**
     * 左侧小图标的宽度
     */
    private int iconWidth;
    /**
     * 左侧小图标的高度
     */
    private int iconHeight;

    public AlbumSelectButton(Context context) {
        super(context);
        this.color = Color.WHITE;
        this.setBackgroundColor(Color.BLACK);
    }

    public AlbumSelectButton(Context context, int color) {
        super(context);
        this.color = color;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Path iconPath = new Path();
        int startY = getHeight() / 2 - getIconHeight() / 2;
        iconPath.moveTo(0, startY);
        iconPath.lineTo(getIconWidth() / 2, getHeight() / 2 + getIconHeight() / 2);
        iconPath.lineTo(getIconWidth(), getHeight() / 2 - getIconHeight() / 2);
        iconPath.close();
        canvas.drawPath(iconPath, getDefaultPaint());
        canvas.drawText("全部图片", getIconWidth() + ScreenUtil.dp2px(getContext(), 7), getHeight() / 2 + 8, getDefaultPaint());
    }

    public Paint getDefaultPaint() {
        if (defaultPaint == null) {
            defaultPaint = new Paint();
            defaultPaint.setColor(color);
            defaultPaint.setStrokeWidth(3);
            defaultPaint.setTextSize(ScreenUtil.dp2px(getContext(), 16));
        }
        return defaultPaint;
    }

    public int getIconWidth() {
        if (iconWidth == 0) {
            iconWidth = ScreenUtil.dp2px(getContext(), 12);
        }
        return iconWidth;
    }

    public int getIconHeight() {
        if (iconHeight == 0) {
            iconHeight = ScreenUtil.dp2px(getContext(), 8);
        }
        return iconHeight;
    }
}
