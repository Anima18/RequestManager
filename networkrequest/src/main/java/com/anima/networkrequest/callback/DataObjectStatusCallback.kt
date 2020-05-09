package com.anima.networkrequest.callback

import com.anima.networkrequest.entity.ViewModelStatus

/**
 * Created by jianjianhong on 19-11-7
 */
interface DataObjectStatusCallback<T> {
    fun onStatus(t: ViewModelStatus<T>)
}