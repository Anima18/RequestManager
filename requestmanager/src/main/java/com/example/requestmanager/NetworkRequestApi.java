package com.example.requestmanager;

import com.example.requestmanager.callBack.BitmapCallBack;
import com.example.requestmanager.callBack.DataCallBack;
import com.example.requestmanager.callBack.ProgressCallBack;

import rx.Observable;
import rx.Subscription;

/**
 * Created by jianjianhong on 2016/11/7.
 */
public interface NetworkRequestApi {
    void uploadFile(ProgressCallBack progressCallBack);
    void downloadFile(ProgressCallBack progressCallBack);
    <T> void call(DataCallBack<T> dataCallBack);
    void getBitMap(BitmapCallBack bitmapCallBack);
    <T> void getSeqData(DataCallBack<T> dataCallBack);
    <T> Observable<T> request();
}
