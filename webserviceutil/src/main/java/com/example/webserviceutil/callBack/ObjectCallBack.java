package com.example.webserviceutil.callBack;

/**
 * 发送获取单个对象请求的回调接口，继承接口{@link CallBack}
 * @param <T> 请求数据的类型
 * @author 简建鸿 2016/6/2
 * @version 1.0
 */
public interface ObjectCallBack<T> extends CallBack {
    /**
     * 请求成功的回调方法
     * @param data 请求返回的数据
     */
    void onSuccess(T data);
}
