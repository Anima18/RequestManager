package com.ut.requsetmanager.callback;


/**
 * 文件上传回调，继承接口{@link DataRequestCallback}
 * @author 简建鸿 2016/6/14
 * @version 1.0
 */
public interface ProgressRequestCallback<T> extends DataRequestCallback {

    /**
     * 上传进度回调方法
     * @param fileName 文件名称
     * @param progress 上传进度
     */
    void onProgress(String fileName, int progress);

}
