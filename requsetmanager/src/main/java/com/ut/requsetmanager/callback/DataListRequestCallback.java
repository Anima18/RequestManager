package com.ut.requsetmanager.callback;

import com.ut.requsetmanager.entity.ResponseStatus;

import java.util.List;

/**
 * Created by Admin on 2017/11/26.
 */

public interface DataListRequestCallback<T> {
    void onResult(List<T> dataList, ResponseStatus status);
}
