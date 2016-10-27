package com.example.requestmanager;

import android.content.Context;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.example.requestmanager.callBack.BitmapCallBack;
import com.example.requestmanager.callBack.CallBack;
import com.example.requestmanager.callBack.DataCallBack;
import com.example.requestmanager.callBack.ProgressCallBack;
import com.example.requestmanager.entity.WebServiceParam;
import com.example.requestmanager.exception.ServiceErrorException;
import com.example.requestmanager.okhttp.OkHttpUtils;
import com.example.requestmanager.service.BitMapService;
import com.example.requestmanager.service.DataService;
import com.example.requestmanager.service.DownloadFileService;
import com.example.requestmanager.service.ProgressObjectService;
import com.example.requestmanager.service.Service;
import com.example.requestmanager.util.StringUtil;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static com.example.requestmanager.service.Service.GET_TYPE;
import static com.example.requestmanager.service.Service.POST_TYPE;
import static com.example.requestmanager.service.Service.gson;

/**
 * Created by jianjianhong on 2016/10/26.
 */
public class NetworkRequest<T> {
    private Context context;
    /**
     * 单个请求参数
     */
    private WebServiceParam param = new WebServiceParam();
    /**
     * 多个请求列表，用于顺序请求
     */
    private List<WebServiceParam> paramList = new ArrayList<>();
    /**
     * 请求回调
     */
    private DataCallBack<T> dataCallBack;
    /**
     * 有进度条的请求回调，用于上传和下载
     */
    private ProgressCallBack<T> progressCallBack;
    /**
     * 获取图片回调
     */
    private BitmapCallBack bitmapCallBack;

    private Observable<T> observable;

    private NetworkRequest() {}

    private NetworkRequest(DataCallBack<T> callBack) {
        this.dataCallBack = callBack;
    }

    private NetworkRequest(ProgressCallBack<T> progressCallBack) {
        this.progressCallBack = progressCallBack;
    }

    private NetworkRequest(BitmapCallBack bitmapCallBack) {
        this.bitmapCallBack = bitmapCallBack;
    }

    public static NetworkRequest create() {
        return new NetworkRequest();
    }

    public static <T> NetworkRequest<T> create(DataCallBack<T> callBack) {
        return new NetworkRequest<>(callBack);
    }

    public static <T> NetworkRequest<T> create(ProgressCallBack<T> callBack) {
        return new NetworkRequest<>(callBack);
    }

    public static  NetworkRequest create(BitmapCallBack callBack) {
        return new NetworkRequest(callBack);
    }

    public NetworkRequest setContext(Context context) {
        this.context = context;
        return this;
    }
    public NetworkRequest setUrl(String url) {
        param.setRequestUrl(url);
        return this;
    }
    public NetworkRequest setDataClass(Class dataClass) {
        param.setClazz(dataClass);
        return this;
    }

    public NetworkRequest setDataType(Type type) {
        param.setClassType(type);
        return this;
    }

    public NetworkRequest setMethod(String method) {
        param.setMethod(method);
        return this;
    }
    public NetworkRequest addParam(String key, Object value) {
        param.addParam(key, value);
        return this;
    }

    public NetworkRequest setParam(Map<String, Object> params) {
        param.addParam(params);
        return this;
    }

    public NetworkRequest addWebServerParam(WebServiceParam param) {
        paramList.add(param);
        return this;
    }

    public NetworkRequest setWebServerParamList(List<WebServiceParam> params) {
        paramList.addAll(params);
        return this;
    }

    private void checkParam(CallBack callBack) {
        if(context == null) {
            throw new Error("NetworkRequest context is null");
        }else if(StringUtil.isEmpty(param.getRequestUrl())) {
            throw new Error("NetworkRequest url is null");
        }else if(param.getClazz() == null && param.getClassType() == null) {
            throw new Error("NetworkRequest dataType is null");
        }else if(!Service.GET_TYPE.equals(param.getMethod()) && !Service.POST_TYPE.equals(param.getMethod())) {
            throw new Error("NetworkRequest method is neither POST nor GET");
        }else if(callBack == null) {
            throw new Error("NetworkRequest callBack is null");
        }
    }

    public Subscription uploadFile() {
        param.setMethod(Service.POST_TYPE);
        checkParam(progressCallBack);
        return ProgressObjectService.getInstance().execute(context, param, progressCallBack);
    }

    public Subscription downloadFile() {
        param.setMethod(Service.POST_TYPE);
        checkParam(progressCallBack);
        return DownloadFileService.getInstance().execute(context, param, progressCallBack);
    }

    public Subscription getData() {
        param.setMethod(Service.GET_TYPE);
        checkParam(dataCallBack);
        return DataService.getInstance().execute(context, param, dataCallBack);
    }

    public Subscription postData() {
        param.setMethod(Service.POST_TYPE);
        checkParam(dataCallBack);
        return DataService.getInstance().execute(context, param, dataCallBack);
    }

    public Subscription getBitMap() {
        if(context == null) {
            throw new Error("NetworkRequest context is null");
        }else if(StringUtil.isEmpty(param.getRequestUrl())) {
            throw new Error("NetworkRequest url is null");
        }else if(bitmapCallBack == null) {
            throw new Error("NetworkRequest callBack is null");
        }
        return BitMapService.getInstance().execute(context, param.getRequestUrl(), bitmapCallBack);
    }

    public Subscription getObjectInSeq() {
        Subscription subscription = Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                if(subscriber.isUnsubscribed())
                    return;
                try {
                    Call call = null;
                    for(WebServiceParam param : paramList) {
                        if(GET_TYPE.equals(param.getMethod())) {
                            call = OkHttpUtils.get(context, param.getRequestUrl());
                        }else if(POST_TYPE.equals(param.getMethod())) {
                            call = OkHttpUtils.post(context, param.getRequestUrl(), param.getParams(), null);
                        }
                        Response response = call.execute();
                        SubscriptionManager.addRequest(param, call);
                        if(response.isSuccessful()) {
                            JsonElement jsonElement = new JsonParser().parse(response.body().charStream());
                            if(jsonElement.isJsonObject()) {
                                Service.rebuildJsonObj((JsonObject)jsonElement);
                            }
                            response.body().close();
                            if(param.getClassType() != null) {
                                subscriber.onNext((T)gson.fromJson(jsonElement.toString(), param.getClassType()));
                            }else if((param.getClazz() != null)) {
                                subscriber.onNext((T)gson.fromJson(jsonElement.toString(), param.getClazz()));
                            }
                        }else {
                            subscriber.onError(new ServiceErrorException(response.code()));
                        }
                        response.body().close();
                    }
                    subscriber.onCompleted();
                }catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(seqResponse(paramList, (DataCallBack<List<Object>>)dataCallBack));

        for(WebServiceParam param : paramList) {
            SubscriptionManager.addSubscription(param, subscription);
        }
        return subscription;
    }

    /**
     * 获取观察者对象
     * @param callBack 回调方法
     * @return Subscriber
     */
    private Subscriber<Object> seqResponse(final List<WebServiceParam> paramList, final DataCallBack<List<Object>> callBack) {
        return new Subscriber<Object>() {
            List<Object> dataList = new ArrayList<>();
            @Override
            public void onCompleted() {
                for(WebServiceParam param : paramList) {
                    SubscriptionManager.removeSubscription(param);
                }
                callBack.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                for(WebServiceParam param : paramList) {
                    SubscriptionManager.removeSubscription(param);
                }
                Service.handleException(e, callBack);
                e.printStackTrace();
                onCompleted();
            }

            @Override
            public void onNext(Object o) {
                dataList.add(o);
                if(dataList.size() == paramList.size()) {
                    callBack.onSuccess(dataList);
                }
            }
        };
    }

    public Subscription response(final DataCallBack<T> callBack) {
        return observable.observeOn(AndroidSchedulers.mainThread())
        .subscribe(
                new Subscriber<T>() {
                    @Override
                    public void onCompleted() {
                        callBack.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Service.handleException(e, callBack);
                        e.printStackTrace();
                        onCompleted();
                    }

                    @Override
                    public void onNext(T t) {
                        callBack.onSuccess(t);
                    }
                }
        );
    }

    /**
     * 获取请求的被观察者对象。
     * 这个方法没有对订阅关系进行管理。
     * @return Observable 被观察者
     */
    public NetworkRequest request() {
        observable = Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
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
                            Service.rebuildJsonObj((JsonObject)jsonElement);
                        }
                        response.body().close();
                        if(param.getClassType() != null) {
                            subscriber.onNext((T)gson.fromJson(jsonElement.toString(), param.getClassType()));
                        }else if((param.getClazz() != null)) {
                            subscriber.onNext((T)gson.fromJson(jsonElement.toString(), param.getClazz()));
                        }
                        subscriber.onCompleted();
                    }else {
                        subscriber.onError(new ServiceErrorException(response.code()));
                    }
                }catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.io());
        return this;
    }

    public NetworkRequest flatMap(Func1<T, Observable<?>> func1) {
        observable.flatMap(func1);
        return this;
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

    public Observable<T> getObservable() {
        return observable;
    }
}
