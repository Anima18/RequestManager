package com.example.webserviceutil.callBack;


/**
 * 文件上传回调，继承接口{@link CallBack}
 * @param <T> 请求数据的类型
 * @author 简建鸿 2016/6/14
 * @version 1.0
 */
public interface ProgressObjectCallBack<T> extends CallBack {

    /**
     * 上传进度回调方法
     * @param fileName 文件名称
     * @param progress 上传进度
     */
    void onProgress(String fileName, int progress);

    /**
     * 上传完成回调方法
     * @param data 返回对象
     */
    void onSuccess(T data);
}
