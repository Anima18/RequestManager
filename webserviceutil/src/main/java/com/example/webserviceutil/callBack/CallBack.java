package com.example.webserviceutil.callBack;

/**
 * 网络请求的回调接口
 * ObjectCallBack、CollectionCallBack、BitmapCallBack都继承CallBack
 * @author 简建鸿 2016/6/2
 * @version 1.0
 */
public interface CallBack<T> {
    /**
     * 请求失败的回调方法
     * @param code  异常code
     * @param message 异常信息
     */
    void onFailure(int code, String message);

    /**
     * 请求结束的回调方法.
     */
    void onCompleted();
}
