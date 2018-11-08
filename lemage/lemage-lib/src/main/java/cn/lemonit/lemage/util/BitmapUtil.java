package cn.lemonit.lemage.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.widget.Toast;

import com.lemorage.file.Lemorage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cn.lemonit.lemage.been.ImageSize;

/**
 * @author zhaoguangyang
 * @date 2018/10/31
 * Describe:
 */
public class BitmapUtil {

    /**
     * 拍照后，保存图片为二进制到short文件夹并返回沙盒路径
     * @param bitmap
     * @return
     */
    public static String bitmap2Url(Context context, Bitmap bitmap, boolean isLongTerm) {
        String path = null;
        byte[] data = bitmap2ByteArr(bitmap);
        path = Lemorage.put(data, isLongTerm, context);
        return path;
    }

    /**
     * 根据沙盒路径和size返回bitmap
     * @param url
     * @param size
     * @return
     */
    public static Bitmap url2Bitmap(String url, ImageSize size, Context context) {
        Bitmap bitmap = null;
//        byte[] data = url2ByteArr(url, context);
        byte[] data = Lemorage.getWithByteArr(url, context);
        if(data == null) {
            Toast.makeText(context, "找不到路径", Toast.LENGTH_SHORT).show();
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        // 原图的宽高
        int bitmapWidth = options.outWidth;
        int bitmapHeight = options.outHeight;
        if(size == null) {
            return bitmap;
        }
        // 目标的宽高
        int targetWidth = size.getWidth();
        int targetHeight = size.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) targetWidth) / bitmapWidth;
        float scaleHeight = ((float) targetHeight) / bitmapHeight;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmapNew = Bitmap.createBitmap(bitmap, 0, 0, (int) bitmapWidth, (int) bitmapHeight, matrix, true);
        return bitmapNew;
    }

    /**
     * Bitmap转byte[]
     * @param bitmap
     * @return
     */
    public static byte[] bitmap2ByteArr(Bitmap bitmap) {
        byte[] data = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        data = out.toByteArray();
        return data;
    }


}
