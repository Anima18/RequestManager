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

public interface NetworkRequest<T> {

    NetworkRequest setUrl(String url);

    NetworkRequest setMethod(String method);

    NetworkRequest setDownloadFilePath(String filePath);

    NetworkRequest setDownloadFileName(String fileName);

    NetworkRequest setTimeout(long timeout, TimeUnit timeoutUnit);

    NetworkRequest setProgressMessage(String message);

    NetworkRequest setProgressMessage(String message, int style);

    NetworkRequest addParam(String key, Object value);

    NetworkRequest setParams(Map<String, Object> params);

    void send(DataRequestCallback<T> callback);

    void download(DataRequestCallback<T> callback);

    void upload(DataRequestCallback<T> callback);

    NetworkRequest dataRequest();

    NetworkRequest downloadRequest();

    NetworkRequest uploadRequest();

    Call getTask();

    Observable getObservable();

    void hideProgress();
}
