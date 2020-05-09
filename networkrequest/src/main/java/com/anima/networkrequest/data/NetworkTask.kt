package com.anima.networkrequest.data

import com.anima.networkrequest.callback.DataDownloadCallback
import com.anima.networkrequest.data.okhttp.dataConvert.ResponseParser
import com.anima.networkrequest.entity.RequestParam

/**
 * Created by jianjianhong on 19-11-1
 */
interface NetworkTask {
    suspend fun <T> dataTask(param: RequestParam): ResponseParser
    suspend fun downloadTask(param: RequestParam, callBack: DataDownloadCallback): String
    suspend fun <T> uploadTask(param: RequestParam, callBack: DataDownloadCallback): ResponseParser
}