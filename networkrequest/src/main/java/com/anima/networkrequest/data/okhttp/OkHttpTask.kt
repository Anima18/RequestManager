package com.anima.networkrequest.data.okhttp

import android.content.Context
import com.anima.networkrequest.exception.RequestErrorException
import com.anima.networkrequest.data.NetworkTask
import com.anima.networkrequest.data.okhttp.dataConvert.DataConvertFactory
import com.anima.networkrequest.data.okhttp.dataConvert.ResponseParser
import com.anima.networkrequest.entity.RequestParam
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Created by jianjianhong on 19-11-1
 */

private const val CONNECT_TIMEOUT: Long = 10
private const val READ_TIMEOUT: Long = 10
private const val WRITE_TIMEOUT: Long = 10
class OkHttpTask private constructor(context: Context): NetworkTask {
    private var client: OkHttpClient
    init {
        println("OkHttpTask init")
        client = OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(CookieInterceptor(context))
            .build()
    }

    companion object {
        @Volatile
        var instance: OkHttpTask? = null

        fun getInstance(context: Context): OkHttpTask {
            if (instance == null) {
                synchronized(OkHttpTask::class) {
                    if (instance == null) {
                        instance = OkHttpTask(context)
                    }
                }
            }
            return instance!!
        }
    }

    override suspend fun <T> dataTask(param: RequestParam): ResponseParser {
        val request = when(param.method) {
            RequestParam.Method.GET -> getRequest(param)
            RequestParam.Method.POST -> postRequest(param)
            else -> throw IllegalArgumentException("请求方式不能为空")
        }
        val call = client.newCall(request)
        return executeCall<T>(param, call)
    }

    private suspend fun <T> executeCall(param: RequestParam, call: Call) = suspendCancellableCoroutine<ResponseParser> { continuation ->
        continuation.invokeOnCancellation {
            call.cancel()
        }

        call.enqueue(object : okhttp3.Callback {
            override fun onFailure(call: Call, e: IOException) {
                continuation.resumeWithException(e)
            }

            override fun onResponse(call: Call, response: Response) {
                if(response.isSuccessful) {
                    response.body?.let {
                        try {
                            val bodyData = response.body!!.string()
                            val responseParser = DataConvertFactory.convert<T>(bodyData, param)!!
                            if(responseParser.isSuccess()) {
                                continuation.resume(responseParser)
                            }else{
                                continuation.resumeWithException(RequestErrorException(9999, responseParser.errorMessage()))
                            }

                        } catch (e: Exception) {
                            continuation.resumeWithException(e)
                        }
                    } ?: continuation.resumeWithException(NullPointerException("ResponseBody is null."))
                }else {
                    continuation.resumeWithException(RequestErrorException(response.code, ""))
                }

            }
        })
    }

    private fun getRequest(param: RequestParam): Request {
        return Request.Builder().url(param.url).build()
    }

    private fun postRequest(param: RequestParam): Request {
        val requestBody = getRequestBody(param)
        return Request.Builder()
            .url(param.url)
            .post(requestBody)
            .build()
    }

    private fun getRequestBody(param: RequestParam): RequestBody {
        val builder = FormBody.Builder()
        param.params?.forEach { builder.add(it.key, it.value) }

        return builder.build()
    }
}