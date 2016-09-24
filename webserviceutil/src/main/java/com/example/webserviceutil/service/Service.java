package com.example.webserviceutil.service;

import android.content.Context;

import com.example.webserviceutil.OkHttp.OkHttpUtils;
import com.example.webserviceutil.SubscriptionManager;
import com.example.webserviceutil.callBack.CallBack;
import com.example.webserviceutil.entity.WebServiceError;
import com.example.webserviceutil.entity.WebServiceParam;
import com.example.webserviceutil.exception.ServiceErrorException;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import okhttp3.Call;
import okhttp3.Response;
import rx.Subscription;

/**
 * 服务基本类
 * @author 简建鸿 2016/6/14
 * @version 1.0
 */
public abstract class Service {

    public final static String TAG = "WebService";
    final static Gson gson = new Gson();
    public final static String POST_TYPE = "POST";
    public final static String GET_TYPE = "GET";

    /**
     * 服务执行方法
     * @param context OKHttp tag参数
     * @param param 请求参数
     * @param callBack 响应回调
     * @param <T>   请求对象类型
     * @return Subscription订阅对象
     */
    abstract <T> Subscription execute(final Context context, final WebServiceParam param, CallBack<T> callBack);

    /**
     * 获取网络请求响应体
     * @param context OKHttp tag参数
     * @param param 请求参数
     * @return Response
     * @throws IOException
     */
    Response getResponse(Context context, WebServiceParam param) throws IOException {
        Call call = null;
        if(GET_TYPE.equals(param.getMethod())) {
            call = OkHttpUtils.get(context, param.getRequestUrl());
        }else if(POST_TYPE.equals(param.getMethod())) {
            call = OkHttpUtils.post(context, param.getRequestUrl(), param.getParams(), null);
        }
        SubscriptionManager.addRequest(param, call);
        return call.execute();
    }

    /**
     * 异常处理，请求出现问题，区分不同的问题
     * @param e 异常对象
     * @param callBack 回调对象
     */
    public static void handleException(Throwable e, CallBack callBack) {
        WebServiceError webServiceError = null;
        if (e instanceof ServiceErrorException) {
            int code = ((ServiceErrorException) e).getCode();
            if (code >= 300 && code < 400) {
                webServiceError = WebServiceError.REQUESET_REDIRECTION;
            }else if (code >= 400 && code < 500) {
                webServiceError = WebServiceError.REQUESET_MISTAKES_ERROR;
            }else if (code >= 500) {
                webServiceError = WebServiceError.SERVICE_ERROR;
            }
        } else if (e instanceof FileNotFoundException) {
            webServiceError = WebServiceError.FILE_NO_FOUND;
        } else if (e instanceof UnknownHostException) {
            webServiceError = WebServiceError.UNKNOWN_HOST;
        } else if (e instanceof SocketTimeoutException) {
            webServiceError = WebServiceError.SOCKET_TIMEOUT;
        } else if(e instanceof ConnectException) {
            webServiceError = WebServiceError.CONNECT_FAILED;
        }else if (e instanceof IOException) {
            webServiceError = WebServiceError.IO_EXCEPTION;
        } else if (e instanceof JsonSyntaxException) {
            webServiceError = WebServiceError.JSON_SYNTAX;
        } else {
            webServiceError = WebServiceError.UNKNOWN_ERROE;
        }
        callBack.onFailure(webServiceError.getCode(), webServiceError.getMessage());
    }
}
