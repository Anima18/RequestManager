package com.anima.networkrequest.callback

/**
 * Created by jianjianhong on 19-11-7
 */
interface DataListCallback<T> {
    fun onSuccess(t: List<T>?)
    fun onFailure(message: String)
}