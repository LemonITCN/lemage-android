package cn.lemonit.lemage.util;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lemonit.lemage.bean.NetBeen;
import cn.lemonit.lemage.lemageutil.SystemInfo;

/**
 * URL工具类
 * @author zhaoguangyang
 */
public class PathUtil {

    private static String TAG = "PathUtil";

    private String baseFileUrl = "/storage/emulated/0/Android/data/";

    private Context mContext;

    private DownLoadFileFinishListener mDownLoadFileFinishListener;

//    private static PathUtil instance;

    public PathUtil(Context context){
        this.mContext = context;
    }

//    public static synchronized PathUtil getInstance(Context context) {
//        if(instance == null) {
//            instance = new PathUtil(context);
//        }
//        return instance;
//    }

    /**
     * 用户直接进入预览界面，传递的参数是URL
     * lemage://album/localImage/storage/emulated/0/Download/logo_index2.png
     * lemage://album/localVideo/storage/emulated/0/Download/logo_index2.MP4
     * lemage://sandbox/long/xxxxxxxxxxxxxxxx
     * http://
     * 1 判断是否是网络地址，如果是，就先把地址MD5转换后去网络图片文件夹和视频文件夹分别找，如果有就把路径返回进行显示或者播放
     *   如果没有，就先下载，下载后判断文件类型是图片还是视频，然后存储在对应的网络临时文件夹中，返回路径进行显示或者播放
     * 2 如果是本地路径，就解析lemage://格式的URL变成正常路径返回进行显示或者播放
     * @param path
     * @return
     */
    public NetBeen getNetBeen(String path) {
        NetBeen mNetBeen = new NetBeen();
        // 网络地址
        if(path.startsWith("http")) {
            // 把path转换成MD5(即文件名)
            String pathMD5 = stringToMD5(path);
            Log.e(TAG, "pathMD5 ================ " + pathMD5);
            // 包名
            String packageName = getPackageName();
            // 网络图片文件夹
            File filePhoto = new File(baseFileUrl + packageName + "/tmp/photo");
            if(!filePhoto.exists()) {
                filePhoto.mkdirs();
            }
            // 网络视频
            File fileVideo = new File(baseFileUrl + packageName + "/tmp/video");
            if(!fileVideo.exists()) {
                fileVideo.mkdirs();
            }
            // 先去网络图片文件夹进行查找文件,如果有这个文件，就返回路径，如果没有就查找网络视频文件夹
            String url = findFile(filePhoto, pathMD5);
            if(!TextUtils.isEmpty(url)) {
                mNetBeen.setType(0);
                mNetBeen.setPath(url);
                return mNetBeen;
            }else {
                url = findFile(fileVideo, pathMD5);
            }
            // 如果还是没有此文件，就下载
            if(TextUtils.isEmpty(url)) {
                new DownLoadAsyncTask().execute(path);
            }else {
                mNetBeen.setType(1);
                mNetBeen.setPath(url);
                return mNetBeen;
            }
        }
        // 本地图片地址
        else if(path.startsWith("lemage://album/localImage")){
            mNetBeen.setPath(path.substring("lemage://album/localImage".length()));
            mNetBeen.setType(0);
            return mNetBeen;
        }
        // 本地视频地址
        else if(path.startsWith("lemage://album/localVideo")) {
            mNetBeen.setPath(path.substring("lemage://album/localVideo".length()));
            mNetBeen.setType(1);
            return mNetBeen;
        }
        return mNetBeen;
    }


    /**
     * 下载图片，或者视频
     * String*********对应我们的URL类型
     * Integer********进度条的进度值
     * String*********下载后返回文件的路径
     *
     *
     */
    class DownLoadAsyncTask extends AsyncTask<String, Integer, NetBeen> {

        @Override
        protected NetBeen doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                //设置超时间为3秒
                conn.setConnectTimeout(3 * 1000);
                //防止屏蔽程序抓取而返回403错误
                conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
                //得到输入流
                InputStream inputStream = conn.getInputStream();
                Log.e(TAG, "下载中，得到了输入流   path ==== " + params[0]);

                //totalSize变量用来获得将要下载文件的总大小
                int totalSize = conn.getContentLength();

                //获取自己数组
//                byte[] data = readInputStream(inputStream);
                //获取目标网络文件的类型    image/jpeg     video/mp4
                String fileType = getFileType(conn);
                if(TextUtils.isEmpty(fileType)) {
                    // 应该默认当做视频先播放，如果出错就按图片显示（暂时不处理）
                }
                // 如果网络文件是图片，下载后就放到图片文件夹
                NetBeen mNetBeen = new NetBeen();
                if(fileType.startsWith("image/")) {
                    File filePhoto = new File(baseFileUrl + getPackageName() + "/tmp/photo");
                    File file = new File(filePhoto + "/" + stringToMD5(params[0]));  // 需要保持的文件
//                    saveFile(filePhoto, file, data, inputStream);
                    saveFileNew(filePhoto, file, inputStream, totalSize);
                    mNetBeen.setPath(file.getPath());
                    mNetBeen.setType(0);
                }else {
                    File fileVideo = new File(baseFileUrl + getPackageName() + "/tmp/video");
                    File file = new File(fileVideo + "/" + stringToMD5(params[0]));  // 需要保持的文件
//                    saveFile(fileVideo, file, data, inputStream);
                    saveFileNew(fileVideo, file, inputStream, totalSize);
                    mNetBeen.setPath(file.getPath());
                    mNetBeen.setType(0);
                }
                return mNetBeen;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }


        /**
         * 下载完成回调
         * @param mNetBeen
         */
        @Override
        protected void onPostExecute(NetBeen mNetBeen) {
            super.onPostExecute(mNetBeen);
            mDownLoadFileFinishListener.downLoadFileFinish(mNetBeen);
        }

        /**
         * 更新进度
         * @param values
         */
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }


    /**
     * 将字符串转成MD5值
     * @param string 需要转换的字符串
     * @return 字符串的MD5值
     */
    private String stringToMD5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }

    /**
     * 根据文件名称在指定文件夹中查找
     */
    private String findFile(File file, String fileName) {
        String path = "";
        File[] files = file.listFiles();  // 得到文件夹中的所有文件
        if(files.length == 0) {
            Log.e(TAG, "文件夹是空的");
            return path;
        }
        for(File mFile : files) {
            if(mFile.getName().equals(fileName)) {
                path = file.getPath() + "/" + fileName;
                return path;
            }
        }
        return path;
    }


    /**
     * 从输入流中获取字节数组
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        inputStream.close();
        return bos.toByteArray();
    }


    /**
     * 下载文件时，根据响应头获取文件类型
     * @param conn
     * @return
     */
    private String getFileType(HttpURLConnection conn) {
        String fileType = "";
        Map<String, List<String>> responseHeaderMap = conn.getHeaderFields();
        int size = responseHeaderMap.size();
        for(int i = 0; i < size; i++) {
            String responseHeaderKey = conn.getHeaderFieldKey(i);
            if(!TextUtils.isEmpty(responseHeaderKey) && responseHeaderKey.equals("Content-Type")) {
                fileType = conn.getHeaderField(i);
                return fileType;
            }
        }
        return fileType;
    }

    /**
     * 获取应用程序包名
     * @return
     */
    private String getPackageName() {
        return SystemInfo.getApplicationPackageName(mContext);
    }


    /**
     * 保存文件
     * @param parentFile
     * @param file
     * @param data
     */
    private void saveFile(File parentFile, File file, byte[] data, InputStream inputStream) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(data);
            if (fos != null) {
                fos.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private void saveFileNew(File parentFile, File file, InputStream inputStream, int totalSize) {
        int currentSize = 0;
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
                currentSize += bytesRead;
                int progressSize = (int)(currentSize/(double)totalSize * 100);
                Log.e(TAG, "进度 ============= " + progressSize);
            }
            os.close();
            inputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public interface DownLoadFileFinishListener {
        void downLoadFileFinish(NetBeen netBeen);
    }

    public void setDownLoadFileFinishListener(DownLoadFileFinishListener mDownLoadFileFinishListener) {
        this.mDownLoadFileFinishListener = mDownLoadFileFinishListener;
    }
}
