package com.example.requestmanager;

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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.example.requestmanager.service.Service.gson;

/**
 * Created by jianjianhong on 2016/10/26.
 */
public class NetworkRequest<T> implements NetworkRequestApi {
    public final static String POST_TYPE = "POST";
    public final static String GET_TYPE = "GET";
    /**
     * 单个请求参数
     */
    private WebServiceParam param = new WebServiceParam();
    /**
     * 多个请求列表，用于顺序请求
     */
    private List<WebServiceParam> paramList = new ArrayList<>();

    private NetworkRequest(Builder builder) {
        this.param.setRequestUrl(builder.url);
        this.param.setClassType(builder.type);
        this.param.setClazz(builder.aClass);
        this.param.setMethod(builder.method);
        this.param.setTag(builder.tag);
        if(builder.param != null) {
            this.param.addParam(builder.param);
        }
        this.paramList = builder.params;
    }

    public static class Builder<T> implements NetworkRequestApi {
        private String url;
        private Class aClass;
        private Type type;
        private String method;
        private Object tag;
        private Map<String, Object> param;
        private List<WebServiceParam> params;

        public Builder url(String url) {
            this.url = url;
            return this;
        }
        public Builder dataClass(Class aClass) {
            this.aClass = aClass;
            return this;
        }

        public Builder dataType(Type type) {
            this.type = type;
            return this;
        }

        public Builder tag(Object tag) {
            this.tag = tag;
            return this;
        }

        public Builder method(String method) {
           this.method = method;
            return this;
        }
        public Builder param(String key, Object value) {
            if(this.param == null) {
                param = new HashMap<>();
            }
            this.param.put(key, value);
            return this;
        }

        public Builder param(Map<String, Object> param) {
            if(this.param == null) {
                this.param = new HashMap<>();
            }
            this.param.putAll(param);
            return this;
        }

        public Builder params(WebServiceParam param) {
            if(this.params == null) {
                this.params = new ArrayList<>();
            }
            this.params.add(param);
            return this;
        }

        public Builder params(List<WebServiceParam> params) {
            if(this.params == null) {
                this.params = new ArrayList<>();
            }
            this.params.addAll(params);
            return this;
        }

        public NetworkRequest build() {
            return new NetworkRequest(this);
        }

        @Override
        public Subscription uploadFile(ProgressCallBack progressCallBack) {
            NetworkRequest request2 = build();
            return request2.uploadFile(progressCallBack);
        }

        @Override
        public Subscription downloadFile(ProgressCallBack progressCallBack) {
            NetworkRequest request2 = build();
            return request2.downloadFile(progressCallBack);
        }

        @Override
        public <T> Subscription call(DataCallBack<T> dataCallBack) {
            NetworkRequest request2 = build();
            return request2.call(dataCallBack);
        }

        @Override
        public Subscription getBitMap(BitmapCallBack bitmapCallBack) {
            NetworkRequest request2 = build();
            return request2.getBitMap(bitmapCallBack);
        }

        @Override
        public <T> Subscription getSeqData(DataCallBack<T> dataCallBack) {
            NetworkRequest request2 = build();
            return request2.getSeqData(dataCallBack);
        }

        @Override
        public Observable<T> request() {
            NetworkRequest request2 = build();
            return request2.request();
        }
    }

    private void checkParam(CallBack callBack) {
        if(StringUtil.isEmpty(param.getRequestUrl())) {
            throw new Error("NetworkRequest url is null");
        }else if(param.getClazz() == null && param.getClassType() == null) {
            throw new Error("NetworkRequest dataType is null");
        }else if(!GET_TYPE.equals(param.getMethod()) && !POST_TYPE.equals(param.getMethod())) {
            throw new Error("NetworkRequest method is neither POST nor GET");
        }else if(callBack == null) {
            throw new Error("NetworkRequest callBack is null");
        }
    }

    public Subscription uploadFile(ProgressCallBack progressCallBack) {
        checkParam(progressCallBack);
        return ProgressObjectService.getInstance().execute(param, progressCallBack);
    }

    public Subscription downloadFile(ProgressCallBack progressCallBack) {
        checkParam(progressCallBack);
        return DownloadFileService.getInstance().execute(param, progressCallBack);
    }

    public <T> Subscription call(DataCallBack<T> dataCallBack) {
        checkParam(dataCallBack);
        return DataService.getInstance().execute(param, dataCallBack);
    }

    public Subscription getBitMap(BitmapCallBack bitmapCallBack) {
        if(StringUtil.isEmpty(param.getRequestUrl())) {
            throw new Error("NetworkRequest url is null");
        }else if(bitmapCallBack == null) {
            throw new Error("NetworkRequest callBack is null");
        }
        return BitMapService.getInstance().execute(param, bitmapCallBack);
    }

    public <T> Subscription getSeqData(DataCallBack<T> dataCallBack) {
        Subscription subscription = Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                if(subscriber.isUnsubscribed())
                    return;
                try {
                    Call call = null;
                    for(WebServiceParam param : paramList) {
                        if(GET_TYPE.equals(param.getMethod())) {
                            call = OkHttpUtils.get(param.getRequestUrl());
                        }else if(POST_TYPE.equals(param.getMethod())) {
                            call = OkHttpUtils.post(param.getRequestUrl(), param.getParams(), null);
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

    /**
     * 获取请求的被观察者对象。
     * 这个方法没有对订阅关系进行管理。
     * @return Observable 被观察者
     */
    public Observable<T> request() {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                if(subscriber.isUnsubscribed())
                    return;
                try {
                    Call call = null;
                    if(GET_TYPE.equals(param.getMethod())) {
                        call = OkHttpUtils.get(param.getRequestUrl());
                    }else if(POST_TYPE.equals(param.getMethod())) {
                        call = OkHttpUtils.post(param.getRequestUrl(), param.getParams(), null);
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

    /**
     * 取消普通请求服务
     * @param subscription 订阅对象
     */
    public static void cancel(Subscription subscription) {
        SubscriptionManager.removeSubscription(subscription);
    }

    /*public static void cancelAll(Object tag) {
        OkHttpUtils.cancelTag(tag);
    }*/

}
