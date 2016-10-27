package com.example.requestmanager.service;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.example.requestmanager.callBack.ProgressCallBack;
import com.example.requestmanager.okhttp.OkHttpUtils;
import com.example.requestmanager.SubscriptionManager;
import com.example.requestmanager.callBack.CallBack;
import com.example.requestmanager.entity.ProgressValue;
import com.example.requestmanager.entity.WebServiceParam;
import com.example.requestmanager.exception.ServiceErrorException;


import okhttp3.Call;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 文件上传服务，并返回一个对象
 * @author 简建鸿 2016/6/14
 * @version 1.0
 */
public final class ProgressObjectService extends Service {
    private static final ProgressObjectService INSTATNCE = new ProgressObjectService();

    private ProgressObjectService() {}

    public static ProgressObjectService getInstance() {
        return INSTATNCE;
    }
    @Override
    public <T> Subscription execute(final Context context, final WebServiceParam param, CallBack<T> callBack) {
        Subscription subscription =  Observable.create(new Observable.OnSubscribe<ProgressValue<T>>() {
            @Override
            public void call(final Subscriber<? super ProgressValue<T>> subscriber) {
                if(subscriber.isUnsubscribed())
                    return;
                try {
                    Call call = OkHttpUtils.post(context, param.getRequestUrl(), param.getParams(), new OkHttpUtils.ProgressCallBack() {
                        @Override
                        public void callBack(String fileName, int progress) {
                            Log.d("WebService", fileName +", "+ progress);
                            subscriber.onNext(new ProgressValue<T>(fileName, progress));
                        }
                    });
                    SubscriptionManager.addRequest(param, call);
                    Response response = call.execute();

                    if(response.isSuccessful()) {
                        JsonElement jsonElement = new JsonParser().parse(response.body().charStream());
                        if(jsonElement.isJsonObject()) {
                            rebuildJsonObj((JsonObject)jsonElement);
                        }

                        if(param.getClassType() != null) {
                            subscriber.onNext(new ProgressValue<T>((T)gson.fromJson(jsonElement.toString(), param.getClassType())));
                        }else if((param.getClazz() != null)) {
                            subscriber.onNext(new ProgressValue<T>((T)gson.fromJson(jsonElement.toString(), param.getClazz())));
                        }
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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getProgressSubscriber(param, (ProgressCallBack<T>)callBack));
        SubscriptionManager.addSubscription(param, subscription);
        return subscription;
    }

    private <T> Subscriber<ProgressValue<T>> getProgressSubscriber(final WebServiceParam param, final ProgressCallBack<T> callBack) {
        return new Subscriber<ProgressValue<T>>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "WebService onCompleted");
                SubscriptionManager.removeSubscription(param);
                callBack.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "WebService onError");
                SubscriptionManager.removeSubscription(param);
                Service.handleException(e, callBack);
                callBack.onCompleted();
                e.printStackTrace();
            }

            @Override
            public void onNext(ProgressValue<T> t) {
                if(t.getFileName() == null || "".equals(t.getFileName())) {
                    callBack.onSuccess(t.getObject().get(0));
                }else {
                    callBack.onProgress(t.getFileName(), t.getProgress());
                }

            }
        };
    }
}
