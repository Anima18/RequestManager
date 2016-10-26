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
    private static Map<WebServiceParam, Subscription> webServiceParamSubMap = new ArrayMap<>();
    private static Map<Subscription, List<Call>> subscriptionCallMap = new ArrayMap<>();

    //图片请求管理
    private static Map<String, Subscription> urlSubMap = new ArrayMap<>();

    /**
     * 管理普通请求
     * @param param 请求参数对象
     * @param subscription 订阅对象
     */
    public static void addSubscription(WebServiceParam param, Subscription subscription) {
        webServiceParamSubMap.put(param, subscription);
        Log.d(TAG, "SubscriptionManager add subscription");
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
        if(webServiceParamSubMap.containsKey(param)) {
            List<Call> callList = subscriptionCallMap.get(webServiceParamSubMap.get(param));
            if(callList == null) {
                callList = new ArrayList<>();
                callList.add(call);
                subscriptionCallMap.put(webServiceParamSubMap.get(param), callList);
            }else {
                callList.add(call);
            }
            Log.d(TAG, "SubscriptionManager add Request");
        }
    }

    /**
     * 把subscription和call映射起来
     * @param url 图片url
     * @param call 请求对象
     */
    public static void addRequest(String url, Call call) {
        if(urlSubMap.containsKey(url)) {
            //subscriptionCallMap.put(urlSubMap.get(url), call);
            List<Call> callList = subscriptionCallMap.get(urlSubMap.get(url));
            if(callList == null) {
                callList = new ArrayList<>();
                callList.add(call);
                subscriptionCallMap.put(urlSubMap.get(url), callList);
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
        subscriptionCallMap.remove(webServiceParamSubMap.get(param));
        webServiceParamSubMap.remove(param);

        Log.d(TAG, "SubscriptionManager remove subscription");
        Log.d(TAG, "SubscriptionManager remove Request");
        Log.d(TAG, "urlSubMap size :" + urlSubMap.size());
        Log.d(TAG, "subscriptionCallMap size :" + subscriptionCallMap.size());
    }

    /**
     * 取消subscription和call的管理
     * @param url 图片url
     */
    public static void removeSubscription(String url) {
        Subscription subscription = urlSubMap.get(url);

        List<Call> callList = subscriptionCallMap.get(subscription);
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
        subscriptionCallMap.remove(subscription);
        Log.d(TAG, "SubscriptionManager remove subscription");
        Log.d(TAG, "SubscriptionManager remove Request");
        Log.d(TAG, "urlSubMap size :" + urlSubMap.size());
        Log.d(TAG, "subscriptionCallMap size :" + subscriptionCallMap.size());
    }

    /**
     * 取消subscription和call的管理
     * @param subscription subscription订阅对象
     */
    public static void removeSubscription(Subscription subscription) {
        if(subscription != null && !subscription.isUnsubscribed() && subscriptionCallMap.containsKey(subscription)) {
            subscription.unsubscribe();
            Log.d(TAG, "subscription is unsubscribe");

            List<Call> callList = subscriptionCallMap.get(subscription);
            for(Call call : callList) {
                OkHttpUtils.cancelCall(call);
            }

            //remove webServiceParamSubMap
            WebServiceParam param = getKeyByValue(webServiceParamSubMap, subscription);
            webServiceParamSubMap.remove(param);
            //remove urlSubMap
            String url = getKeyByValue(urlSubMap, subscription);
            urlSubMap.remove(url);
            subscriptionCallMap.remove(subscription);
            Log.d(TAG, "SubscriptionManager remove subscription");
            Log.d(TAG, "SubscriptionManager remove Request");
            Log.d(TAG, "urlSubMap size :" + urlSubMap.size());
            Log.d(TAG, "subscriptionCallMap size :" + subscriptionCallMap.size());
        }
    }

    public static void removeSubscription(Call call) {
        if(call != null) {
            Subscription subscription = null;
            for (Map.Entry<Subscription, List<Call>> entry : subscriptionCallMap.entrySet()) {
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

    public static boolean isContainUrl(String url) {
        return urlSubMap.containsKey(url);
    }
}