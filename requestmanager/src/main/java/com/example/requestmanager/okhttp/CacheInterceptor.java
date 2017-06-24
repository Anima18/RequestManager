package com.example.requestmanager.okhttp;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jianjianhong on 2016/10/20.
 */
public class CacheInterceptor implements Interceptor {
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);
        return response.newBuilder()
                .removeHeader("Pragma")
                .removeHeader("Cache-Control")
                //cache for 30 days
                //.header("Cache-Control", "max-age=" + 3600 * 24 * 30)
                .header("Cache-Control", "max-age=" + 3600)
                .build();
    }
}