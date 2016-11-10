package com.example.requestmanager.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.requestmanager.entity.WebServiceParam;
import com.example.requestmanager.okhttp.OkHttpUtils;
import com.example.requestmanager.callBack.BitmapCallBack;
import com.example.requestmanager.exception.ServiceErrorException;
import com.trello.rxlifecycle.android.ActivityEvent;

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

    public <T> Subscription execute(final WebServiceParam param, BitmapCallBack callBack) {

        Subscription subscription =  Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                if(subscriber.isUnsubscribed())
                    return;
                callBitmapWebService(subscriber, param);
            }
        })
                .compose(param.getProvider().bindUntilEvent(ActivityEvent.PAUSE))
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(getBitMapSubscriber(param.getRequestUrl(), callBack));

        //SubscriptionManager.addSubscription(param.getRequestUrl(), subscription);
        return subscription;
    }

    private void callBitmapWebService(Subscriber<? super Bitmap> subscriber, WebServiceParam param) {
        try {
            Call call = OkHttpUtils.get(param);
            //SubscriptionManager.addRequest(param.getRequestUrl(), call);
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
                callBack.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "WebService onError");
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
