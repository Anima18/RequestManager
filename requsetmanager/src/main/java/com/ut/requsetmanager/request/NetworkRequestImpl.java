package com.ut.requsetmanager.request;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.ut.requsetmanager.callback.DataRequestCallback;
import com.ut.requsetmanager.entity.ResponseStatus;
import com.ut.requsetmanager.entity.WebServiceError;
import com.ut.requsetmanager.entity.WebServiceParam;
import com.ut.requsetmanager.network.NetworkTaskImpl;
import com.ut.requsetmanager.util.JsonUtil;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import rx.Observable;
import rx.Subscriber;

/**
 * Created by jianjianhong on 2017/11/19.
 */

public class NetworkRequestImpl<T> implements NetworkRequest, DialogInterface.OnCancelListener {
    
    private static final String TAG = "NetworkRequestImpl";
    public static final String POST = "POST";
    public static final String GET = "GET";


    private Context context;
    private WebServiceParam param;
    private String progressMessage;
    private NetworkProgressDialog progressDialog;
    private Call task;
    private Observable observable;

    public NetworkRequestImpl(Context context) {
        this.context = context;
        this.param = new WebServiceParam();
        param.setClassType(new TypeToken<T>(){}.getType());
    }

    @Override
    public NetworkRequest setUrl(String url) {
        this.param.setUrl(url);
        return this;
    }

    @Override
    public NetworkRequest setMethod(String method) {
        this.param.setMethod(method);
        return this;
    }

    @Override
    public NetworkRequest setDownloadFilePath(String filePath) {
        this.param.setDownloadFilePath(filePath);
        return this;
    }

    @Override
    public NetworkRequest setDownloadFileName(String fileName) {
        this.param.setDownloadFileName(fileName);
        return this;
    }

    @Override
    public NetworkRequest setTimeout(long timeout, TimeUnit timeoutUnit) {
        this.param.setTimeout(timeout);
        this.param.setTimeoutUnit(timeoutUnit);
        return this;
    }
    @Override
    public NetworkRequest setProgressMessage(String message) {
        return setProgressMessage(message, ProgressDialog.STYLE_SPINNER);
    }

    @Override
    public NetworkRequest setProgressMessage(String message, int style) {
        if(!TextUtils.isEmpty(message)) {
            this.progressMessage = message;
            this.progressDialog = new NetworkProgressDialogImpl(context, style, this);
        }
        return this;
    }

    @Override
    public NetworkRequest addParam(String key, Object value) {
        this.param.addParam(key, value);
        return this;
    }

    @Override
    public NetworkRequest setParams(Map params) {
        this.param.addParam(params);
        return this;
    }

    @Override
    public void send(final DataRequestCallback callback) {
        showProgress();

        this.task = NetworkTaskImpl.getInstance().dataTask(context, param, new NetworkTaskImpl.DataTaskCallback<T>() {
            @Override
            public void onSuccess(T data) {
                callback.onResult(data, new ResponseStatus(200, ""));
                hideProgress();
            }

            @Override
            public void onFailure(Throwable e) {
                WebServiceError error = JsonUtil.getError(e);
                callback.onResult(null, new ResponseStatus(error.getCode(), error.getMessage()));
                hideProgress();
            }
        });
    }
    @Override
    public void download(final DataRequestCallback callback) {
        showProgress();

        this.task = NetworkTaskImpl.getInstance().downloadTask(context, param, new NetworkTaskImpl.ProgressTaskCallback<T>() {
            @Override
            public void onSuccess(Object data) {
                callback.onResult((T)data, new ResponseStatus(200, ""));
                hideProgress();
            }

            @Override
            public void onFailure(Throwable e) {
                WebServiceError error = JsonUtil.getError(e);
                callback.onResult(null, new ResponseStatus(error.getCode(), error.getMessage()));
                hideProgress();
            }

            @Override
            public void onProgress(String fileName, int progress) {
                updateProgress(fileName, progress);
            }
        });
    }

    @Override
    public void upload(final DataRequestCallback callback) {
        showProgress();

        this.task = NetworkTaskImpl.getInstance().uploadTask(context, param, new NetworkTaskImpl.ProgressTaskCallback<T>() {
            @Override
            public void onSuccess(Object data) {
                callback.onResult((T)data, new ResponseStatus(200, ""));
                hideProgress();
            }

            @Override
            public void onFailure(Throwable e) {
                WebServiceError error = JsonUtil.getError(e);
                callback.onResult(null, new ResponseStatus(error.getCode(), error.getMessage()));
                hideProgress();
            }

            @Override
            public void onProgress(String fileName, int progress) {
                updateProgress(fileName, progress);
            }
        });
    }

    @Override
    public NetworkRequest dataRequest() {
        final NetworkRequestImpl request = this;
        this.observable = Observable.create(new Observable.OnSubscribe<Map<String, T>>() {
            @Override
            public void call(final Subscriber<? super Map<String, T>> subscriber) {
                if(subscriber.isUnsubscribed())
                    return;
                showProgress();

                Call task = NetworkTaskImpl.getInstance().dataTask(context, param, new NetworkTaskImpl.DataTaskCallback<T>() {
                    @Override
                    public void onSuccess(T data) {
                        Map<String, T> dataMap = new HashMap<>();
                        dataMap.put(request.toString(), data);
                        subscriber.onNext(dataMap);
                        subscriber.onCompleted();
                        hideProgress();
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        subscriber.onError(e);
                        subscriber.onCompleted();
                        hideProgress();
                    }
                });
                setTask(task);
            }
        });
        return this;
    }

    @Override
    public  NetworkRequest downloadRequest() {
        final NetworkRequestImpl request = this;
        this.observable = Observable.create(new Observable.OnSubscribe<Map<String, Object>>() {
            @Override
            public void call(final Subscriber<? super Map<String, Object>> subscriber) {
                if(subscriber.isUnsubscribed())
                    return;
                showProgress();

                Call task = NetworkTaskImpl.getInstance().downloadTask(context, param, new NetworkTaskImpl.ProgressTaskCallback<T>() {
                    @Override
                    public void onSuccess(Object data) {
                        Map<String, Object> dataMap = new HashMap<>();
                        dataMap.put(request.toString(), data);
                        subscriber.onNext(dataMap);
                        subscriber.onCompleted();
                        hideProgress();
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        subscriber.onError(e);
                        subscriber.onCompleted();
                        hideProgress();
                    }

                    @Override
                    public void onProgress(String fileName, int progress) {
                        updateProgress(fileName, progress);
                    }
                });
                setTask(task);
            }
        });
        return this;
    }

    @Override
    public NetworkRequest uploadRequest() {
        final NetworkRequestImpl request = this;
        this.observable = Observable.create(new Observable.OnSubscribe<Map<String, Object>>() {
            @Override
            public void call(final Subscriber<? super Map<String, Object>> subscriber) {
                if(subscriber.isUnsubscribed())
                    return;

                showProgress();

                Call task = NetworkTaskImpl.getInstance().uploadTask(context, param, new NetworkTaskImpl.ProgressTaskCallback<T>() {
                    @Override
                    public void onSuccess(Object data) {
                        Map<String, Object> dataMap = new HashMap<>();
                        dataMap.put(request.toString(), data);
                        subscriber.onNext(dataMap);
                        subscriber.onCompleted();
                        hideProgress();
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        subscriber.onError(e);
                        subscriber.onCompleted();
                        hideProgress();
                    }

                    @Override
                    public void onProgress(String fileName, int progress) {
                        updateProgress(fileName, progress);
                    }
                });
                setTask(task);
            }
        });
        return this;
    }

    @Override
    public Call getTask() {
        return this.task;
    }

    public void setTask(Call task) {
        this.task = task;
    }

    @Override
    public Observable getObservable() {
        return observable;
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if(this.task != null) {
            task.cancel();
            Log.i(TAG, task.toString() + " canceled");
        }
    }

    @Override
    public void hideProgress() {
        if(progressDialog != null) {
            progressDialog.hideProgress();
        }
    }

    private void showProgress() {
        if(progressDialog != null) {
            progressDialog.showProgress(progressMessage);
        }
    }

    private void updateProgress(String fileName, int progress) {
        if(progressDialog != null) {
            progressDialog.updateProgress(fileName, progress);
        }
    }
}
