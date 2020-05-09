package com.anima.networkrequest.callback

/**
 * Created by jianjianhong on 19-11-7
 */
interface DataObjectCallback<T> {
    fun onSuccess(t: T?)
    fun onFailure(message: String)
}