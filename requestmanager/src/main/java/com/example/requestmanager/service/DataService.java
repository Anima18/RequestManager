package com.example.requestmanager.service;

import android.util.Log;

import com.example.requestmanager.callBack.CallBack;
import com.example.requestmanager.callBack.DataCallBack;
import com.example.requestmanager.entity.WebServiceParam;
import com.example.requestmanager.exception.ServiceErrorException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.trello.rxlifecycle.android.ActivityEvent;

import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 获取一个对象服务
 *
 * @author 简建鸿 2016/6/14
 * @version 1.0
 */
public final class DataService extends Service {

    private static final DataService INSTATNCE = new DataService();

    private DataService() {
    }

    public static DataService getInstance() {
        return INSTATNCE;
    }

    @Override
    public <T> void execute(final WebServiceParam param, CallBack<T> callBack) {
        Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                if (subscriber.isUnsubscribed())
                    return;
                callObjectWebService(subscriber, param);

            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(param.getProvider().bindUntilEvent(ActivityEvent.PAUSE))
                .subscribe(getSubscriber((DataCallBack<T>) callBack));

    }

    public <T> void callObjectWebService(Subscriber<? super T> subscriber, WebServiceParam param) {
        try {
            Response response = getResponse(param);
            if (response.isSuccessful()) {
                JsonElement jsonElement = new JsonParser().parse(response.body().charStream());
                if (jsonElement.isJsonObject()) {
                    rebuildJsonObj((JsonObject) jsonElement);
                }
                response.body().close();
                if (param.getClassType() != null) {
                    subscriber.onNext((T) gson.fromJson(jsonElement.toString(), param.getClassType()));
                } else if ((param.getClazz() != null)) {
                    subscriber.onNext((T) gson.fromJson(jsonElement.toString(), param.getClazz()));
                }
                subscriber.onCompleted();
            } else {
                subscriber.onError(new ServiceErrorException(response.code()));
            }

        } catch (Exception e) {
            subscriber.onError(e);
        }
    }

    private <T> Subscriber<T> getSubscriber(final DataCallBack<T> callBack) {
        return new Subscriber<T>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "WebService onCompleted");
                callBack.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "WebService onError");
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
