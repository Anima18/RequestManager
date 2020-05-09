package com.anima.networkrequest

import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import com.anima.networkrequest.data.okhttp.OkHttpTask
import com.anima.networkrequest.data.okhttp.dataConvert.ResponseParser
import com.anima.networkrequest.exception.ExceptionUtil
import com.anima.networkrequest.indicator.IndicatorDialog
import kotlinx.coroutines.*

/**
 * Created by jianjianhong on 19-11-20
 */
class RequestStream<T> {

    companion object {
        fun create(context: Context): RequestStream<Any> {
            var requestStream: RequestStream<Any> = RequestStream()
            requestStream.context = context
            return requestStream
        }
    }

    enum class StreamType {
        SEQUENCE, PARALLEL
    }

    private var requestList: MutableList<NetworkRequest<out T>> = mutableListOf()
    private var context: Context? = null
    private var message: String? = null

    fun showMessage(message: String): RequestStream<T> {
        this.message = message
        return this
    }

    fun sequence(requests: List<NetworkRequest<out T>>): Collector<T> {
        requestList.addAll(requests)
        return Collector(context!!, message, StreamType.SEQUENCE, requestList)
    }

    fun sequence(vararg requests: NetworkRequest<out T>): Collector<T> {
        for (request in requests) {
            requestList.add(request)
        }
        return Collector(context!!, message, StreamType.SEQUENCE, requestList)
    }

    fun parallel(requests: List<NetworkRequest<out T>>): Collector<T> {
        requestList.addAll(requests)
        return Collector(context!!, message, StreamType.PARALLEL, requestList)
    }

    fun parallel(vararg requests: NetworkRequest<out T>): Collector<T> {
        for (request in requests) {
            requestList.add(request)
        }
        return Collector(context!!, message, StreamType.PARALLEL, requestList)
    }

    class Collector<T>(
        val context: Context,
        val message: String?,
        val streamType: StreamType,
        val requestList: MutableList<NetworkRequest<out T>>
    ) {

        private var processDialog: IndicatorDialog? = null

        fun collect(collectListener: OnCollectListener) {
            when (streamType) {
                StreamType.SEQUENCE -> sequenceCollect(collectListener)
                StreamType.PARALLEL -> parallelCollect(collectListener)
            }
        }

        private fun parallelCollect(collectListener: OnCollectListener) {
            processDialog = showRequestIndicator()
            var deferredList = mutableListOf<Deferred<ResponseParser>>()
            for (request in requestList) {
                val deferred = MainScope().async { OkHttpTask.create().dataTask<T>(request.param) }
                deferredList.add(deferred)
            }

            var dataList: MutableList<T> = mutableListOf()
            val job = MainScope().launch {
                try {
                    for (deferred in deferredList) {
                        val responseParser = deferred.await()
                        val data = responseParser.getResult()!! as T
                        dataList.add(data)
                    }

                    GlobalScope.launch(Dispatchers.Main) {
                        processDialog?.hideProgress()
                        collectListener.onSuccess(dataList)
                    }
                } catch (e: Exception) {
                    val message = ExceptionUtil.analysisException(e)
                    GlobalScope.launch(Dispatchers.Main) {
                        processDialog?.hideProgress()
                        message?.let { collectListener.onFailure(it) }
                    }
                }
            }

            onCancelListener(processDialog, job)
        }

        private fun sequenceCollect(collectListener: OnCollectListener) {
            var dataList: MutableList<T> = mutableListOf()
            processDialog = showRequestIndicator()
            val job = MainScope().launch {
                try {
                    for (request in requestList) {
                        val responseParser = OkHttpTask.create().dataTask<T>(request.param)
                        val data = responseParser.getResult()!! as T
                        dataList.add(data)
                    }

                    GlobalScope.launch(Dispatchers.Main) {
                        processDialog?.hideProgress()
                        collectListener.onSuccess(dataList)
                    }
                } catch (e: Exception) {
                    val message = ExceptionUtil.analysisException(e)
                    GlobalScope.launch(Dispatchers.Main) {
                        processDialog?.hideProgress()
                        message?.let { collectListener.onFailure(it) }
                    }
                }
            }
            onCancelListener(processDialog, job)
        }

        private fun showRequestIndicator(): IndicatorDialog? {
            var processDialog = message?.let {
                IndicatorDialog(context, message!!, ProgressDialog.STYLE_SPINNER, null)
            }
            processDialog?.showProgress()
            return processDialog
        }

        private fun onCancelListener(progressDialog: IndicatorDialog?, job: Job?) {
            progressDialog?.progressDialog?.setOnCancelListener(DialogInterface.OnCancelListener {
                job?.cancel()
            })
        }
    }

    interface OnCollectListener {
        fun onSuccess(dataList: List<*>)
        fun onFailure(message: String)
    }

}