package com.example.requestmanager;

import android.text.TextUtils;

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
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.android.ActivityEvent;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
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
        this.param.setProvider(builder.lifecycleProvider);
        if(builder.param != null) {
            this.param.addParam(builder.param);
        }
        this.paramList = builder.params;
    }

    public static class Builder<T> implements NetworkRequestApi {
        private LifecycleProvider lifecycleProvider;
        private String url;
        private Class aClass;
        private Type type;
        private String method;
        private Object tag;
        private Map<String, Object> param;
        private List<WebServiceParam> params;

        public Builder() {}

        public Builder(LifecycleProvider lifecycleProvider) {
            this.lifecycleProvider = lifecycleProvider;
        }

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
        public void uploadFile(ProgressCallBack progressCallBack) {
            NetworkRequest request2 = build();
            request2.uploadFile(progressCallBack);
        }

        @Override
        public void downloadFile(ProgressCallBack progressCallBack) {
            build().downloadFile(progressCallBack);
        }

        @Override
        public <T> void call(DataCallBack<T> dataCallBack) {
            NetworkRequest request2 = build();
            request2.call(dataCallBack);
        }

        @Override
        public void getBitMap(BitmapCallBack bitmapCallBack) {
            build().getBitMap(bitmapCallBack);
        }

        @Override
        public <T> void getSeqData(DataCallBack<T> dataCallBack) {
            build().getSeqData(dataCallBack);
        }

        @Override
        public Observable<T> request() {
            NetworkRequest request2 = build();
            return request2.request();
        }
    }

    private void checkParam(CallBack callBack) {
        if(TextUtils.isEmpty(param.getRequestUrl())) {
            throw new Error("NetworkRequest url is null");
        }else if(param.getClazz() == null && param.getClassType() == null) {
            throw new Error("NetworkRequest dataType is null");
        }else if(!GET_TYPE.equals(param.getMethod()) && !POST_TYPE.equals(param.getMethod())) {
            throw new Error("NetworkRequest method is neither POST nor GET");
        }else if(callBack == null) {
            throw new Error("NetworkRequest callBack is null");
        }
    }

    public void uploadFile(ProgressCallBack progressCallBack) {
        checkParam(progressCallBack);
        ProgressObjectService.getInstance().execute(param, progressCallBack);
    }

    public void downloadFile(ProgressCallBack progressCallBack) {
        checkParam(progressCallBack);
        DownloadFileService.getInstance().execute(param, progressCallBack);
    }

    public <T> void call(DataCallBack<T> dataCallBack) {
        checkParam(dataCallBack);
        DataService.getInstance().execute(param, dataCallBack);
    }

    public void getBitMap(BitmapCallBack bitmapCallBack) {
        if(TextUtils.isEmpty(param.getRequestUrl())) {
            throw new Error("NetworkRequest url is null");
        }else if(bitmapCallBack == null) {
            throw new Error("NetworkRequest callBack is null");
        }
        BitMapService.getInstance().execute(param, bitmapCallBack);
    }

    public <T> void getSeqData(DataCallBack<T> dataCallBack) {
        Observable.create(new Observable.OnSubscribe<Object>() {
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
                        //SubscriptionManager.addRequest(param, call);
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
        })
                .compose(param.getProvider().bindUntilEvent(ActivityEvent.PAUSE))
                .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(seqResponse(paramList, (DataCallBack<List<Object>>)dataCallBack));

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
                callBack.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
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
                    //SubscriptionManager.addRequest(param, call);
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
}
