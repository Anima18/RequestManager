package com.example.webserviceutil.OkHttp;

import android.content.Context;
import android.util.Log;

import com.example.webserviceutil.SubscriptionManager;
import com.example.webserviceutil.entity.FileObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Headers;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * OKHttp封装类，封装get和post请求方法
 * @author 简建鸿 2016/6/3
 * @version 1.0
 */
public class OkHttpUtils {

    private final static OkHttpClient client ;
    /**
     * 已上载文件大小
     */
    private static Long totalReadLength;
    /**
     * 所有文件大小
     */
    private static Long totalFilesLength;

    static {
        client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 获取一个get请求
     * @param url 请求url
     * @return call
     * @throws IOException
     */
    public static Call get(Context context, String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .tag(context)
                .build();

        return client.newCall(request);
    }

    /**
     * 获取一个post请求，这个请求包含多块请求体，可以上传多个普通参数和多个文件参数。
     * 如果有上传文件，可以通过ProgressCallBack回调得到文件上载的进度
     * @param url 请求url
     * @param paramMap 请求参数
     * @param callBack 文件上载回调
     * @return call
     * @throws IOException
     */
    public static Call post (Context context, String url, Map<String, Object> paramMap, final ProgressCallBack callBack) throws IOException {

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        //MultipartBody必须有一个请求体，设置一个默认请求体
        builder.addFormDataPart("1", "1");

        List<File> fileList = new ArrayList<>();
        totalFilesLength = 0L;
        if(paramMap != null && !paramMap.isEmpty()) {
            Iterator<String> it = paramMap.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                Object value = paramMap.get(key);
                if(value instanceof FileObject) {
                    File file = new File(((FileObject) value).getFilePath());
                    fileList.add(file);
                    totalFilesLength += file.length();
                }else {
                    builder.addFormDataPart(key, paramMap.get(key).toString());
                }
            }
        }

        totalReadLength = 0L;
        for(File file : fileList) {
            RequestBody fileBody = new CountingFileRequestBody(file, "application/octet-stream", new CountingFileRequestBody.ProgressListener() {
                @Override
                public void onUpdate(long bytesRead, String fileName) {
                    totalReadLength += bytesRead;
                    callBack.callBack(fileName, (int)((100 * totalReadLength) / totalFilesLength));
                }
            });
            builder.addPart(Headers.of("Content-Disposition",
                    "form-data; name=file; filename=\"" + file.getName() + "\""), fileBody);
        }
        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .tag(context)
                .post(requestBody)
                .build();

        return client.newCall(request);
    }

    /**
     * 取消OKHttp请求
     * @param call 取消的请求
     */
    public static void cancelCall(Call call) {
        if(call != null && call.isExecuted()) {
            call.cancel();
        }
    }

    /** 根据Tag取消请求 */
    public static void cancelTag(Context tag) {
        synchronized (client.dispatcher().getClass()) {
            Log.d("WebService", "queuedCalls size:"+client.dispatcher().queuedCalls().size());
            for (Call call : client.dispatcher().queuedCalls()) {
                Log.d("WebService", call.request().tag().toString());
                if (tag.equals(call.request().tag())) {
                    call.cancel();
                    SubscriptionManager.removeSubscription(call);
                }
            }

            Log.d("WebService", "runningCalls size:"+client.dispatcher().runningCalls().size());
            for (Call call : client.dispatcher().runningCalls()) {
                Log.d("WebService", call.request().tag().toString());
                if (tag.equals(call.request().tag())) {
                    call.cancel();
                    SubscriptionManager.removeSubscription(call);
                }
            }
        }
    }

    public interface ProgressCallBack {
        void callBack(String fileName, int progress);
    }
}
