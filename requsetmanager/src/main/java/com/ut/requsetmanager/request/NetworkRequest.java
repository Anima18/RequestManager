package com.ut.requsetmanager.request;

import com.ut.requsetmanager.callback.DataRequestCallback;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import rx.Observable;

/**
 * Created by jianjianhong on 2017/11/19.
 */

public interface NetworkRequest {

    NetworkRequest setUrl(String url);

    NetworkRequest setMethod(String method);

    NetworkRequest setDataClass(Class cls);

    NetworkRequest setDataType(Type type);

    NetworkRequest setDownloadFilePath(String filePath);

    NetworkRequest setProgressMessage(String message);

    NetworkRequest setProgressMessage(String message, int style);

    NetworkRequest addParam(String key, Object value);

    NetworkRequest setParams(Map<String, Object> params);

    NetworkRequest setTimeout(long timeout, TimeUnit timeUnit);

    NetworkRequest isPlatformService(Boolean platformService);

    <T> void send(DataRequestCallback<T> callback);

    <T> void download(DataRequestCallback<T> callback);

    <T>void upload(DataRequestCallback<T> callback);

    <T> NetworkRequest dataRequest();

    <T>NetworkRequest downloadRequest();

    <T> NetworkRequest uploadRequest();

    Call getTask();

    Observable getObservable();

    void hideProgress();
}
