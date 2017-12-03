package com.ut.requsetmanager.network;

import com.ut.requsetmanager.callback.DataRequestCallback;
import com.ut.requsetmanager.callback.ProgressRequestCallback;
import com.ut.requsetmanager.entity.WebServiceParam;

import okhttp3.Call;

/**
 * Created by jianjianhong on 2017/11/18.
 */

public interface NetworkTask {
    <T> Call dataTask(WebServiceParam param, NetworkTaskImpl.DataTaskCallback<T> dataCallback);

    <T> Call downloadTask(WebServiceParam param, NetworkTaskImpl.ProgressTaskCallback<T> callBack);

    <T> Call uploadTask(WebServiceParam param, NetworkTaskImpl.ProgressTaskCallback<T> callBack);
}
