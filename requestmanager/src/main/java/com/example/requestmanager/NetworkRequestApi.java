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
    Subscription uploadFile(ProgressCallBack progressCallBack);
    Subscription downloadFile(ProgressCallBack progressCallBack);
    <T> Subscription call(DataCallBack<T> dataCallBack);
    Subscription getBitMap(BitmapCallBack bitmapCallBack);
    <T> Subscription getSeqData(DataCallBack<T> dataCallBack);
    <T> Observable<T> request();
}
