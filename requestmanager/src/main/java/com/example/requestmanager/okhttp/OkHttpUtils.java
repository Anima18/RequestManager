package com.example.requestmanager.okhttp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.requestmanager.entity.FileObject;
import com.example.requestmanager.entity.WebServiceParam;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * OKHttp封装类，封装get和post请求方法
 * @author 简建鸿 2016/6/3
 * @version 1.0
 */
public class OkHttpUtils {

    @NonNull
    private final static OkHttpClient client ;
    //private final static String CACHE_DIR = Environment.getExternalStorageDirectory() + "/RxJavaSimple/caches";
   // private final static Cache CACHE = new Cache(new File(CACHE_DIR), 10240*1024);
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
                //.addNetworkInterceptor(new CacheInterceptor())
                //.cache(CACHE)
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
    public static Call get(@NonNull String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        return client.newCall(request);
    }

    public static Call get(@NonNull WebServiceParam param) throws IOException {
        Request request = new Request.Builder()
                .url(param.getRequestUrl())
                .tag(param.getTag())
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
    public static Call post(@NonNull String url, @Nullable Map<String, Object> paramMap, @NonNull final ProgressCallBack callBack) throws IOException {

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
                .post(requestBody)
                .build();

        return client.newCall(request);
    }

    public static Call post (@NonNull String url, @Nullable Map<String, Object> paramMap) throws IOException {

        FormBody.Builder builder = new FormBody.Builder();
        builder.add("1", "1");

        if(paramMap != null && !paramMap.isEmpty()) {
            Iterator<String> it = paramMap.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                builder.addEncoded(key, paramMap.get(key).toString());
            }
        }
        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        return client.newCall(request);
    }

    public static Call downloadFile(@NonNull String url, @Nullable final Map<String, Object> paramMap, @NonNull final ProgressCallBack callBack) throws IOException {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("1", "1");

        if(paramMap != null && !paramMap.isEmpty()) {
            Iterator<String> it = paramMap.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                builder.addEncoded(key, paramMap.get(key).toString());
            }
        }
        RequestBody requestBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        final ProgressResponseBody.ProgressListener progressListener = new ProgressResponseBody.ProgressListener() {
            @Override public void update(long bytesRead, long contentLength, boolean done) {
                //System.out.format("%d%% done\n", (100 * bytesRead) / contentLength);
                callBack.callBack(paramMap.get("fileName").toString(), (int)((100 * bytesRead) / contentLength));

            }
        };

        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override public Response intercept(@NonNull Chain chain) throws IOException {
                        Response originalResponse = chain.proceed(chain.request());
                        return originalResponse.newBuilder()
                                .body(new ProgressResponseBody(originalResponse.body(), progressListener))
                                .build();
                    }
                })
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        return client.newCall(request);
    }

    /**
     * 取消OKHttp请求
     * @param call 取消的请求
     */
    public static void cancelCall(@Nullable Call call) {
        if(call != null && call.isExecuted()) {
            call.cancel();
        }
    }

    /** 根据Tag取消请求 */
    public static void cancelTag(@NonNull Object tag) {
        synchronized (client.dispatcher().getClass()) {
            Log.d("WebService", "queuedCalls size:"+client.dispatcher().queuedCalls().size());
            for (Call call : client.dispatcher().queuedCalls()) {
                Log.d("WebService", call.request().tag().toString());
                if (tag.equals(call.request().tag())) {
                    call.cancel();
                    //SubscriptionManager.removeSubscription(call);
                }
            }

            Log.d("WebService", "runningCalls size:"+client.dispatcher().runningCalls().size());
            for (Call call : client.dispatcher().runningCalls()) {
                Log.d("WebService", call.request().tag().toString());
                if (tag.equals(call.request().tag())) {
                    call.cancel();
                    //SubscriptionManager.removeSubscription(call);
                }
            }
        }
    }

    public interface ProgressCallBack {
        void callBack(String fileName, int progress);
    }
}
