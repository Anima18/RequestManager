package com.example.webserviceutil;

import android.content.Context;

import com.example.webserviceutil.OkHttp.OkHttpUtils;
import com.example.webserviceutil.callBack.BitmapCallBack;
import com.example.webserviceutil.callBack.CollectionCallBack;
import com.example.webserviceutil.callBack.ObjectCallBack;
import com.example.webserviceutil.callBack.ProgressCollectionCallBack;
import com.example.webserviceutil.callBack.ProgressObjectCallBack;
import com.example.webserviceutil.entity.WebServiceParam;
import com.example.webserviceutil.exception.ServiceErrorException;
import com.example.webserviceutil.service.BitMapService;
import com.example.webserviceutil.service.CollectionService;
import com.example.webserviceutil.service.ObjectService;
import com.example.webserviceutil.service.ProgressCollectionService;
import com.example.webserviceutil.service.ProgressObjectService;
import com.example.webserviceutil.service.Service;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.example.webserviceutil.service.Service.GET_TYPE;
import static com.example.webserviceutil.service.Service.POST_TYPE;
import static com.example.webserviceutil.service.Service.gson;

/**
 * 服务封装类，这里的服务是指网络服务，比如get/post请求。
 * @author 简建鸿 2016/6/3
 * @version 1.0
 */
public final class WebService {

    /**
     * 请求文件上传服务，显示上传进度，并返回单个对象。
     * @param param 请求参数
     * @param callBack 结果回调
     * @param <T> 结果对象类型
     * @return 订阅对象
     */
    public final static <T> Subscription uploadFile(Context context, WebServiceParam param, ProgressObjectCallBack<T> callBack) {
        return ProgressObjectService.getInstance().execute(context, param, callBack);
    }

    /**
     * 请求文件上传服务，显示上传进度，并返回对象集合
     * @param param 请求参数
     * @param callBack 结果回调
     * @param <T> 结果对象类型
     * @return 订阅对象
     */
    public final static <T> Subscription uploadFile(Context context, WebServiceParam param, ProgressCollectionCallBack<T> callBack) {
        return ProgressCollectionService.getInstance().execute(context, param, callBack);
    }

    /**
     * 请求对象集合服务
     * @param param 请求参数
     * @param callBack 结果回调
     * @param <T> 结果对象类型
     * @return 订阅对象
     */
    public final static <T> Subscription getCollection(Context context, WebServiceParam param, CollectionCallBack<T> callBack) {
        return CollectionService.getInstance().execute(context, param, callBack);
    }

    /**
     * 请求单个对象服务
     * @param param 请求参数
     * @param callBack 结果回调
     * @param <T> 结果对象类型
     * @return 订阅对象
     */
    public final static <T> Subscription getObject(Context context, WebServiceParam param, ObjectCallBack<T> callBack) {
        return ObjectService.getInstance().execute(context, param, callBack);
    }

    /**
     * 请求图片资源服务
     * @param url 请求图片的url
     * @param callBack 结果回调
     * @return 订阅对象
     */
    public static Subscription getBitMap(Context context, String url, BitmapCallBack callBack) {
        return BitMapService.getInstance().execute(context, url, callBack);
    }

    /**
     * 按顺序发送获取请求列表，每个请求都会回调方法；如果其中一个请求发送错误，则终止请求。
     * 这个方法没有对订阅关系进行管理。
     * @param context 上下文
     * @param params 请求列表
     * @param callBack 请求回调
     * @return Subscription 订阅对象
     */
    public static Subscription getObjectInSeq(final Context context, final List<WebServiceParam> params, final ObjectCallBack<Object> callBack) {
        Subscription subscription = Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                if(subscriber.isUnsubscribed())
                    return;
                try {
                    Call call = null;
                    for(WebServiceParam param : params) {
                        if(GET_TYPE.equals(param.getMethod())) {
                            call = OkHttpUtils.get(context, param.getRequestUrl());
                        }else if(POST_TYPE.equals(param.getMethod())) {
                            call = OkHttpUtils.post(context, param.getRequestUrl(), param.getParams(), null);
                        }
                        Response response = call.execute();
                        SubscriptionManager.addRequest(params.get(0), call);
                        if(response.isSuccessful()) {
                            JsonElement jsonElement = new JsonParser().parse(response.body().charStream());
                            if(jsonElement.isJsonObject()) {
                                subscriber.onNext(gson.fromJson(jsonElement.toString(), param.getClazz()));
                            }else if(jsonElement.isJsonArray()) {
                                JsonArray array = (JsonArray)jsonElement;
                                List<Object> resultList = new ArrayList<>();
                                for(JsonElement elem : array){
                                    resultList.add(gson.fromJson(elem, param.getClazz()));
                                }
                                subscriber.onNext(resultList);
                            }
                        }else {
                            subscriber.onError(new ServiceErrorException(response.code()));
                        }
                    }
                    subscriber.onCompleted();
                }catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(WebService.getObjectSubscriber(params.get(0), callBack));
        SubscriptionManager.addSubscription(params.get(0), subscription);
        return subscription;
    }

    /**
     * 获取请求的被观察者对象。
     * 这个方法没有对订阅关系进行管理。
     * @param context 上下文
     * @param param 请求列表
     * @return Observable 被观察者
     */
    public static Observable<Object> getObjectObservable(final Context context, final WebServiceParam param) {
        return Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                if(subscriber.isUnsubscribed())
                    return;
                try {
                    Call call = null;
                    if(GET_TYPE.equals(param.getMethod())) {
                        call = OkHttpUtils.get(context, param.getRequestUrl());
                    }else if(POST_TYPE.equals(param.getMethod())) {
                        call = OkHttpUtils.post(context, param.getRequestUrl(), param.getParams(), null);
                    }
                    Response response = call.execute();
                    if(response.isSuccessful()) {
                        JsonElement jsonElement = new JsonParser().parse(response.body().charStream());
                        if(jsonElement.isJsonObject()) {
                            subscriber.onNext(gson.fromJson(jsonElement.toString(), param.getClazz()));
                        }else if(jsonElement.isJsonArray()) {
                            JsonArray array = (JsonArray)jsonElement;
                            List<Object> resultList = new ArrayList<>();
                            for(JsonElement elem : array){
                                resultList.add(gson.fromJson(elem, param.getClazz()));
                            }
                            subscriber.onNext(resultList);
                        }
                        subscriber.onCompleted();
                    }else {
                        subscriber.onError(new ServiceErrorException(response.code()));
                    }
                }catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }
/*
    public static <T> Observable<List<T>> getCollectionObservable(final Context context, final WebServiceParam param) {
        return Observable.create(new Observable.OnSubscribe<List<T>>() {
            @Override
            public void call(Subscriber<? super List<T>> subscriber) {
                if(subscriber.isUnsubscribed())
                    return;
                try {
                    Call call = null;
                    if(GET_TYPE.equals(param.getMethod())) {
                        call = OkHttpUtils.get(context, param.getRequestUrl());
                    }else if(POST_TYPE.equals(param.getMethod())) {
                        call = OkHttpUtils.post(context, param.getRequestUrl(), param.getParams(), null);
                    }
                    //SubscriptionManager.addRequest(param, call);
                    Response response = call.execute();
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
                }catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        });
    }*/

    /**
     * 获取观察者对象
     * @param callBack 回调方法
     * @return Subscriber
     */
    public static Subscriber<Object> getObjectSubscriber(final WebServiceParam param, final ObjectCallBack<Object> callBack) {
        return new Subscriber<Object>() {
            @Override
            public void onCompleted() {
                SubscriptionManager.removeSubscription(param);
                callBack.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                SubscriptionManager.removeSubscription(param);
                Service.handleException(e, callBack);
                e.printStackTrace();
                onCompleted();
            }

            @Override
            public void onNext(Object o) {
                callBack.onSuccess(o);
            }
        };
    }
/*
    public static Subscriber<List<Object>> getCollectionSubscriber( final CollectionCallBack<Object> callBack) {
        return new Subscriber<List<Object>>() {
            @Override
            public void onCompleted() {
                callBack.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                Service.handleException(e, callBack);
                onCompleted();
                e.printStackTrace();
            }

            @Override
            public void onNext(List<Object> t) {
                callBack.onSuccess(t);
            }
        };
    }

    /**
     * 取消普通请求服务
     * @param subscription 订阅对象
     */
    public static void cancel(Subscription subscription) {
        SubscriptionManager.removeSubscription(subscription);
    }

    /**
     * 取消获取图片资源服务
     * @param url 请求图片的url
     */
    public static void cancel(String url) {
        SubscriptionManager.removeSubscription(url);
    }

    /**
     * 取消页面上所有的请求
     * @param tag
     */
    public static void cancel(Context tag) {
        OkHttpUtils.cancelTag(tag);
    }

}
