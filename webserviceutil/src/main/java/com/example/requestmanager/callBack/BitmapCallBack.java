package com.example.requestmanager.callBack;

import android.graphics.Bitmap;

/**
 * 获取图片资源的回调接口，继承接口{@link CallBack}
 * @author 简建鸿 2016/6/2
 */
public interface BitmapCallBack extends CallBack {
    /**
     * 成功获取图片资源的回调方法
     * @param url 请求url
     * @param bitmap 图片资源
     */
    void onSuccess(String url, Bitmap bitmap);
}
