package com.example.webserviceutil.service;

import android.content.Context;
import android.util.Log;

import com.example.webserviceutil.SubscriptionManager;
import com.example.webserviceutil.callBack.CallBack;
import com.example.webserviceutil.callBack.CollectionCallBack;
import com.example.webserviceutil.entity.WebServiceParam;
import com.example.webserviceutil.exception.ServiceErrorException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 获取对象集合服务
 * @author 简建鸿 2016/6/14
 * @version 1.0
 */
public final class CollectionService extends Service {

    private static final CollectionService INSTATNCE = new CollectionService();

    private CollectionService() {}

    public static CollectionService getInstance() {
        return INSTATNCE;
    }

    @Override
    public <T> Subscription execute(final Context context, final WebServiceParam param, CallBack<T> callBack) {
        Subscription subscription =  Observable.create(new Observable.OnSubscribe<List<T>>() {
            @Override
            public void call(Subscriber<? super List<T>> subscriber) {
                if(subscriber.isUnsubscribed())
                    return;
                callCollectionWebService(context, subscriber, param);
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(getSubscriber(param, (CollectionCallBack<T>)callBack));
        SubscriptionManager.addSubscription(param, subscription);
        return subscription;
    }

    private <T> void callCollectionWebService(Context context, Subscriber<? super List<T>> subscriber, WebServiceParam param) {
        try {
            Response response = getResponse(context, param);
            if(response.isSuccessful()) {
                List<T> resultList = new ArrayList<>();

                JsonArray array = new JsonParser().parse(response.body().charStream()).getAsJsonArray();
                for(JsonElement elem : array){
                    resultList.add((T) gson.fromJson(elem, param.getClazz()));
                }
                subscriber.onNext(resultList);
                subscriber.onCompleted();
            }else {
                subscriber.onError(new ServiceErrorException(response.code()));
            }

        } catch (Exception e) {
            subscriber.onError(e);
        }
    }

    private <T> Subscriber<List<T>> getSubscriber(final WebServiceParam param, final CollectionCallBack<T> callBack) {
        return new Subscriber<List<T>>() {
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
                e.printStackTrace();
            }

            @Override
            public void onNext(List<T> t) {
                callBack.onSuccess(t);
            }
        };
    }
}
