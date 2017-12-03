package com.ut.requsetmanager.manager;


import com.ut.requsetmanager.callback.DataListRequestCallback;
import com.ut.requsetmanager.callback.DataRequestCallback;
import com.ut.requsetmanager.request.NetworkRequest;

/**
 * Created by jianjianhong on 2017/11/23.
 */

public interface NetworkRequestTransportable {
    <T> NetworkRequestTransportable nest(NetworkRequestManager.NestFlatMapCallback callback);

    NetworkRequestTransportable sequence(NetworkRequest request);

    NetworkRequestTransportable merge(NetworkRequest request);

    <T> void subscribe(DataRequestCallback<T> callback);

    <T> void subscribe(DataListRequestCallback<T> callback);

}
