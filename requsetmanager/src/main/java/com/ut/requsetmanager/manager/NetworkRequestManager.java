package com.ut.requsetmanager.manager;


import com.ut.requsetmanager.callback.DataListRequestCallback;
import com.ut.requsetmanager.callback.DataRequestCallback;
import com.ut.requsetmanager.entity.ResponseStatus;
import com.ut.requsetmanager.entity.WebServiceError;
import com.ut.requsetmanager.request.NetworkRequest;
import com.ut.requsetmanager.util.JsonUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by jianjianhong on 2017/11/23.
 */

public class NetworkRequestManager implements NetworkRequestTransportable {
    private final static String TAG = "NetworkRequestManager";
    private Observable observable;
    private List<NetworkRequest> requestList;
    private Subscription subscription;

    private NetworkRequestManager() {
        this.requestList = new ArrayList<>();
    }

    public static NetworkRequestManager create(NetworkRequest request) {
        NetworkRequestManager manager = new NetworkRequestManager();
        manager.observable = request.getObservable();
        manager.requestList.add(request);
        return manager;
    }

    @Override
    public <T> NetworkRequestTransportable nest(final NestFlatMapCallback callback) {
        final NetworkRequestManager manager = this;
        this.observable = observable.flatMap(new Func1<Map<String, T>, Observable<?>>() {
            @Override
            public Observable call(Map<String, T> data) {
                NetworkRequest request = callback.flatMap(data.get(requestList.get(requestList.size()-1).toString()), new ResponseStatus(200, ""), manager);
                requestList.add(request);
                return request.getObservable();
            }
        });
        return this;
    }

    @Override
    public NetworkRequestTransportable sequence(NetworkRequest request) {
        this.observable = this.observable.concatWith(request.getObservable());
        this.requestList.add(request);
        return this;
    }

    @Override
    public NetworkRequestTransportable merge(NetworkRequest request) {
        this.observable = this.observable.mergeWith(request.getObservable());
        this.requestList.add(request);
        return this;
    }

    @Override
    public <T> void subscribe(final DataRequestCallback<T> callback) {
        this.subscription = this.observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Map<String, T>>() {
                    @Override
                    public void onCompleted() {
                        //NetworkProgressDialogImpl.getInstance().hideProgress();
                    }

                    @Override
                    public void onError(Throwable e) {
                        JsonUtil.handleException(e, callback);
                        cancelSubscription();
                    }

                    @Override
                    public void onNext(Map<String, T> t) {
                        callback.onResult(t.get(requestList.get(requestList.size()-1).toString()), new ResponseStatus(200, ""));
                    }
                });
    }

    @Override
    public <T> void subscribe(final DataListRequestCallback<T> callback) {
        final List<T> dataList = new ArrayList<>();
        for(int i = 0; i < requestList.size(); i++) {
            dataList.add(null);
        }
        this.subscription = this.observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Map<String, T>>() {
                    int responseCount = 0;
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        WebServiceError error = JsonUtil.getError(e);
                        callback.onResult(null, new ResponseStatus(error.getCode(), error.getMessage()));
                        cancelSubscription();

                    }

                    @Override
                    public void onNext(Map<String, T> data) {

                        for(int i = 0; i < requestList.size(); i++) {
                            String key = requestList.get(i).toString();
                            if(data.containsKey(key)) {
                                dataList.set(i, data.get(key));
                                responseCount++;
                                break;
                            }
                        }
                        if(responseCount == requestList.size()) {
                            callback.onResult(dataList, new ResponseStatus(200, ""));
                        }
                    }
                });
    }

    public void cancelSubscription() {
        this.subscription.unsubscribe();
        for(NetworkRequest request : requestList) {
            request.hideProgress();
        }
    }

    public interface NestFlatMapCallback<T> {
        NetworkRequest flatMap(T t, ResponseStatus status, NetworkRequestManager manager);
    }
}
