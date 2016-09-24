package com.example.webserviceutil.callBack;

import java.util.List;

/**
 * 获取对象集合的回调接口，继承接口{@link CallBack}
 * @author 简建鸿 2016/6/2
 * @version 1.0
 */
public interface CollectionCallBack<T> extends CallBack {
    /**
     * 成功获取对象集合的回调方法
     * @param data 数据集合
     */
    void onSuccess(List<T> data);
}
