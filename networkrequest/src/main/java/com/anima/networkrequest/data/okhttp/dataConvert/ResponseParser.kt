package com.anima.networkrequest.data.okhttp.dataConvert

/**
 * Created by jianjianhong on 19-11-16
 */
interface ResponseParser {
    fun parser(body: String, clazz: Class<*>): ResponseParser
    fun getResult(): Any?
    fun getTotal(): Int
    fun isSuccess(): Boolean
    fun errorMessage(): String
}