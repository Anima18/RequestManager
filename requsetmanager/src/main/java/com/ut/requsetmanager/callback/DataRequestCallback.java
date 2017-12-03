package com.ut.requsetmanager.callback;

import com.ut.requsetmanager.entity.ResponseStatus;

/**
 * 网络请求的回调接口
 * DataCallBack、CollectionCallBack、BitmapCallBack都继承CallBack
 * @author 简建鸿 2016/6/2
 * @version 1.0
 */
public interface DataRequestCallback<T> {
    void onResult(T data, ResponseStatus status);
}
