package com.anima.networkrequest

import com.anima.networkrequest.data.okhttp.dataConvert.ResponseParser
import com.anima.networkrequest.entity.RequestParam
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job

/**
 * Created by jianjianhong on 19-11-4
 */

interface IRequest<T> {


    fun url(url: String): IRequest<T>
    fun method(method: RequestParam.Method): IRequest<T>
    fun coroutineScope(scope: CoroutineScope): IRequest<T>
    fun dataClass(dataClass: Class<*>): IRequest<T>
    fun dataParser(parser: ResponseParser): IRequest<T>
    fun params(params: MutableMap<String, String>): IRequest<T>
    fun addParam(key: String, value: String): IRequest<T>
    fun dataFormat(format: RequestParam.DataFormat): IRequest<T>
    fun loadingMessage(message: String): IRequest<T>
    fun getObject(objectCallback: DataObjectCallback<T>): Job
    fun getList(callback: DataListCallback<T>): Job
    fun getPageData(callback: DataPagingCallback<T>): Job

    fun create(): NetworkRequest<T>
}