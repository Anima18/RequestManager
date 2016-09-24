package com.example.webserviceutil;

import android.content.Context;

import com.example.webserviceutil.OkHttp.OkHttpUtils;
import com.example.webserviceutil.callBack.BitmapCallBack;
import com.example.webserviceutil.callBack.CollectionCallBack;
import com.example.webserviceutil.callBack.ObjectCallBack;
import com.example.webserviceutil.callBack.ProgressCollectionCallBack;
import com.example.webserviceutil.callBack.ProgressObjectCallBack;
import com.example.webserviceutil.entity.WebServiceParam;
import com.example.webserviceutil.service.BitMapService;
import com.example.webserviceutil.service.CollectionService;
import com.example.webserviceutil.service.ObjectService;
import com.example.webserviceutil.service.ProgressCollectionService;
import com.example.webserviceutil.service.ProgressObjectService;

import rx.Subscription;

/**
 * 服务封装类，这里的服务是指网络服务，比如get/post请求。
 * @author 简建鸿 2016/6/3
 * @version 1.0
 */
public final class WebService {

    /**
     * 请求文件上传服务，显示上传进度，并返回单个对象。
     * @param param 请求参数
     * @param callBack 结果回调
     * @param <T> 结果对象类型
     * @return 订阅对象
     */
    public final static <T> Subscription updateFile(Context context, WebServiceParam param, ProgressObjectCallBack<T> callBack) {
        return ProgressObjectService.getInstance().execute(context, param, callBack);
    }

    /**
     * 请求文件上传服务，显示上传进度，并返回对象集合
     * @param param 请求参数
     * @param callBack 结果回调
     * @param <T> 结果对象类型
     * @return 订阅对象
     */
    public final static <T> Subscription updateFile(Context context, WebServiceParam param, ProgressCollectionCallBack<T> callBack) {
        return ProgressCollectionService.getInstance().execute(context, param, callBack);
    }

    /**
     * 请求对象集合服务
     * @param param 请求参数
     * @param callBack 结果回调
     * @param <T> 结果对象类型
     * @return 订阅对象
     */
    public final static <T> Subscription getCollection(Context context, WebServiceParam param, CollectionCallBack<T> callBack) {
        return CollectionService.getInstance().execute(context, param, callBack);
    }

    /**
     * 请求单个对象服务
     * @param param 请求参数
     * @param callBack 结果回调
     * @param <T> 结果对象类型
     * @return 订阅对象
     */
    public final static <T> Subscription getObject(Context context, WebServiceParam param, ObjectCallBack<T> callBack) {
        return ObjectService.getInstance().execute(context, param, callBack);
    }

    /**
     * 请求图片资源服务
     * @param url 请求图片的url
     * @param callBack 结果回调
     * @return 订阅对象
     */
    public static Subscription getBitMap(Context context, String url, BitmapCallBack callBack) {
        return BitMapService.getInstance().execute(context, url, callBack);
    }

    /**
     * 取消普通请求服务
     * @param subscription 订阅对象
     */
    public static void cancel(Subscription subscription) {
        SubscriptionManager.removeSubscription(subscription);
    }

    /**
     * 取消获取图片资源服务
     * @param url 请求图片的url
     */
    public static void cancel(String url) {
        SubscriptionManager.removeSubscription(url);
    }

    /**
     * 取消页面上所有的请求
     * @param tag
     */
    public static void cancel(Context tag) {
        OkHttpUtils.cancelTag(tag);
    }

}
