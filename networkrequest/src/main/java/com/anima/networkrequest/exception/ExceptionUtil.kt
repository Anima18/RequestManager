package com.anima.networkrequest.exception

import com.google.gson.JsonSyntaxException
import kotlinx.coroutines.CancellationException
import java.io.FileNotFoundException
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Created by jianjianhong on 19-11-21
 */
object ExceptionUtil {
    fun analysisException(e: Exception): String? {
        return when(e) {
            is FileNotFoundException -> "文件找不到"
            is UnknownHostException -> "请求的服务器地址不存在"
            is SocketTimeoutException -> "服务器无响应，访问超时"
            is ConnectException -> "连接服务器失败"
            is IOException -> "连接服务器失败"
            is JsonSyntaxException -> "Json解析有误,请重试或者联系系统负责人"
            is InterruptedException -> "你取消了请求"
            is CancellationException -> "你取消了请求"
            is RequestErrorException -> when(e.code) {
                in 300..400 -> "请求重定向"
                in 400..500 -> "请求包含语法错误或者请求无法实现"
                in 500..600 -> "服务器遇到错误，无法完成请求"
                else -> e.message
            }
            else -> e.message
        }
    }
}