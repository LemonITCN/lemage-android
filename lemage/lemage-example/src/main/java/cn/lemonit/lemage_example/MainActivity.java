package cn.lemonit.lemage_example;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.lemonit.lemage.Lemage;
import cn.lemonit.lemage.activity.LemageActivity;
import cn.lemonit.lemage.activity.PreviewActivity;
import cn.lemonit.lemage.bean.AlbumNew;
import cn.lemonit.lemage.bean.FileObj;
import cn.lemonit.lemage.bean.ImageSize;
import cn.lemonit.lemage.bean.Video;
import cn.lemonit.lemage.core.LemageScanner;
import cn.lemonit.lemage.core.LemageScannerNew;
import cn.lemonit.lemage.interfaces.LemageResultCallback;
import cn.lemonit.lemage.interfaces.ScanCompleteCallback;
import cn.lemonit.lemage.interfaces.VideoScanCompleteCallback;
import cn.lemonit.lemage.util.PathUtil;
import cn.lemonit.lemage.view.VideoStartImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "MainActivity";

    private Button selectPhoto;

    private ImageView image;

    private String path;

    private RelativeLayout layout;

    private String MP4_path;
    private long MP4_size;

    /****************************/
    private Button selectVideo, selectAll, testType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(android.support.v7.appcompat.R.style.Theme_AppCompat_DayNight_NoActionBar);
        setContentView(R.layout.activity_main);
        initView();
        initData();
    }

    private void initView() {
        selectPhoto = findViewById(R.id.selectPhoto);
        selectPhoto.setOnClickListener(this);
        selectVideo = findViewById(R.id.selectVideo);
        selectVideo.setOnClickListener(this);
        selectAll = findViewById(R.id.selectAll);
        selectAll.setOnClickListener(this);
        image = findViewById(R.id.image);

        testType = findViewById(R.id.testType);
        testType.setOnClickListener(this);
    }

    private void initData() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.test);
        if(bitmap != null) {
            path = Lemage.generateLemageUrl(this, bitmap, true);
            Log.e("MainActivity", "path ==== " + path);
        }else {
            Log.e("MainActivity", "bitmap ==== null");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selectPhoto:
//                Lemage.startChooser(MainActivity.this, 5, false, R.color.colorPrimaryDark, LemageScanner.STYLE_ONLY_PICTURE, null);

//                actionVideo();
//                addView();
                Lemage.startChooser(this, 100, false, 0, LemageScanner.STYLE_ONLY_PHOTO, null);
                break;
            case R.id.selectVideo:
                Lemage.startChooser(this, 100, false, 0, LemageScanner.STYLE_ONLY_VIDEO, null);
                break;
            case R.id.selectAll:
                Lemage.startChooser(this, 100, false, 0, LemageScanner.STYLE_ALL, null);
                break;
            // 获取文件类型
            case R.id.testType:
//                String pathN = "https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=3742173999,1643624888&fm=27&gp=0.jpg";
//                String pathN = "http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4";
//                PathUtil.getInstance(this).getUrl(pathN);
//                setImageViewBitMap(image, pathN);
                PreviewActivity.setCallback(new LemageResultCallback() {
                    @Override
                    public void willClose(List<String> imageUrlList, boolean isOriginal, List<FileObj> list) {

                    }

                    @Override
                    public void closed(List<String> imageUrlList, boolean isOriginal) {

                    }
                });
                ArrayList<String> listUrlAll = new ArrayList<String>();   // 全部文件路径
                ArrayList<String> listUrlSelect = new ArrayList<String>();  // 已经选择文件路径
                listUrlAll.add("lemage://album/localImage/storage/emulated/0/Download/2e2eb9389b504fc2b3b590d7efdde71191ef6d7d.jpeg");
                listUrlAll.add("lemage://album/localVideo/storage/emulated/0/DCIM/Camera/20180629_131558.mp4");
                listUrlAll.add("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=3742173999,1643624888&fm=27&gp=0.jpg");
//                listUrlAll.add("http://wxsnsdy.tc.qq.com/105/20210/snsdyvideodownload?filekey=30280201010421301f0201690402534804102ca905ce620b1241b726bc41dcff44e00204012882540400&bizid=1023&hy=SH&fileparam=302c020101042530230204136ffd93020457e3c4ff02024ef202031e8d7f02030f42400204045a320a0201000400");
//                listUrlAll.add("http://clips.vorwaerts-gmbh.de/big_buck_bunny.mp4");
                listUrlSelect.addAll(listUrlAll);
                Intent intent = new Intent(this, PreviewActivity.class);
                intent.putStringArrayListExtra("listAll", listUrlAll);
                intent.putStringArrayListExtra("listSelect", listUrlSelect);
                startActivity(intent);
                break;
        }
    }

    Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what == 1){
                image.setImageBitmap(bitmap_);
            }
        }
    };

    private Bitmap bitmap_;

    /**
     * 加载图片ImageView
     * @param url 图片网络地址
     * @return
     */
    public void setImageViewBitMap(ImageView imageView, final String url) {

        new Thread() {
            @Override
            public void run() {
                URL myFileUrl = null;
                Bitmap bitmap = null;
                try {
                    myFileUrl = new URL(url);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    HttpURLConnection conn = (HttpURLConnection) myFileUrl.openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream is = conn.getInputStream();
                    String responseHeader = getResponseHeader(conn);
//                    bitmap = BitmapFactory.decodeStream(is);
                    is.close();
//                    String responseHeader = getResponseHeader(conn);
//                    Log.e(TAG, "responseHeader ====================== " + responseHeader);
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                bitmap_ = bitmap;
//                myHandler.sendEmptyMessage(1);
            }
        }.start();
    }




    class NetworkAsyncTask extends AsyncTask<String, Integer, Map<String, Object>> {
        //NETWORK_GET表示发送GET请求
        public static final String NETWORK_GET = "NETWORK_GET";
        //NETWORK_POST_KEY_VALUE表示用POST发送键值对数据
        public static final String NETWORK_POST_KEY_VALUE = "NETWORK_POST_KEY_VALUE";
        //NETWORK_POST_XML表示用POST发送XML数据
        public static final String NETWORK_POST_XML = "NETWORK_POST_XML";
        //NETWORK_POST_JSON表示用POST发送JSON数据
        public static final String NETWORK_POST_JSON = "NETWORK_POST_JSON";

        @Override
        protected Map<String, Object> doInBackground(String... params) {
            Map<String,Object> result = new HashMap<>();
            URL url = null;//请求的URL地址
            HttpURLConnection conn = null;
            String requestHeader = null;//请求头
            byte[] requestBody = null;//请求体
            String responseHeader = null;//响应头
            byte[] responseBody = null;//响应体
            String action = params[0];//http请求的操作类型

            try {
                if (NETWORK_GET.equals(action)) {
                    //发送GET请求
//                    url = new URL("http://192.168.31.200:8080/HttpServer/MyServlet?name=孙群&age=27");
                    url = new URL("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1530615089571&di=15e20db32c869a24b46d0cdf835aedaa&imgtype=0&src=http%3A%2F%2Fpic.58pic.com%2F58pic%2F15%2F14%2F89%2F07758PIChvH_1024.jpg");
                    conn = (HttpURLConnection) url.openConnection();
                    //HttpURLConnection默认就是用GET发送请求，所以下面的setRequestMethod可以省略
                    conn.setRequestMethod("GET");
                    //HttpURLConnection默认也支持从服务端读取结果流，所以下面的setDoInput也可以省略
                    conn.setDoInput(true);
                    //用setRequestProperty方法设置一个自定义的请求头:action，由于后端判断
                    conn.setRequestProperty("action", NETWORK_GET);
                    //禁用网络缓存
                    conn.setUseCaches(false);
                    //获取请求头
                    requestHeader = getReqeustHeader(conn);
                    //在对各种参数配置完成后，通过调用connect方法建立TCP连接，但是并未真正获取数据
                    //conn.connect()方法不必显式调用，当调用conn.getInputStream()方法时内部也会自动调用connect方法
                    conn.connect();
                    //调用getInputStream方法后，服务端才会收到请求，并阻塞式地接收服务端返回的数据
                    InputStream is = conn.getInputStream();
                    //将InputStream转换成byte数组,getBytesByInputStream会关闭输入流
                    responseBody = getBytesByInputStream(is);
                    //获取响应头
                    responseHeader = getResponseHeader(conn);
                    Log.e(TAG, "responseHeader =================== " + responseHeader);
                } else if (NETWORK_POST_KEY_VALUE.equals(action)) {
                    //用POST发送键值对数据
                    url = new URL("http://192.168.31.200:8080/HttpServer/MyServlet");
                    conn = (HttpURLConnection) url.openConnection();
                    //通过setRequestMethod将conn设置成POST方法
                    conn.setRequestMethod("POST");
                    //调用conn.setDoOutput()方法以显式开启请求体
                    conn.setDoOutput(true);
                    //用setRequestProperty方法设置一个自定义的请求头:action，由于后端判断
                    conn.setRequestProperty("action", NETWORK_POST_KEY_VALUE);
                    //获取请求头
                    requestHeader = getReqeustHeader(conn);
                    //获取conn的输出流
                    OutputStream os = conn.getOutputStream();
                    //获取两个键值对name=孙群和age=27的字节数组，将该字节数组作为请求体
                    requestBody = new String("name=孙群&age=27").getBytes("UTF-8");
                    //将请求体写入到conn的输出流中
                    os.write(requestBody);
                    //记得调用输出流的flush方法
                    os.flush();
                    //关闭输出流
                    os.close();
                    //当调用getInputStream方法时才真正将请求体数据上传至服务器
                    InputStream is = conn.getInputStream();
                    //获得响应体的字节数组
                    responseBody = getBytesByInputStream(is);
                    //获得响应头
                    responseHeader = getResponseHeader(conn);
                } else if (NETWORK_POST_XML.equals(action)) {
                    //用POST发送XML数据
                    url = new URL("http://192.168.31.200:8080/HttpServer/MyServlet");
                    conn = (HttpURLConnection) url.openConnection();
                    //通过setRequestMethod将conn设置成POST方法
                    conn.setRequestMethod("POST");
                    //调用conn.setDoOutput()方法以显式开启请求体
                    conn.setDoOutput(true);
                    //用setRequestProperty方法设置一个自定义的请求头:action，由于后端判断
                    conn.setRequestProperty("action", NETWORK_POST_XML);
                    //获取请求头
                    requestHeader = getReqeustHeader(conn);
                    //获取conn的输出流
                    OutputStream os = conn.getOutputStream();
                    //读取assets目录下的person.xml文件，将其字节数组作为请求体
                    requestBody = getBytesFromAssets("person.xml");
                    //将请求体写入到conn的输出流中
                    os.write(requestBody);
                    //记得调用输出流的flush方法
                    os.flush();
                    //关闭输出流
                    os.close();
                    //当调用getInputStream方法时才真正将请求体数据上传至服务器
                    InputStream is = conn.getInputStream();
                    //获得响应体的字节数组
                    responseBody = getBytesByInputStream(is);
                    //获得响应头
                    responseHeader = getResponseHeader(conn);
                } else if (NETWORK_POST_JSON.equals(action)) {
                    //用POST发送JSON数据
                    url = new URL("http://192.168.31.200:8080/HttpServer/MyServlet");
                    conn = (HttpURLConnection) url.openConnection();
                    //通过setRequestMethod将conn设置成POST方法
                    conn.setRequestMethod("POST");
                    //调用conn.setDoOutput()方法以显式开启请求体
                    conn.setDoOutput(true);
                    //用setRequestProperty方法设置一个自定义的请求头:action，由于后端判断
                    conn.setRequestProperty("action", NETWORK_POST_JSON);
                    //获取请求头
                    requestHeader = getReqeustHeader(conn);
                    //获取conn的输出流
                    OutputStream os = conn.getOutputStream();
                    //读取assets目录下的person.json文件，将其字节数组作为请求体
                    requestBody = getBytesFromAssets("person.json");
                    //将请求体写入到conn的输出流中
                    os.write(requestBody);
                    //记得调用输出流的flush方法
                    os.flush();
                    //关闭输出流
                    os.close();
                    //当调用getInputStream方法时才真正将请求体数据上传至服务器
                    InputStream is = conn.getInputStream();
                    //获得响应体的字节数组
                    responseBody = getBytesByInputStream(is);
                    //获得响应头
                    responseHeader = getResponseHeader(conn);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                //最后将conn断开连接
                if (conn != null) {
                    conn.disconnect();
                }
            }

            result.put("url", url.toString());
            result.put("action", action);
            result.put("requestHeader", requestHeader);
            result.put("requestBody", requestBody);
            result.put("responseHeader", responseHeader);
            result.put("responseBody", responseBody);
            return result;
        }

        @Override
        protected void onPostExecute(Map<String, Object> result) {
            super.onPostExecute(result);
            String url = (String)result.get("url");//请求的URL地址
            String action = (String) result.get("action");//http请求的操作类型
            String requestHeader = (String) result.get("requestHeader");//请求头
            byte[] requestBody = (byte[]) result.get("requestBody");//请求体
            String responseHeader = (String) result.get("responseHeader");//响应头
            byte[] responseBody = (byte[]) result.get("responseBody");//响应体

            //更新tvUrl，显示Url
//            tvUrl.setText(url);

            //更新tvRequestHeader，显示请求头
            if (requestHeader != null) {
//                tvRequestHeader.setText(requestHeader);
            }

            //更新tvRequestBody，显示请求体
            if(requestBody != null){
                try{
                    String request = new String(requestBody, "UTF-8");
//                    tvRequestBody.setText(request);
                }catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                }
            }

            //更新tvResponseHeader，显示响应头
            if (responseHeader != null) {
//                tvResponseHeader.setText(responseHeader);
            }

            //更新tvResponseBody，显示响应体
//            if (NETWORK_GET.equals(action)) {
//                String response = getStringByBytes(responseBody);
//                tvResponseBody.setText(response);
//            } else if (NETWORK_POST_KEY_VALUE.equals(action)) {
//                String response = getStringByBytes(responseBody);
//                tvResponseBody.setText(response);
//            } else if (NETWORK_POST_XML.equals(action)) {
//                //将表示xml的字节数组进行解析
//                String response = parseXmlResultByBytes(responseBody);
//                tvResponseBody.setText(response);
//            } else if (NETWORK_POST_JSON.equals(action)) {
//                //将表示json的字节数组进行解析
//                String response = parseJsonResultByBytes(responseBody);
//                tvResponseBody.setText(response);
//            }
        }

        //读取请求头
        private String getReqeustHeader(HttpURLConnection conn) {
            //https://github.com/square/okhttp/blob/master/okhttp-urlconnection/src/main/java/okhttp3/internal/huc/HttpURLConnectionImpl.java#L236
            Map<String, List<String>> requestHeaderMap = conn.getRequestProperties();
            Iterator<String> requestHeaderIterator = requestHeaderMap.keySet().iterator();
            StringBuilder sbRequestHeader = new StringBuilder();
            while (requestHeaderIterator.hasNext()) {
                String requestHeaderKey = requestHeaderIterator.next();
                String requestHeaderValue = conn.getRequestProperty(requestHeaderKey);
                sbRequestHeader.append(requestHeaderKey);
                sbRequestHeader.append(":");
                sbRequestHeader.append(requestHeaderValue);
                sbRequestHeader.append("\n");
            }
            return sbRequestHeader.toString();
        }

        //读取响应头
        private String getResponseHeader(HttpURLConnection conn) {
            Map<String, List<String>> responseHeaderMap = conn.getHeaderFields();
            int size = responseHeaderMap.size();
            StringBuilder sbResponseHeader = new StringBuilder();
            for(int i = 0; i < size; i++){
                String responseHeaderKey = conn.getHeaderFieldKey(i);
                String responseHeaderValue = conn.getHeaderField(i);
                sbResponseHeader.append(responseHeaderKey);
                sbResponseHeader.append(":");
                sbResponseHeader.append(responseHeaderValue);
                sbResponseHeader.append("\n");
            }
            return sbResponseHeader.toString();
        }

        //根据字节数组构建UTF-8字符串
        private String getStringByBytes(byte[] bytes) {
            String str = "";
            try {
                str = new String(bytes, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return str;
        }

        //从InputStream中读取数据，转换成byte数组，最后关闭InputStream
        private byte[] getBytesByInputStream(InputStream is) {
            byte[] bytes = null;
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(baos);
            byte[] buffer = new byte[1024 * 8];
            int length = 0;
            try {
                while ((length = bis.read(buffer)) > 0) {
                    bos.write(buffer, 0, length);
                }
                bos.flush();
                bytes = baos.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return bytes;
        }

        //根据文件名，从asserts目录中读取文件的字节数组
        private byte[] getBytesFromAssets(String fileName){
            byte[] bytes = null;
            AssetManager assetManager = getAssets();
            InputStream is = null;
            try{
                is = assetManager.open(fileName);
                bytes = getBytesByInputStream(is);
            }catch (IOException e){
                e.printStackTrace();
            }
            return bytes;
        }

        //将表示xml的字节数组进行解析
        private String parseXmlResultByBytes(byte[] bytes) {
            InputStream is = new ByteArrayInputStream(bytes);
            StringBuilder sb = new StringBuilder();
//            List<Person> persons = XmlParser.parse(is);
//            for (Person person : persons) {
//                sb.append(person.toString()).append("\n");
//            }
            return sb.toString();
        }

        //将表示json的字节数组进行解析
        private String parseJsonResultByBytes(byte[] bytes){
//            String jsonString = getStringByBytes(bytes);
//            List<Person> persons = JsonParser.parse(jsonString);
//            StringBuilder sb = new StringBuilder();
//            for (Person person : persons) {
//                sb.append(person.toString()).append("\n");
//            }
//            return sb.toString();
            return "";
        }

    }


    //读取响应头
    private String getResponseHeader(HttpURLConnection conn) {
        Map<String, List<String>> responseHeaderMap = conn.getHeaderFields();
        int size = responseHeaderMap.size();
        StringBuilder sbResponseHeader = new StringBuilder();
        for(int i = 0; i < size; i++){
            String responseHeaderKey = conn.getHeaderFieldKey(i);
            String responseHeaderValue = conn.getHeaderField(i);
            sbResponseHeader.append(responseHeaderKey);
            sbResponseHeader.append(":");
            sbResponseHeader.append(responseHeaderValue);
            sbResponseHeader.append("\n");
            if(!TextUtils.isEmpty(responseHeaderKey) && responseHeaderKey.equals("Content-Type")) {
                Log.e(TAG, "类型是 ==================== " + responseHeaderValue);
            }
        }
        return sbResponseHeader.toString();
    }
}
