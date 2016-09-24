package com.example.webserviceutil.callBack;

/**
 * 获取一个对象的回调接口，继承接口{@link CallBack}
 * @author 简建鸿 2016/6/2
 * @version 1.0
 */
public interface ObjectCallBack<T> extends CallBack {
    /**
     * 成功获取对象的回调方法
     * @param data 对象数据
     */
    void onSuccess(T data);
}
