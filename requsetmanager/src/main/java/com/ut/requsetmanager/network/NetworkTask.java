package com.ut.requsetmanager.network;

import android.content.Context;

import com.ut.requsetmanager.callback.DataRequestCallback;
import com.ut.requsetmanager.callback.ProgressRequestCallback;
import com.ut.requsetmanager.entity.WebServiceParam;

import okhttp3.Call;

/**
 * Created by jianjianhong on 2017/11/18.
 */

public interface NetworkTask {
    <T> Call dataTask(Context context, WebServiceParam param, NetworkTaskImpl.DataTaskCallback<T> dataCallback);

    <T> Call downloadTask(Context context,WebServiceParam param, NetworkTaskImpl.ProgressTaskCallback<T> callBack);

    <T> Call uploadTask(Context context,WebServiceParam param, NetworkTaskImpl.ProgressTaskCallback<T> callBack);
}
