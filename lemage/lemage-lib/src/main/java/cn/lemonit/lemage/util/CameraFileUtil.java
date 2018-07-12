package cn.lemonit.lemage.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import cn.lemonit.lemage.bean.ImageSize;
import cn.lemonit.lemage.lemageutil.SystemInfo;

/**
 * @author zhaoguangyang
 */
public class CameraFileUtil {

    private Context mContext;
    // 最终保存在文件夹中的文件的前缀名
    private final String sendBoxFilePrefixName = "lemage://sandbox";
    // 根目录
    private static final String baseUrl = "/storage/emulated/0/Android/data/";
    // 照相机图片文件夹
    private static final String cameraPhotoName = "/camera/photo/";
    // 照相机视频文件夹
    private static final String cameraVideoName = "/camera/video/";
    // 应用程序包名
    private static String packageName;

    private CameraFileUtil(Context mContext) {
        this.mContext = mContext;
        packageName = SystemInfo.getApplicationPackageName(mContext);
    }

    private static CameraFileUtil instance;

    public static CameraFileUtil getInstance(Context context) {
        if(instance == null) {
            instance = new CameraFileUtil(context);
        }
        return instance;
    }

    /**
     * 拍照后保存临时文件，返回路径
     * @return
     */
    public String getPhotoPath(Bitmap bitmap) {
        File file = getFile(0);
        byte[] bitmapData = bitmap2byte(bitmap);
        return writeByteArray(bitmapData, file);
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
     * 根据URL获取指定文件的byte[]数据
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


    /**
     * 根据URL获取到具体的文件
     * @param url
     * @return  File
     */
    private File url2File(String url) {
        String suffixUrl = url.substring(url.lastIndexOf("/")+1);   // 目标文件名
        File targetFile = null;  // 路径的文件
        // 根据路径找到对应的文件夹
        // lemage://sandbox/storage/emulated/0/Android/data/cn.lemonit.lemage_example/camera/photo/0f144fb5-4ef6-4cea-8a98-2a3f57053d47
        String fileSuffixName = url.contains("photo") ? "/camera/photo/" : "/camera/video/";  // 文件夹的后缀
        String fileName = baseUrl + SystemInfo.getApplicationPackageName(mContext) + fileSuffixName;
        File file = new File(fileName);  // 目标文件夹
        if(!file.exists()) {
            System.out.println("没有找到文件夹");
            return null;
        }
        // 文件夹里面有文件就开始遍历，找到具体的文件
        File[] files = file.listFiles();
        if(files.length == 0) {
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
     * 获取拍照或者视频临时文件夹
     * @param type   0 photo  1 video
     * @return
     */
    public File getFile(int type) {
        String path;
        String packageName = SystemInfo.getApplicationPackageName(mContext);
        if(type == 0) {
            path = baseUrl + packageName + cameraPhotoName;
        }else {
            path = baseUrl + packageName + cameraVideoName;
        }
        File file = new File(path);
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
    private String writeByteArray(byte[] bytes, File parentFile) {
        // 保存的文件的全名
        String fileName;
        // 随机得到UUID, 作为文件后缀
        String suffixName = UUID.randomUUID().toString();
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
        fileName = sendBoxFilePrefixName + parentFile.getPath() + "/" + suffixName;
        return fileName;
    }

    /**
     * 拍摄视频的文件保存路径
     * @return
     */
    public String getPath(int action) {
        return getFile(action).getPath() + "/" + UUID.randomUUID().toString() + ".mp4";
    }
}
