package com.example.requestmanager.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.requestmanager.okhttp.OkHttpUtils;
import com.example.requestmanager.SubscriptionManager;
import com.example.requestmanager.callBack.BitmapCallBack;
import com.example.requestmanager.exception.ServiceErrorException;

import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 获取图片资源服务
 * @author 简建鸿 2016/6/14
 * @version 1.0
 */
public final class BitMapService {

    private final static String TAG = "WebService";
    private static final BitMapService INSTATNCE = new BitMapService();

    private BitMapService() {}

    public static BitMapService getInstance() {
        return INSTATNCE;
    }

    public <T> Subscription execute(final String url, BitmapCallBack callBack) {
        if(SubscriptionManager.isContainUrl(url)) {
            return null;
        }
        Subscription subscription =  Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                if(subscriber.isUnsubscribed())
                    return;
                callBitmapWebService(subscriber, url);
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(getBitMapSubscriber(url, callBack));

        //SubscriptionManager.addSubscription(url, subscription);
        return subscription;
    }

    private void callBitmapWebService(Subscriber<? super Bitmap> subscriber, String url) {
        try {
            Call call = OkHttpUtils.get(url);
            //SubscriptionManager.addRequest(url, call);
            Response response = call.execute();
            if(response.isSuccessful()) {
                InputStream is = response.body().byteStream();
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                response.body().close();
                subscriber.onNext(bitmap);
                subscriber.onCompleted();
            }else {
                subscriber.onError(new ServiceErrorException(response.code()));
            }

        } catch (Exception e) {
            subscriber.onError(e);
        }
    }

    private Subscriber<Bitmap> getBitMapSubscriber(final String url, final BitmapCallBack callBack) {
        return new Subscriber<Bitmap>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "WebService onCompleted");
                SubscriptionManager.removeSubscription(url);
                callBack.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "WebService onError");
                SubscriptionManager.removeSubscription(url);
                Service.handleException(e, callBack);
                e.printStackTrace();
            }

            @Override
            public void onNext(Bitmap bitmap) {
                callBack.onSuccess(url, bitmap);
            }
        };
    }
}
