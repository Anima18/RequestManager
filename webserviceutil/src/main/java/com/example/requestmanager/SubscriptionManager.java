package com.example.requestmanager;

import android.util.ArrayMap;
import android.util.Log;

import com.example.requestmanager.okhttp.OkHttpUtils;
import com.example.requestmanager.entity.WebServiceParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import rx.Subscription;

/**
 * RxJava的关联和OKHttp请求管理类
 * @author 简建鸿 2016/6/8
 * @version 1.0
 */
public final class SubscriptionManager {
    private final static String TAG = "WebService";
    //普通请求管理
    private static Map<WebServiceParam, Subscription> paramSubMap = new ArrayMap<>();
    private static Map<Subscription, List<Call>> subCallMap = new ArrayMap<>();

    //图片请求管理
    private static Map<String, Subscription> urlSubMap = new ArrayMap<>();

    /**
     * 管理普通请求
     * @param param 请求参数对象
     * @param subscription 订阅对象
     */
    public static void addSubscription(WebServiceParam param, Subscription subscription) {
        paramSubMap.put(param, subscription);
        Log.d(TAG, "paramSubMap add subscription, size="+ paramSubMap.size());
    }

    /**
     * 管理图片请求
     * @param url 图片url
     * @param subscription 订阅对象
     */
    public static void addSubscription(String url, Subscription subscription) {
        urlSubMap.put(url, subscription);
        Log.d(TAG, "SubscriptionManager add subscription");
    }

    /**
     * 把subscription和call映射起来
     * @param param 请求参数对象
     * @param call 请求对象
     */
    public static void addRequest(WebServiceParam param, Call call) {
        if(paramSubMap.containsKey(param)) {
            List<Call> callList = subCallMap.get(paramSubMap.get(param));
            if(callList == null) {
                callList = new ArrayList<>();
                callList.add(call);
                subCallMap.put(paramSubMap.get(param), callList);
            }else {
                callList.add(call);
            }
            Log.d(TAG, "this call size = "+callList.size());
            Log.d(TAG, "subCallMap add call, size = "+subCallMap.size());
        }
    }

    /**
     * 把subscription和call映射起来
     * @param url 图片url
     * @param call 请求对象
     */
    public static void addRequest(String url, Call call) {
        if(urlSubMap.containsKey(url)) {
            //subCallMap.put(urlSubMap.get(url), call);
            List<Call> callList = subCallMap.get(urlSubMap.get(url));
            if(callList == null) {
                callList = new ArrayList<>();
                callList.add(call);
                subCallMap.put(urlSubMap.get(url), callList);
            }else {
                callList.add(call);
            }
            Log.d(TAG, "SubscriptionManager add Request");
        }
    }

    /**
     * 删除RxJava的订阅和OKHttp请求的管理
     * 这种情况出现在OKHttp请求和RxJava订阅结束后
     * @param param 请求参数
     */
    public static void removeSubscription(WebServiceParam param) {
        Log.d(TAG, "================removeSubscription by param========================");
        subCallMap.remove(paramSubMap.get(param));
        paramSubMap.remove(param);

        Log.d(TAG, "subCallMap remove subscription");
        Log.d(TAG, "paramSubMap remove param");
        //Log.d(TAG, "urlSubMap size :" + urlSubMap.size());
        Log.d(TAG, "subCallMap size :" + subCallMap.size());
        Log.d(TAG, "paramSubMap size :" + paramSubMap.size());
    }

    /**
     * 取消subscription和call的管理
     * @param url 图片url
     */
    public static void removeSubscription(String url) {
        Subscription subscription = urlSubMap.get(url);

        List<Call> callList = subCallMap.get(subscription);
        if(callList != null) {
            for(Call call : callList) {
                OkHttpUtils.cancelCall(call);
            }
        }

        if(subscription != null && subscription.isUnsubscribed()) {
            subscription.unsubscribe();
            Log.d(TAG, "subscription is unsubscribe");
        }
        urlSubMap.remove(url);
        subCallMap.remove(subscription);
        Log.d(TAG, "SubscriptionManager remove subscription");
        Log.d(TAG, "SubscriptionManager remove Request");
        Log.d(TAG, "urlSubMap size :" + urlSubMap.size());
        Log.d(TAG, "subCallMap size :" + subCallMap.size());
    }

    /**
     * 取消subscription和call的管理
     * @param subscription subscription订阅对象
     */
    public static void removeSubscription(Subscription subscription) {
        Log.d(TAG, "================removeSubscription by subscription========================");
        if(subscription != null && !subscription.isUnsubscribed()/* && subCallMap.containsKey(subscription)*/) {
            subscription.unsubscribe();
            Log.d(TAG, "subscription is unsubscribe");

            List<Call> callList = subCallMap.get(subscription);
            if(callList != null) {
                for(Call call : callList) {
                    OkHttpUtils.cancelCall(call);
                }
            }

            //remove paramSubMap
            List<WebServiceParam> params = getKeyListByValue(paramSubMap, subscription);
            if(params != null) {
                for(WebServiceParam param : params) {
                    paramSubMap.remove(param);
                }
            }

            //remove urlSubMap
            String url = getKeyByValue(urlSubMap, subscription);
            urlSubMap.remove(url);
            subCallMap.remove(subscription);
            Log.d(TAG, "subCallMap remove subscription");
            Log.d(TAG, "paramSubMap remove param");
            //Log.d(TAG, "urlSubMap size :" + urlSubMap.size());
            Log.d(TAG, "subCallMap size :" + subCallMap.size());
            Log.d(TAG, "paramSubMap size :" + paramSubMap.size());
        }
    }

    public static void removeSubscription(Call call) {
        if(call != null) {
            Subscription subscription = null;
            for (Map.Entry<Subscription, List<Call>> entry : subCallMap.entrySet()) {
                if (entry.getValue().contains(call)) {
                    subscription = entry.getKey();
                    break;
                }
            }
            removeSubscription(subscription);
        }
    }

    private static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private static <T, E> List<T> getKeyListByValue(Map<T, E> map, E value) {
        List<T> list = new ArrayList<>();
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (entry.getValue().equals(value)) {
                list.add(entry.getKey());
            }
        }
        return list;
    }

    public static boolean isContainUrl(String url) {
        return urlSubMap.containsKey(url);
    }
}
