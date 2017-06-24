package com.example.requestmanager.service;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.requestmanager.callBack.CallBack;
import com.example.requestmanager.callBack.ProgressCallBack;
import com.example.requestmanager.entity.ProgressValue;
import com.example.requestmanager.entity.WebServiceParam;
import com.example.requestmanager.exception.ServiceErrorException;
import com.example.requestmanager.okhttp.OkHttpUtils;
import com.trello.rxlifecycle.android.ActivityEvent;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.Call;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 文件上传服务，并返回一个对象
 * @author 简建鸿 2016/6/14
 * @version 1.0
 */
public final class DownloadFileService extends Service {
    private static final DownloadFileService INSTATNCE = new DownloadFileService();
    final String basePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/RxJava/";

    private DownloadFileService() {}

    @NonNull
    public static DownloadFileService getInstance() {
        return INSTATNCE;
    }
    @Override
    public <T> void execute(@NonNull final WebServiceParam param, CallBack<T> callBack) {
        Observable.create(new Observable.OnSubscribe<ProgressValue<T>>() {
            @Override
            public void call(@NonNull final Subscriber<? super ProgressValue<T>> subscriber) {
                if(subscriber.isUnsubscribed())
                    return;
                try {
                    Call call = OkHttpUtils.downloadFile(param.getRequestUrl(), param.getParams(), new OkHttpUtils.ProgressCallBack() {
                        @Override
                        public void callBack(String fileName, int progress) {
                            Log.d("WebService", fileName +", "+ progress);
                            subscriber.onNext(new ProgressValue<T>(fileName, progress));
                        }
                    });
                    //SubscriptionManager.addRequest(param, call);
                    Response response = call.execute();

                    if(response.isSuccessful()) {
                        InputStream is = response.body().byteStream();
                        BufferedInputStream input = new BufferedInputStream(is);
                        OutputStream output = new FileOutputStream(basePath + param.getParams().get("fileName").toString());

                        byte[] data = new byte[1024];
                        int count = 0;

                        while ((count = input.read(data)) != -1) {
                            output.write(data, 0, count);
                        }
                        output.flush();
                        output.close();
                        input.close();

                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }else {
                        subscriber.onError(new ServiceErrorException(response.code()));
                    }

                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        })
                .distinct()
                .compose(param.getProvider().bindUntilEvent(ActivityEvent.PAUSE))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getProgressSubscriber((ProgressCallBack<T>)callBack));
    }

    @Nullable
    private <T> Subscriber<ProgressValue<T>> getProgressSubscriber(@NonNull final ProgressCallBack<T> callBack) {
        return new Subscriber<ProgressValue<T>>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "WebService onCompleted");
                callBack.onCompleted();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d(TAG, "WebService onError");
                Service.handleException(e, callBack);
                callBack.onCompleted();
                e.printStackTrace();
            }

            @Override
            public void onNext(@Nullable ProgressValue<T> t) {
                if(t == null) {
                    callBack.onSuccess((T)new Boolean(true));
                }else {
                    callBack.onProgress(t.getFileName(), t.getProgress());
                }

            }
        };
    }
}
