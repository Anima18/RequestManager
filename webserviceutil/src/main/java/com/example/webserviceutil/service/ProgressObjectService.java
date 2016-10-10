package com.example.webserviceutil.service;

import android.content.Context;
import android.util.Log;

import com.example.webserviceutil.OkHttp.OkHttpUtils;
import com.example.webserviceutil.SubscriptionManager;
import com.example.webserviceutil.callBack.CallBack;
import com.example.webserviceutil.callBack.ProgressObjectCallBack;
import com.example.webserviceutil.entity.ProgressValue;
import com.example.webserviceutil.entity.WebServiceParam;
import com.example.webserviceutil.exception.ServiceErrorException;


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
                        subscriber.onNext(new ProgressValue<T>((T)gson.fromJson(response.body().charStream(), param.getClazz())));
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
                .subscribe(getProgressSubscriber(param, (ProgressObjectCallBack<T>)callBack));
        SubscriptionManager.addSubscription(param, subscription);
        return subscription;
    }

    private <T> Subscriber<ProgressValue<T>> getProgressSubscriber(final WebServiceParam param, final ProgressObjectCallBack<T> callBack) {
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
