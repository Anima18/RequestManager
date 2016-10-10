package com.example.webserviceutil.service;

import android.content.Context;
import android.util.Log;

import com.example.webserviceutil.SubscriptionManager;
import com.example.webserviceutil.callBack.CallBack;
import com.example.webserviceutil.callBack.ObjectCallBack;
import com.example.webserviceutil.entity.WebServiceParam;
import com.example.webserviceutil.exception.ServiceErrorException;

import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 获取一个对象服务
 * @author 简建鸿 2016/6/14
 * @version 1.0
 */
public final class ObjectService extends Service {

    private static final ObjectService INSTATNCE = new ObjectService();

    private ObjectService() {}

    public static ObjectService getInstance() {
        return INSTATNCE;
    }

    @Override
    public <T> Subscription execute(final Context context, final WebServiceParam param, CallBack<T> callBack) {
        Subscription subscription =  Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                if(subscriber.isUnsubscribed())
                    return;
                callObjectWebService(context, subscriber, param);

            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(getSubscriber(param, (ObjectCallBack<T>)callBack));

        SubscriptionManager.addSubscription(param, subscription);
        return subscription;
    }

    public <T> void callObjectWebService(Context context, Subscriber<? super T> subscriber, WebServiceParam param) {
        try {
            Response response = getResponse(context, param);
            if(response.isSuccessful()) {
                subscriber.onNext((T)gson.fromJson(response.body().charStream(), param.getClazz()));
                subscriber.onCompleted();
            }else {
                subscriber.onError(new ServiceErrorException(response.code()));
            }

        }catch (Exception e) {
            subscriber.onError(e);
        }
    }

    private <T> Subscriber<T> getSubscriber(final WebServiceParam param, final ObjectCallBack<T> callBack) {
        return new Subscriber<T>() {
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
            public void onNext(T t) {
                callBack.onSuccess(t);
            }
        };
    }
}
