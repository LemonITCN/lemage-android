package cn.lemonit.lemage.lemageutil;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.UUID;

import cn.lemonit.lemage.bean.ImageSize;

/**
 * 沙箱文件管理类
 * @author: zhaoguangyang
 */
public class SendBoxFileManager {

    private final String TAG = "SendBoxFileManager";

    private Context mContext;

    // 长缓存文件夹
    private File longTermFile;
    // 短缓存文件夹
    private File shortTermFile;

    // 长缓存文件夹名称后缀
    private final String longTermFileName = "/longTermPicture";
    // 短缓存文件夹名称后缀
    private final String shortTermFileName = "/shortTermPicture";

    // 文件夹的根目录
    private String baseFileUrl = "/storage/emulated/0/Android/data/";

    // 应用程序的全包名
    private String packageName;

    // 最终保存在文件夹中的文件的前缀名
    private final String sendBoxFilePrefixName = "lemage://sandbox/";

    // 长缓存文件夹路径
    private final String longTermURL = "/storage/emulated/0/Android/data/cn.lemonit.lemage_example/longTermPicture";
    // 短缓存文件夹路径
    private final String shortTermURL = "/storage/emulated/0/Android/data/cn.lemonit.lemage_example/shortTermPicture";

    private static SendBoxFileManager instance;

    private SendBoxFileManager(Context context){
        mContext = context;
    }

    public static synchronized SendBoxFileManager getInstance(Context context) {
        if(instance == null) {
            instance = new SendBoxFileManager(context);
        }
        return instance;
    }

    /**
     * 传递一个bitmap得到URL(分为长缓存和短缓存)
     * @param bitmap
     * @param longTerm
     * @return
     */
    public String generateLemageUrl(Bitmap bitmap, boolean longTerm) {
        // 1 找到或者创建持久缓存文件夹或者非持久缓存文件夹
        File file = getFile(longTerm);
        // 2 把bitmap转成二进制数据
        byte[] bitmapData = bitmap2byte(bitmap);
        // 3 把二进制数据存储在文件夹中，返回这个文件的名称
        return writeByteArray(bitmapData, file, longTerm);
    }

    /**
     * 根据url获得目标文件流
     * @param url
     * @return
     */
    public InputStream loadImageInputStream(String url) {
        ByteArrayInputStream bais = new ByteArrayInputStream(loadImageData(url));
        return bais;
    }

    /**
     * 根据URL获取指定文件的byte【】数据
     * @param url
     * @return
     */
    public byte[] loadImageData(String url) {
        byte[] data = null;
        File file = url2File(url);
        data = file2Byte(file);
        return data;
    }

    /**
     * 根据URL和size获取Bitmap
     * @param url
     * @param size
     * @return
     */
    public Bitmap loadImage(String url, ImageSize size) {
        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
        byte[] data = loadImageData(url);
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
        // 原图的宽高
        int bitmapWidth = options.outWidth;
        int bitmapHeight = options.outHeight;
        if(size == null) {
//            options.inJustDecodeBounds = false;
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
     * 根据URL获取Bitmap
     * @param url
     * @return
     */
    public Bitmap loadImage(String url) {
        byte[] data = loadImageData(url);
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        return bitmap;
    }

    /**
     * 清空所有的长缓存图片
     */
    public void expiredAllLongTermUrl() {
        File file = new File(longTermURL);
        if(file == null) return;
        if(!file.isDirectory()) return;
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            f.delete();
        }
    }

    /**
     * 清空所有的短缓存图片
     */
    public void expiredAllShortTermUrl() {
        File file = new File(shortTermURL);
        if(file == null) return;
        if(!file.isDirectory()) return;
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            f.delete();
        }
    }

    public void expiredUrl(String url) {
        if(TextUtils.isEmpty(url)) return;
        String suffixUrl = url.substring(url.lastIndexOf("/")+1);
        File file = null;
        String fileSuffixName = url.contains("long") ? "/longTermPicture" : "/shortTermPicture";  // 文件夹的后缀
        String fileName = baseFileUrl + SystemInfo.getApplicationPackageName(mContext) + fileSuffixName;
        Log.e(TAG, "fileName =========== " + fileName);
        file = new File(fileName);  // 目标文件夹
        if(!file.exists()) return;
        File[] files = file.listFiles();
        if(files.length == 0) return;
        for(File mFile : files) {
            String fileSuffixName_ = mFile.getName().substring(mFile.getName().lastIndexOf("/") + 1);
            if (mFile.isDirectory()) {
                break;
            }
            //  取UUID，即SuffixName进行比较，相同则是同一个文件，返回
            if(fileSuffixName_.equals(suffixUrl)) {
                mFile.delete();
                return;
            }
        }
    }

    /**
     * 获取对应的文件夹
     * @param longTerm
     * @return
     */
    private File getFile(boolean longTerm) {
        String path;
        String packageName = SystemInfo.getApplicationPackageName(mContext);
        if(longTerm) {
            path = baseFileUrl + packageName + longTermFileName;
        }else {
            path = baseFileUrl + packageName + shortTermFileName;
        }
        Log.e(TAG, "path ======== " + path);
        File file = new File(path);
        // 如果不存在就创建
        if(!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    /**
     * 把bitmap转换成byte[]
     * @param bitmap
     * @return
     */
    private byte[] bitmap2byte(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }

    /**
     * 把byte[]写进文件夹中
     * @param bytes
     * @param parentFile
     * @return
     */
    private String writeByteArray(byte[] bytes, File parentFile, boolean longTerm) {
        boolean result = false;
        // 保存的文件的全名
        String fileName;
        // 中间名称
        String centerName = longTerm ? "long/" : "short/";
        // 随机得到UUID, 作为文件后缀
        String suffixName = UUID.randomUUID().toString();
//        fileName = sendBoxFilePrefixName + centerName + suffixName;
//        fileName = suffixName;

        File file = new File(parentFile, suffixName);  // 需要保存的目标文件

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        fileName = sendBoxFilePrefixName + centerName + suffixName;
        return fileName;
    }

    /**
     * 根据URL获取到具体的文件
     * @param url
     * @return  File
     */
    private File url2File(String url) {
        String suffixUrl = url.substring(url.lastIndexOf("/")+1);   // 目标文件名
        File targetFile = null;  // 路径的文件
        // 根据路径找到对应的文件夹
        // lemage://sandbox/long/0257c5b7-b0d0-4779-bbdf-f011eff8c48c
        // /storage/emulated/0/Android/data/cn.lemonit.lemage_example/longTermPicture
        String fileSuffixName = url.contains("long") ? "/longTermPicture" : "/shortTermPicture";  // 文件夹的后缀
        String fileName = baseFileUrl + SystemInfo.getApplicationPackageName(mContext) + fileSuffixName;
        Log.e(TAG, "fileName =========== " + fileName);
        File file = new File(fileName);  // 目标文件夹
        if(!file.exists()) {
            System.out.println("没有找到文件夹");
            Log.e(TAG, "file.exists()");
            return null;
        }
        // 文件夹里面有文件就开始遍历，找到具体的文件
        File[] files = file.listFiles();
        if(files.length == 0) {
            Log.e(TAG, "文件夹是空的");
            return null;
        }
        for(File mFile : files) {
            String fileSuffixName_ = mFile.getName().substring(mFile.getName().lastIndexOf("/") + 1);
            if (mFile.isDirectory()) {
                break;
            }
            //  取UUID，即SuffixName进行比较，相同则是同一个文件，返回
            if(fileSuffixName_.equals(suffixUrl)) {
                targetFile = mFile;
                break;
            }
        }
        return targetFile;
    }


    /**
     * 文件转byte[]
     * @param file
     * @return
     */
    private byte[] file2Byte(File file) {
        byte[] buffer = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1)
            {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

}
