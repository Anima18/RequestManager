package com.anima.networkrequest.data.okhttp

import com.anima.networkrequest.callback.DataDownloadCallback
import com.anima.networkrequest.data.NetworkTask
import com.anima.networkrequest.data.okhttp.CountingFileRequestBody.ProgressListener
import com.anima.networkrequest.data.okhttp.dataConvert.DataConvertFactory
import com.anima.networkrequest.data.okhttp.dataConvert.ResponseParser
import com.anima.networkrequest.entity.RequestParam
import com.anima.networkrequest.exception.RequestErrorException
import com.google.gson.Gson
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.*
import okhttp3.Headers.Companion.headersOf
import okhttp3.OkHttpClient
import java.io.*
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import okhttp3.RequestBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody


/**
 * Created by jianjianhong on 19-11-1
 */

private const val CONNECT_TIMEOUT: Long = 10
private const val READ_TIMEOUT: Long = 10
private const val WRITE_TIMEOUT: Long = 10
class OkHttpTask private constructor(): NetworkTask {
    private var client: OkHttpClient
    init {
        println("OkHttpTask init")
        client = OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            //.addInterceptor(CookieInterceptor(context))
            .build()
    }

    companion object {
        @Volatile
        var instance: OkHttpTask? = null

        fun create(): OkHttpTask {
            if (instance == null) {
                synchronized(OkHttpTask::class) {
                    if (instance == null) {
                        instance = OkHttpTask()
                    }
                }
            }
            return instance!!
        }
    }

    override suspend fun <T> dataTask(param: RequestParam): ResponseParser {
        val request = requestOf(param)
        val call = client.newCall(request)
        return executeCall<T>(param, call)
    }

    override suspend fun downloadTask(param: RequestParam, callBack: DataDownloadCallback): String {
        val request = requestOf(param)

        val progressListener = object : ProgressResponseBody.ProgressListener {
            override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {
                callBack.onProgress(
                    param.downloadFileName!!,
                    (100 * bytesRead / contentLength).toInt()
                )

            }
        }
        val client = OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .addNetworkInterceptor(object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): Response {
                    val originalResponse = chain.proceed(chain.request())!!
                    return originalResponse.newBuilder()
                        .body(ProgressResponseBody(originalResponse.body!!, progressListener))
                        .build()
                }
            })
            .build()

        Request.Builder().url(param.url).build()
        val call = client.newCall(request)
        return downloadCall(param, call)
    }

    override suspend fun <T> uploadTask(param: RequestParam, callBack: DataDownloadCallback): ResponseParser {
        val builder = MultipartBody.Builder().setType(MultipartBody.FORM)
        //MultipartBody必须有一个请求体，设置一个默认请求体
        builder.addFormDataPart("1", "1")
        param.params?.forEach { builder.addFormDataPart(it.key, it.value) }

        var totalFilesLength = 0L
        param.uploadFiles?.forEach {
            if(it.exists()) {
                totalFilesLength += it.length();
            }
        }

        var totalReadLength = 0L
        for (file in param.uploadFiles!!) {
            val fileBody = CountingFileRequestBody( file, "application/octet-stream",
                object : ProgressListener {
                    override fun onUpdate(bytesRead: Long, fileName: String) {
                        totalReadLength += bytesRead
                        callBack.onProgress(fileName, (100 * totalReadLength / totalFilesLength).toInt())
                    }
                })
            var fileName = URLEncoder.encode(file.name, "UTF-8")
            builder.addPart(
                headersOf(
                    "Content-Disposition",
                    "form-data; name=files; filename=\"$fileName\""
                ), fileBody
            )
        }

        val requestBody = builder.build()

        val request = Request.Builder()
            .url(param.url)
            .post(requestBody)
            .build()

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

    private suspend fun downloadCall(param: RequestParam, call: Call) = suspendCancellableCoroutine<String> { continuation ->
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
                            val dataStream = response.body!!.byteStream()
                            val input = BufferedInputStream(dataStream)
                            createDir(param.downloadFilePath!!)
                            val output = FileOutputStream(param.downloadFilePath + param.downloadFileName)

                            val data = ByteArray(1024)
                            var count = 0

                            count = input.read(data)
                            while (count != -1) {
                                output.write(data, 0, count)
                                count = input.read(data)
                            }
                            output.flush()
                            output.close()
                            input.close()
                            continuation.resume(param.downloadFilePath + param.downloadFileName)
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


    private fun requestOf(param: RequestParam): Request {
        return when(param.method) {
            RequestParam.Method.GET -> getRequest(param)
            RequestParam.Method.POST -> postRequest(param)
            else -> throw IllegalArgumentException("请求方式不能为空")
        }
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
        if (param.asJson) {
            return Gson().toJson(param.params).toRequestBody("application/json;charset=utf-8".toMediaType())
        }else {
            val builder = FormBody.Builder()
            param.params?.forEach { builder.add(it.key, it.value) }
            return builder.build()
        }

    }

    private fun createDir(destDirName: String): Boolean {
        var flag = false
        val dir = File(destDirName)
        if (!dir.exists()) {
            if (dir.mkdirs()) {
                flag = true
            }
        } else {
            flag = true
        }
        return flag
    }

}