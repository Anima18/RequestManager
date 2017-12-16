package com.ut.requsetmanager.network;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.ut.requsetmanager.callback.DataResponseCallback;
import com.ut.requsetmanager.callback.DownloadResponseCallback;
import com.ut.requsetmanager.entity.FileObject;
import com.ut.requsetmanager.entity.WebServiceParam;

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
 * Created by jianjianhong on 2017/11/18.
 */

public class NetworkTaskImpl implements NetworkTask {
    private final static long CONNECT_TIMEOUT = 30;
    private final static long READ_TIMEOUT = 30;
    private final static long WRITE_TIMEOUT = 30;
    private final static String POST_TYPE = "POST";
    private final static String GET_TYPE = "GET";

    private static OkHttpClient client ;
    private static Gson gson = new Gson();
    /**
     * 已上载文件大小
     */
    private static Long totalReadLength;
    /**
     * 所有文件大小
     */
    private static Long totalFilesLength;

    private static NetworkTask instance = new NetworkTaskImpl();
     private NetworkTaskImpl (){
         client = new OkHttpClient.Builder()
                 .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                 .writeTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                 .readTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                 .build();
     }
     public static NetworkTask getInstance() {
             return instance;
    }

    @Override
    public <T> Call dataTask(Context context, WebServiceParam param, DataTaskCallback<T> dataCallback) {
        Request request = generateRequest(param);
        resetClientTimeOut(param.getTimeout(), param.getTimeoutUnit());
        Call call = client.newCall(request);
        call.enqueue(new DataResponseCallback(context, param, dataCallback));
        return call;
    }

    @Override
    public <T> Call downloadTask(Context context, final WebServiceParam param, final ProgressTaskCallback<T> callBack) {
        Request request = generateRequest(param);

        final ProgressResponseBody.ProgressListener progressListener = new ProgressResponseBody.ProgressListener() {
            @Override public void update(long bytesRead, long contentLength, boolean done) {
                callBack.onProgress(param.getDownloadFileName(), (int)((100 * bytesRead) / contentLength));

            }
        };

        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new Interceptor() {
                    @Override public Response intercept(Chain chain) throws IOException {
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

        resetClientTimeOut(param.getTimeout(), param.getTimeoutUnit());
        Call call = client.newCall(request);
        call.enqueue(new DownloadResponseCallback(context, param, callBack));
        return call;
    }

    @Override
    public <T> Call uploadTask(Context context, WebServiceParam param, final ProgressTaskCallback<T> callBack) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        //MultipartBody必须有一个请求体，设置一个默认请求体
        builder.addFormDataPart("1", "1");

        List<File> fileList = new ArrayList<>();
        totalFilesLength = 0L;
        Map<String, Object> paramMap = param.getParams();
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
                    callBack.onProgress(fileName, (int)((100 * totalReadLength) / totalFilesLength));
                }
            });
            builder.addPart(Headers.of("Content-Disposition",
                    "form-data; name=file; filename=\"" + file.getName() + "\""), fileBody);
        }
        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(param.getUrl())
                .post(requestBody)
                .build();

        resetClientTimeOut(param.getTimeout(), param.getTimeoutUnit());
        Call call = client.newCall(request);
        call.enqueue(new DataResponseCallback(context, param, callBack));
        return call;
    }

    private Request generateRequest(WebServiceParam param) {
        if(GET_TYPE.equals(param.getMethod())) {
            return getRequest(param);
        }else if(POST_TYPE.equals(param.getMethod())) {
            return postRequest(param);
        }else {
            return null;
        }
    }

    private Request getRequest(WebServiceParam param) {
        StringBuilder urlBuilder = new StringBuilder(param.getUrl());

        Map<String, Object> paramMap = param.getParams();
        if(paramMap != null && !paramMap.isEmpty()) {
            if(!param.getUrl().contains("?")) {
                urlBuilder.append("?");
            }

            Iterator<String> it = paramMap.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                String value = paramMap.get(key).toString();
                urlBuilder.append("&");
                urlBuilder.append(key);
                urlBuilder.append("=");
                urlBuilder.append(value);
            }
        }
        Request request = new Request.Builder()
                .url(urlBuilder.toString())
                .build();

        return request;
    }

    private Request postRequest(WebServiceParam param) {
        FormBody.Builder builder = new FormBody.Builder();

        Map<String, Object> paramMap = param.getParams();
        if(paramMap != null && !paramMap.isEmpty()) {
            Iterator<String> it = paramMap.keySet().iterator();
            while (it.hasNext()) {
                String key = it.next();
                builder.addEncoded(key, paramMap.get(key).toString());
            }
        }
        RequestBody requestBody = builder.build();

        Request request = new Request.Builder()
                .url(param.getUrl())
                .post(requestBody)
                .build();

        return request;
    }

    private void resetClientTimeOut(long timeOut, TimeUnit timeUnit) {
        Log.i("Chris", "connectTimeoutMillis:"+client.connectTimeoutMillis());
        Log.i("Chris", "default time::"+TimeUnit.SECONDS.toMillis(CONNECT_TIMEOUT));
        if(timeOut == 0 || timeUnit == null) {
            if(client.connectTimeoutMillis() != TimeUnit.MILLISECONDS.toMillis(CONNECT_TIMEOUT)) {
                client = client.newBuilder()
                        .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                        .writeTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                        .readTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                        .build();
            }
        }else {
            client = client.newBuilder()
                    .connectTimeout(timeOut, timeUnit)
                    .writeTimeout(timeOut, timeUnit)
                    .readTimeout(timeOut, timeUnit)
                    .build();

        }
    }

    public interface DataTaskCallback<T> {
        void onSuccess(T data);
        void onFailure(Throwable e);
    }

    public interface ProgressTaskCallback<T> extends  DataTaskCallback{
        void onProgress(String fileName, int progress);
    }
}
