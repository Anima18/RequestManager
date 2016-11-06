package com.example.requestmanager.callBack;

/**
 * 网络请求的回调接口
 * DataCallBack、CollectionCallBack、BitmapCallBack都继承CallBack
 * @author 简建鸿 2016/6/2
 * @version 1.0
 */
public interface CallBack<T> {
    /**
     * 得到服务端错误响应的回调方法
     * @param code  异常code
     * @param message 异常信息
     */
    void onFailure(int code, String message);

    void onCompleted();
}
